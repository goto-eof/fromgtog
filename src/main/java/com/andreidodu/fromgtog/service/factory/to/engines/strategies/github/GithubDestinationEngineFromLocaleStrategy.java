package com.andreidodu.fromgtog.service.factory.to.engines.strategies.github;

import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.service.GitHubService;
import com.andreidodu.fromgtog.service.LocalService;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.AbstractStrategyCommon;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.ThreadSleepCommand;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.UpdateStatusCommand;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.github.common.GithubDestinationEngineCommon;
import com.andreidodu.fromgtog.service.impl.GitHubServiceImpl;
import com.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.type.RepoPrivacyType;
import com.andreidodu.fromgtog.util.ThreadUtil;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.CLONER_THREAD_NAME_PREFIX;
import static com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.CommandCommon.*;

public class GithubDestinationEngineFromLocaleStrategy extends AbstractStrategyCommon implements GithubDestinationEngineFromStrategy {
    public static final String GITHUB_URL = "https://github.com";
    Logger log = LoggerFactory.getLogger(GithubDestinationEngineFromLocaleStrategy.class);

    @Override
    public boolean accept(EngineType engineType) {
        return EngineType.LOCAL.equals(engineType);
    }

    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        if (StringUtils.equals(fromContext.rootPath(), toContext.rootPath())) {
            throw new IllegalArgumentException("Root paths cannot be the same");
        }

        new UpdateStatusCommand(buildUpdateStatusContext(engineContext.callbackContainer(), repositoryDTOList.size(), 0, "initializing the cloning process")).execute();


        List<String> pathList = repositoryDTOList.stream()
                .map(RepositoryDTO::getPath)
                .toList();

        GitHubService gitHubService = GitHubServiceImpl.getInstance();
        GitHub githubClient = gitHubService.retrieveGitHubClient(toContext.token());
        GHUser githubUser = gitHubService.retrieveGitHubMyself(githubClient);
        String tokenOwnerLogin = githubUser.getLogin();

        log.debug("tokenOwnerLogin: {}", tokenOwnerLogin);


        ThreadUtil threadUtil = ThreadUtil.getInstance();
        final ExecutorService executorService = threadUtil.createExecutor(CLONER_THREAD_NAME_PREFIX, engineContext.settingsContext().multithreadingEnabled());
        super.resetIndex();


        for (String path : pathList) {
            executorService.execute(() -> processItem(engineContext, path, githubClient, tokenOwnerLogin));
        }

        threadUtil.waitUntilShutDownCompleted(executorService);

        new UpdateStatusCommand(buildUpdateStatusContext(engineContext.callbackContainer(), 100, 0, "done")).execute();

        callbackContainer.setShouldStop().accept(true);
        return true;
    }


    private void processItem(EngineContext engineContext, String path, GitHub githubClient, String tokenOwnerLogin) {
        String repositoryName = new File(path).getName();
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();
        LocalService localService = LocalServiceImpl.getInstance();

        if (isShouldStopTheProcess(repositoryName, callbackContainer)) {
            completeTask(callbackContainer);
            return;
        }

        repositoryName = correctRepositoryName(repositoryName);

        if (isRemoteRepositoryAlreadyExists(GithubDestinationEngineCommon.buildRemoteExistsCheckInput(engineContext, tokenOwnerLogin, repositoryName))) {
            completeTask(callbackContainer);
            return;
        }

        callbackContainer.updateApplicationStatusMessage().accept("cloning " + repositoryName + " ...");
        callbackContainer.updateApplicationStatusMessage().accept("cloning repository: " + repositoryName);

        try {
            githubClient.createRepository(repositoryName).private_(RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy())).owner(tokenOwnerLogin).create();
        } catch (IOException e) {
            callbackContainer.updateApplicationStatusMessage().accept("unable to create repository: " + repositoryName);
            log.debug("unable to create repository: {}", repositoryName, e);
            completeTask(callbackContainer);
            return;
        }

        try {
            log.debug("pushing...");
            boolean result = localService.pushOnRemote(tokenOwnerLogin, toContext.token(), GITHUB_URL, repositoryName, tokenOwnerLogin, new File(path));
        } catch (IOException | GitAPIException | URISyntaxException e) {
            callbackContainer.updateApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
            log.error("Unable to push repository {}", repositoryName, e);
            completeTask(callbackContainer);
            return;
        }

        try {
            log.debug("updating repository privacy...");
            githubClient.getRepository(tokenOwnerLogin + "/" + repositoryName)
                    .setPrivate(RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()));
        } catch (IOException e) {
            callbackContainer.updateApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
            log.error("Unable to push repository {}", repositoryName, e);
            completeTask(callbackContainer);
            return;
        }


        completeTask(callbackContainer);


        if (!new ThreadSleepCommand(engineContext.settingsContext().sleepTimeSeconds()).execute()) {
            throw new RuntimeException("Unable to put thread on sleep " + repositoryName);
        }

    }

}
