package com.andreidodu.fromgtog.service.factory.to.engines.strategies.github;

import com.andreidodu.fromgtog.config.NoHomeGitConfigSystemReader;
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
import org.apache.commons.io.FileUtils;
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

public class GithubDestinationEngineFromRemoteStrategy extends AbstractStrategyCommon implements GithubDestinationEngineFromStrategy {
    Logger log = LoggerFactory.getLogger(GithubDestinationEngineFromRemoteStrategy.class);

    @Override
    public boolean isApplicable(EngineType engineType) {
        return List.of(EngineType.GITHUB, EngineType.GITEA, EngineType.GITLAB).contains(engineType);
    }


    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        GitHubService gitHubService = GitHubServiceImpl.getInstance();
        GitHub githubClient = gitHubService.retrieveGitHubClient(toContext.token());
        GHUser githubUser = gitHubService.retrieveGitHubMyself(githubClient);
        String tokenOwnerLogin = githubUser.getLogin();


        new UpdateStatusCommand(buildUpdateStatusContext(engineContext.callbackContainer(), repositoryDTOList.size(), 0, "initializing the cloning process")).execute();

        ThreadUtil threadUtil = ThreadUtil.getInstance();
        try (final ExecutorService executorService = threadUtil.createExecutor(CLONER_THREAD_NAME_PREFIX, engineContext.settingsContext().multithreadingEnabled())) {
            super.resetIndex();
            NoHomeGitConfigSystemReader.install();

            for (RepositoryDTO repositoryDTO : repositoryDTOList) {
                executorService.execute(() -> processItem(engineContext, repositoryDTO, githubClient, tokenOwnerLogin));
            }

        }

        new UpdateStatusCommand(buildUpdateStatusContext(engineContext.callbackContainer(), repositoryDTOList.size(), super.getIndex(), String.format("done%s", calculateStatus(repositoryDTOList.size())))).execute();

        callbackContainer.setShouldStop().accept(true);

        return super.getIndex() == repositoryDTOList.size();
    }

    private void processItem(EngineContext engineContext, RepositoryDTO repositoryDTO, GitHub githubClient, String tokenOwnerLogin) {
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();
        String repositoryName = repositoryDTO.getName();
        LocalService localService = LocalServiceImpl.getInstance();
        String fromContextToken = fromContext.token();
        final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
        if (isShouldStopTheProcess(repositoryName, callbackContainer)) {
            return;
        }

        callbackContainer.updateApplicationStatusMessage().accept("cloning repository: " + repositoryName);

        if (isRemoteRepositoryAlreadyExists(GithubDestinationEngineCommon.buildRemoteExistsCheckInput(engineContext, tokenOwnerLogin, repositoryName))) {
            incrementIndexSuccess(callbackContainer);
            return;
        }

        String stagedClonePath = TEMP_DIRECTORY + File.separator + repositoryName;

        try {
            log.debug("local cloning path: {}", stagedClonePath);
            log.debug("from url: {}", repositoryDTO.getCloneAddress());
            FileUtils.deleteDirectory(new File(stagedClonePath));
            boolean result = localService.clone(tokenOwnerLogin, fromContextToken, repositoryDTO.getCloneAddress(), stagedClonePath);
        } catch (Exception e) {
            callbackContainer.updateApplicationStatusMessage().accept("Unable to clone repository " + repositoryName);
            log.error("Unable to clone repository {}", repositoryName, e);
            return;
        }


        callbackContainer.updateApplicationStatusMessage().accept("cloning " + repositoryName + " ...");
        callbackContainer.updateApplicationStatusMessage().accept("cloning repository: " + repositoryName);

        try {
            githubClient.createRepository(repositoryName).private_(RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy())).owner(tokenOwnerLogin).create();
        } catch (IOException e) {
            callbackContainer.updateApplicationStatusMessage().accept("unable to create repository: " + repositoryName);
            log.debug("unable to create repository: {}", repositoryName, e);
            return;
        }

        try {
            log.debug("pushing...");
            boolean result = localService.pushOnRemote(tokenOwnerLogin, toContext.token(), "https://github.com", repositoryName, tokenOwnerLogin, new File(stagedClonePath));
        } catch (IOException | GitAPIException | URISyntaxException e) {
            callbackContainer.updateApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
            log.error("Unable to push repository {}", repositoryName, e);
            return;
        }

        try {
            log.debug("updating repository privacy...");
            githubClient.getRepository(tokenOwnerLogin + "/" + repositoryName).setPrivate(RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()));
        } catch (IOException e) {
            callbackContainer.updateApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
            log.error("Unable to push repository {}", repositoryName, e);
            return;
        }

        incrementIndexSuccess(callbackContainer);


        if (!new ThreadSleepCommand(engineContext.settingsContext().sleepTimeSeconds()).execute()) {
            throw new RuntimeException("Unable to put thread on sleep " + repositoryName);
        }

    }
}
