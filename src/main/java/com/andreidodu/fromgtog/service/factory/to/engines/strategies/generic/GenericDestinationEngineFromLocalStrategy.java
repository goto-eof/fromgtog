package com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic;

import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.service.LocalService;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.AbstractStrategyCommon;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.ThreadSleepCommand;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.UpdateStatusCommand;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.RemoteExistsCheckCommandContext;
import com.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.type.RepoPrivacyType;
import com.andreidodu.fromgtog.util.ThreadUtil;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.CLONER_THREAD_NAME_PREFIX;
import static com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.CommandCommon.*;

public class GenericDestinationEngineFromLocalStrategy<ServiceType extends GenericDestinationEngineFromStrategyService> extends AbstractStrategyCommon implements GenericDestinationEngineFromStrategyCommon {
    private Logger log = LoggerFactory.getLogger(GenericDestinationEngineFromLocalStrategy.class);

    private final ServiceType service;

    public GenericDestinationEngineFromLocalStrategy(ServiceType service) {
        this.service = service;
    }

    @Override
    public boolean accept(EngineType engineType) {
        return EngineType.LOCAL.equals(engineType);
    }

    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        validateInput(fromContext, toContext);


        new UpdateStatusCommand(buildUpdateStatusContext(engineContext.callbackContainer(), repositoryDTOList.size(), 0, "initializing the cloning process")).execute();


        List<String> pathList = repositoryDTOList.stream()
                .map(RepositoryDTO::getPath)
                .toList();

        String tokenOwnerLogin = service.getLogin(toContext.token(), toContext.url());

        log.debug("tokenOwnerLogin: {}", tokenOwnerLogin);


        ThreadUtil threadUtil = ThreadUtil.getInstance();

        try (final ExecutorService executorService = threadUtil.createExecutor(CLONER_THREAD_NAME_PREFIX, engineContext.settingsContext().multithreadingEnabled())) {
            super.resetIndex();

            for (String path : pathList) {
                executorService.execute(() -> processItem(engineContext, path, tokenOwnerLogin));
            }

            threadUtil.waitUntilShutDownCompleted(executorService);

            new UpdateStatusCommand(buildUpdateStatusContext(engineContext.callbackContainer(), pathList.size(), super.getIndex(), String.format("done%s", calculateStatus(pathList.size())))).execute();

            callbackContainer.setShouldStop().accept(true);
            return true;
        }
    }

    private void processItem(EngineContext engineContext, String path, String tokenOwnerLogin) {
        String repositoryName = new File(path).getName();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();
        LocalService localService = LocalServiceImpl.getInstance();

        if (isShouldStopTheProcess(repositoryName, callbackContainer)) {
            return;
        }

        repositoryName = super.correctRepositoryName(repositoryName);
        log.debug("toDirectoryPath: {}", path);


        RemoteExistsCheckCommandContext remoteExistsCheckCommandContext = GenericDestinationEngineCommon.buildRemoteExistsCheckInput(engineContext, tokenOwnerLogin, repositoryName);
        if (isRemoteRepositoryAlreadyExists(remoteExistsCheckCommandContext)) {
            incrementIndexSuccess(callbackContainer);
            return;
        }

        String remoteRepositoryUrl = toContext.url() + "/" + tokenOwnerLogin + "/" + repositoryName + ".git";
        if (localService.isRemoteRepositoryExists(tokenOwnerLogin, toContext.token(), remoteRepositoryUrl)) {
            log.debug("skipping because {} already exists", repositoryName);
            callbackContainer.updateApplicationStatusMessage().accept("Skipping repository because it already exists: " + repositoryName);
            incrementIndexSuccess(callbackContainer);
            return;
        }

        callbackContainer.updateApplicationStatusMessage().accept("cloning " + repositoryName + " ...");
        callbackContainer.updateApplicationStatusMessage().accept("cloning repository: " + repositoryName);

        try {
            if (!service.createRepository(toContext.url(), toContext.token(), repositoryName, "", RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()))) {
                callbackContainer.updateApplicationStatusMessage().accept("unable to create repository: " + repositoryName);
                log.debug("unable to create repository: {}", repositoryName);
                return;
            }
        } catch (Exception e) {
            callbackContainer.updateApplicationStatusMessage().accept("unable to create repository: " + repositoryName);
            log.debug("unable to create repository: {}", repositoryName, e);
            return;
        }

        try {
            log.debug("pushing...");
            boolean isPushOk = localService.pushOnRemote(tokenOwnerLogin, toContext.token(), toContext.url(), repositoryName, tokenOwnerLogin, new File(path));
        } catch (IOException | GitAPIException | URISyntaxException e) {
            callbackContainer.updateApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
            log.error("Unable to push repository {}", repositoryName, e);
            return;
        }

        try {
            log.debug("updating repository privacy...");
            boolean result = service.updateRepositoryPrivacy(toContext.token(), tokenOwnerLogin, toContext.url(), repositoryName, false, RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()));
        } catch (Exception e) {
            callbackContainer.updateApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
            log.error("Unable to push repository {}", repositoryName, e);
            return;
        }

        incrementIndexSuccess(callbackContainer);

        if (!new ThreadSleepCommand(engineContext.settingsContext().sleepTimeSeconds()).execute()) {
            throw new RuntimeException("Unable to put thread on sleep " + repositoryName);
        }
    }


    private static void validateInput(FromContext fromContext, ToContext toContext) {
        if (StringUtils.equals(fromContext.rootPath(), toContext.rootPath())) {
            throw new IllegalArgumentException("Root paths cannot be the same");
        }
    }


}
