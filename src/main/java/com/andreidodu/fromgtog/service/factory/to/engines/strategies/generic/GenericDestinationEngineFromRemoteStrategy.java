package com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic;

import com.andreidodu.fromgtog.config.NoHomeGitConfigSystemReader;
import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.service.LocalService;
import com.andreidodu.fromgtog.service.factory.Engine;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.AbstractStrategyCommon;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.ThreadSleepCommand;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.UpdateStatusCommand;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.RemoteExistsCheckCommandContext;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.StatusCommandContext;
import com.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.type.RepoPrivacyType;
import com.andreidodu.fromgtog.util.ThreadUtil;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.CommandCommon.*;

public class GenericDestinationEngineFromRemoteStrategy<ServiceType extends GenericDestinationEngineFromStrategyService> extends AbstractStrategyCommon implements GenericDestinationEngineFromStrategyCommon {
    Logger log = LoggerFactory.getLogger(GenericDestinationEngineFromRemoteStrategy.class);
    private final ServiceType service;

    public GenericDestinationEngineFromRemoteStrategy(ServiceType service) {
        this.service = service;
    }

    @Override
    public boolean accept(EngineType engineType) {
        return List.of(EngineType.GITHUB, EngineType.GITEA, EngineType.GITLAB).contains(engineType);
    }

    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        CallbackContainer callbackContainer = engineContext.callbackContainer();
        ToContext toContext = engineContext.toContext();

        String login = service.getLogin(toContext.token(), toContext.url());

        StatusCommandContext initializingTheCloningProcessInput = buildUpdateStatusContext(engineContext.callbackContainer(), repositoryDTOList.size(), 0, "initializing the cloning process");
        new UpdateStatusCommand(initializingTheCloningProcessInput).execute();


        ThreadUtil threadUtil = ThreadUtil.getInstance();
        final ExecutorService executorService = threadUtil.createExecutor(engineContext.settingsContext().multithreadingEnabled());
        super.resetIndex();
        NoHomeGitConfigSystemReader.install();

        for (RepositoryDTO repositoryDTO : repositoryDTOList) {
            executorService.execute(() -> processItem(engineContext, repositoryDTO, login));
        }

        threadUtil.waitUntilShutDownCompleted(executorService);

        new UpdateStatusCommand(buildUpdateStatusContext(engineContext.callbackContainer(), 100, 0, "done")).execute();

        callbackContainer.setShouldStop().accept(true);
        return true;
    }

    private void processItem(EngineContext engineContext, RepositoryDTO repositoryDTO, String login) {
        String repositoryName = repositoryDTO.getName();
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");

        LocalService localService = LocalServiceImpl.getInstance();
        if (isShouldStopTheProcess(repositoryName, callbackContainer)) {
            completeTask(callbackContainer);
            return;
        }

        callbackContainer.updateApplicationStatusMessage().accept("cloning repository: " + repositoryName);

        RemoteExistsCheckCommandContext remoteExistsCheckCommandContext = GenericDestinationEngineCommon.buildRemoteExistsCheckInput(engineContext, login, repositoryName);
        if (isRemoteRepositoryAlreadyExists(remoteExistsCheckCommandContext)) {
            completeTask(callbackContainer);
            return;
        }

        String stagedClonePath = TEMP_DIRECTORY + File.separator + repositoryName;
        try {
            log.debug("local cloning path: {}", stagedClonePath);
            log.debug("from url: {}", repositoryDTO.getCloneAddress());
            FileUtils.deleteDirectory(new File(stagedClonePath));
            boolean result = localService.clone(fromContext.login(), fromContext.token(), repositoryDTO.getCloneAddress(), stagedClonePath);
        } catch (Exception e) {
            callbackContainer.updateApplicationStatusMessage().accept("Unable to clone repository " + repositoryName);
            log.error("Unable to clone repository {}", repositoryName, e);
            completeTask(callbackContainer);
            return;
        }


        callbackContainer.updateApplicationStatusMessage().accept("cloning " + repositoryName + " ...");
        callbackContainer.updateApplicationStatusMessage().accept("cloning repository: " + repositoryName);

        try {
            log.debug("creating repository {}", repositoryName);
            boolean repositoryCreationResult = service.createRepository(toContext.url(), toContext.token(), repositoryName, "", RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()));
            log.debug("repository {} created: {}", repositoryName, repositoryCreationResult);
            log.debug("pushing...");
            boolean pushResult = localService.pushOnRemote(login, toContext.token(), toContext.url(), repositoryName, login, new File(stagedClonePath));
            log.debug("pushed {}: {}", repositoryName, pushResult);
        } catch (IOException | GitAPIException | URISyntaxException | InterruptedException e) {
            callbackContainer.updateApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
            log.error("Unable to push repository {}", repositoryName, e);
            completeTask(callbackContainer);
            return;
        }

        try {
            log.debug("updating repository privacy...");
            boolean result = service.updateRepositoryPrivacy(toContext.token(), login, toContext.url(), repositoryName, false, RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()));
        } catch (Exception e) {
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
