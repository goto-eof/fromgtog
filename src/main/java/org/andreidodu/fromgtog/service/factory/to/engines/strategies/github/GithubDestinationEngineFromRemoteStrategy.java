package org.andreidodu.fromgtog.service.factory.to.engines.strategies.github;

import org.andreidodu.fromgtog.config.NoHomeGitConfigSystemReader;
import org.andreidodu.fromgtog.dto.*;
import org.andreidodu.fromgtog.dto.*;
import org.andreidodu.fromgtog.service.GitHubService;
import org.andreidodu.fromgtog.service.LocalService;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.AbstractStrategyCommon;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.ThreadSleepCommand;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.UpdateStatusCommand;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.github.common.GithubDestinationEngineCommon;
import org.andreidodu.fromgtog.service.impl.GitHubServiceImpl;
import org.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import org.andreidodu.fromgtog.type.EngineType;
import org.andreidodu.fromgtog.type.RepoPrivacyType;
import org.andreidodu.fromgtog.util.ApplicationUtil;
import org.andreidodu.fromgtog.util.ThreadUtil;
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
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static org.andreidodu.fromgtog.constants.ApplicationConstants.CLONER_THREAD_NAME_PREFIX;
import static org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.CommandCommon.*;

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
            LocalService localService = LocalServiceImpl.getInstance();
            for (RepositoryDTO repositoryDTO : repositoryDTOList) {
                executorService.execute(() -> processItem(engineContext, repositoryDTO, gitHubService, githubClient, localService, tokenOwnerLogin));
            }

        }

        new UpdateStatusCommand(buildUpdateStatusContext(engineContext.callbackContainer(), repositoryDTOList.size(), super.getIndex(), String.format("done%s", calculateStatus(repositoryDTOList.size())))).execute();

        return super.getIndex() == repositoryDTOList.size();
    }

    private void processItem(EngineContext engineContext, RepositoryDTO repositoryDTO, GitHubService service, GitHub githubClient, LocalService localService, String tokenOwnerLogin) {
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();
        String repositoryName = repositoryDTO.getName();
        String fromContextToken = fromContext.token();
        final String TEMP_DIRECTORY = ApplicationUtil.getTemporaryFolderName();
        if (isShouldStopTheProcess(repositoryName, callbackContainer)) {
            return;
        }

        callbackContainer.updateLogAndApplicationStatusMessage().accept("cloning repository: " + repositoryName);
        boolean isOverrideFlagEnabled = toContext.overrideIfExists();
        boolean isDestinationRepositoryAlreadyExists = isRemoteDestinationRepositoryAlreadyExists(GithubDestinationEngineCommon.buildRemoteDestinationExistsCheckInput(engineContext, tokenOwnerLogin, repositoryName));

        if (!isOverrideFlagEnabled && isDestinationRepositoryAlreadyExists) {
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
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Unable to clone repository " + repositoryName);
            log.error("Unable to clone repository {}", repositoryName, e);
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
            String message = String.format("pushing %s on %s...", repositoryName, "GitHub");
            callbackContainer.updateLogAndApplicationStatusMessage().accept(message);

            boolean isPushOk = localService.pushOnRemote(tokenOwnerLogin, toContext.token(), "https://github.com", repositoryName, tokenOwnerLogin, new File(stagedClonePath), isOverrideFlagEnabled, true);


            message = String.format("push status for repo %s: %S", repositoryName, isPushOk);
            callbackContainer.updateLogAndApplicationStatusMessage().accept(message);

            if (!isPushOk) {
                throw new RuntimeException(String.format("push status for repo %s: %S", repositoryName, isPushOk));
            }
        } catch (IOException | GitAPIException | URISyntaxException | RuntimeException e) {
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
            log.error("Unable to push repository {}", repositoryName, e);
            return;
        }

        try {
            log.debug("updating repository privacy...");
            githubClient.getRepository(tokenOwnerLogin + "/" + repositoryName).setPrivate(RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()));
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

    private static DeleteRepositoryRequestDTO buildDestinationDeleteRepositoryRequestDTO(String tokenOwnerLogin, ToContext toContext, String repositoryName) {
        return new DeleteRepositoryRequestDTO(Optional.of(toContext.token()), Optional.of(toContext.url()), Optional.of(tokenOwnerLogin), Optional.of(repositoryName), Optional.empty());
    }
}
