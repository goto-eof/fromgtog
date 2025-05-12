package com.andreidodu.fromgtog.service.factory.to.engines.strategies.github;

import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.service.GitHubService;
import com.andreidodu.fromgtog.service.LocalService;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.ThreadSleepCommand;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.UpdateStatusCommand;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.github.common.GithubDestinationEngineCommon;
import com.andreidodu.fromgtog.service.impl.GitHubServiceImpl;
import com.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.type.RepoPrivacyType;
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

import static com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.CommandCommon.*;

public class GithubDestinationEngineFromRemoteStrategy implements GithubDestinationEngineFromStrategy {
    Logger log = LoggerFactory.getLogger(GithubDestinationEngineFromRemoteStrategy.class);

    @Override
    public boolean accept(EngineType engineType) {
        return List.of(EngineType.GITHUB, EngineType.GITEA, EngineType.GITLAB).contains(engineType);
    }


    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");

        LocalService localService = LocalServiceImpl.getInstance();

        GitHubService gitHubService = GitHubServiceImpl.getInstance();
        GitHub githubClient = gitHubService.retrieveGitHubClient(toContext.token());
        GHUser githubUser = gitHubService.retrieveGitHubMyself(githubClient);
        String tokenOwnerLogin = githubUser.getLogin();
        String fromContextToken = fromContext.token();

        new UpdateStatusCommand(buildUpdateStatusContext(engineContext.callbackContainer(), repositoryDTOList.size(), 0, "initializing the cloning process")).execute();


        int i = 0;
        for (RepositoryDTO repositoryDTO : repositoryDTOList) {
            String repositoryName = repositoryDTO.getName();

            if (isShouldStopTheProcess(repositoryName, callbackContainer)) {
                break;
            }

            callbackContainer.updateApplicationProgressBarCurrent().accept(i++);
            callbackContainer.updateApplicationStatusMessage().accept("cloning repository: " + repositoryName);

            if (isRemoteRepositoryAlreadyExists(GithubDestinationEngineCommon.buildRemoteExistsCheckInput(engineContext, tokenOwnerLogin, repositoryName))) {
                continue;
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
                continue;
            }


            callbackContainer.updateApplicationStatusMessage().accept("cloning " + repositoryName + " ...");
            callbackContainer.updateApplicationStatusMessage().accept("cloning repository: " + repositoryName);

            try {
                githubClient.createRepository(repositoryName).private_(RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy())).owner(tokenOwnerLogin).create();
            } catch (IOException e) {
                callbackContainer.updateApplicationStatusMessage().accept("unable to create repository: " + repositoryName);
                log.debug("unable to create repository: {}", repositoryName, e);
                continue;
            }

            try {
                log.debug("pushing...");
                boolean result = localService.pushOnRemote(tokenOwnerLogin, toContext.token(), "https://github.com", repositoryName, tokenOwnerLogin, new File(stagedClonePath));
            } catch (IOException | GitAPIException | URISyntaxException e) {
                callbackContainer.updateApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
                log.error("Unable to push repository {}", repositoryName, e);
                continue;
            }


            try {
                log.debug("updating repository privacy...");
                githubClient.getRepository(tokenOwnerLogin + "/" + repositoryName).setPrivate(RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()));
            } catch (IOException e) {
                callbackContainer.updateApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
                log.error("Unable to push repository {}", repositoryName, e);
                continue;
            }

            if (!new ThreadSleepCommand(engineContext.settingsContext().sleepTimeSeconds()).execute()) {
                throw new RuntimeException("Unable to put thread on sleep " + repositoryName);
            }

        }
        new UpdateStatusCommand(buildUpdateStatusContext(engineContext.callbackContainer(), 100, 0, "done")).execute();

        callbackContainer.setShouldStop().accept(true);

        return true;
    }
}
