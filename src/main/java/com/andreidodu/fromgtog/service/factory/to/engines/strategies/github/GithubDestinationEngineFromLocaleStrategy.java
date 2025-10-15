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
    public boolean isApplicable(EngineType engineType) {
        return EngineType.LOCAL.equals(engineType);
    }

    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();

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
        try (final ExecutorService executorService = threadUtil.createExecutor(CLONER_THREAD_NAME_PREFIX, engineContext.settingsContext().multithreadingEnabled())) {
            super.resetIndex();
            LocalService localService = LocalServiceImpl.getInstance();
            for (String path : pathList) {
                executorService.execute(() -> processItem(engineContext, path, gitHubService, githubClient, localService, tokenOwnerLogin));
            }

        }

        new UpdateStatusCommand(buildUpdateStatusContext(engineContext.callbackContainer(), pathList.size(), super.getIndex(), String.format("done%s", calculateStatus(pathList.size())))).execute();

        return super.getIndex() == pathList.size();
    }


    private void processItem(EngineContext engineContext, String path, GitHubService service, GitHub githubClient, LocalService localService, String tokenOwnerLogin) {
        String repositoryName = new File(path).getName();
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        if (isShouldStopTheProcess(repositoryName, callbackContainer)) {
            return;
        }

        repositoryName = correctRepositoryName(repositoryName);
        boolean isOverrideFlagEnabled = toContext.overrideIfExists();
        boolean isDestinationRepositoryAlreadyExists = isRemoteDestinationRepositoryAlreadyExists(GithubDestinationEngineCommon.buildRemoteDestinationExistsCheckInput(engineContext, tokenOwnerLogin, repositoryName));

        if (!isOverrideFlagEnabled && isDestinationRepositoryAlreadyExists) {
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Skipping repository because it already exists: " + repositoryName);
            incrementIndexSuccess(callbackContainer);
            return;
        }

        callbackContainer.updateLogAndApplicationStatusMessage().accept("cloning " + repositoryName + " ...");
        callbackContainer.updateLogAndApplicationStatusMessage().accept("cloning repository: " + repositoryName);


        try {
            if (!isDestinationRepositoryAlreadyExists) {
                callbackContainer.updateLogAndApplicationStatusMessage().accept("repository not found on destination platform: " + repositoryName);
                callbackContainer.updateLogAndApplicationStatusMessage().accept("I am going to create it: " + repositoryName);
                githubClient.createRepository(repositoryName).private_(RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy())).owner(tokenOwnerLogin).create();
                callbackContainer.updateLogAndApplicationStatusMessage().accept("Destination repo created: " + repositoryName);
            }
        } catch (IOException e) {
            callbackContainer.updateLogAndApplicationStatusMessage().accept("unable to create repository: " + repositoryName);
            log.debug("unable to create repository: {}", repositoryName, e);
            return;
        }

        if (isOverrideFlagEnabled) {
            String message = String.format("isOverrideFlagEnabled: executing git push --force %s", repositoryName);
            callbackContainer.updateLogAndApplicationStatusMessage().accept(message);
        }

        try {
            String message = String.format("pushing %s on %s...", repositoryName, toContext.url());
            callbackContainer.updateLogAndApplicationStatusMessage().accept(message);

            boolean isPushOk = localService.pushOnRemote(tokenOwnerLogin, toContext.token(), GITHUB_URL, repositoryName, tokenOwnerLogin, new File(path), isOverrideFlagEnabled);

            message = String.format("push status for repo %s: %S", repositoryName, isPushOk);
            callbackContainer.updateLogAndApplicationStatusMessage().accept(message);
        } catch (IOException | GitAPIException | URISyntaxException e) {
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
            log.error("Unable to push repository {}", repositoryName, e);
            return;
        }

        try {
            log.debug("updating repository privacy...");
            githubClient.getRepository(tokenOwnerLogin + "/" + repositoryName)
                    .setPrivate(RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()));
        } catch (IOException e) {
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
            log.error("Unable to push repository {}", repositoryName, e);
            return;
        }


        incrementIndexSuccess(callbackContainer);


        if (!new ThreadSleepCommand(engineContext.settingsContext().sleepTimeSeconds()).execute()) {
            throw new RuntimeException("Unable to put thread on sleep " + repositoryName);
        }

    }

}
