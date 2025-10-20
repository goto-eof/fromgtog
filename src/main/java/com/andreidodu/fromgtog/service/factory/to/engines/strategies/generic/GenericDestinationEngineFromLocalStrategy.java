package com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic;

import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.service.DeletableDestinationContentService;
import com.andreidodu.fromgtog.service.LocalService;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.AbstractStrategyCommon;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.ThreadSleepCommand;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.UpdateStatusCommand;
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
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.CLONER_THREAD_NAME_PREFIX;
import static com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.CommandCommon.buildUpdateStatusContext;
import static com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.CommandCommon.isShouldStopTheProcess;

public class GenericDestinationEngineFromLocalStrategy<ServiceType extends DeletableDestinationContentService> extends AbstractStrategyCommon implements GenericDestinationEngineFromStrategyCommon {
    private final ServiceType service;
    private final Logger log = LoggerFactory.getLogger(GenericDestinationEngineFromLocalStrategy.class);

    public GenericDestinationEngineFromLocalStrategy(ServiceType service) {
        this.service = service;
    }

    private static void validateInput(FromContext fromContext, ToContext toContext) {
        if (StringUtils.equals(fromContext.rootPath(), toContext.rootPath())) {
            throw new IllegalArgumentException("Root paths cannot be the same");
        }
    }

    @Override
    public boolean isApplicable(EngineType engineType) {
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

        String destinationTokenOwnerLogin = service.getLogin(toContext.token(), toContext.url());

        log.debug("destinationTokenOwnerLogin: {}", destinationTokenOwnerLogin);


        ThreadUtil threadUtil = ThreadUtil.getInstance();

        try (final ExecutorService executorService = threadUtil.createExecutor(CLONER_THREAD_NAME_PREFIX, engineContext.settingsContext().multithreadingEnabled())) {
            super.resetIndex();
            LocalService localService = LocalServiceImpl.getInstance();
            for (String path : pathList) {
                executorService.execute(() -> processItem(engineContext, path, destinationTokenOwnerLogin, localService));
            }
        }

        new UpdateStatusCommand(buildUpdateStatusContext(engineContext.callbackContainer(), pathList.size(), super.getIndex(), String.format("done%s", calculateStatus(pathList.size())))).execute();

        return super.getIndex() == pathList.size();
    }

    private void processItem(EngineContext engineContext, String path, String destinationTokenOwnerLogin, LocalService localService) {
        String repositoryName = new File(path).getName();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        if (isShouldStopTheProcess(repositoryName, callbackContainer)) {
            return;
        }

        repositoryName = super.correctRepositoryName(repositoryName);
        log.debug("toDirectoryPath: {}", path);

        String remoteRepositoryUrl = toContext.url() + "/" + destinationTokenOwnerLogin + "/" + repositoryName + ".git";

        boolean isOverrideFlagEnabled = toContext.overrideIfExists();
        boolean isDestinationRepositoryExists = localService.isRemoteRepositoryExists(destinationTokenOwnerLogin, toContext.token(), remoteRepositoryUrl);

        if (!isOverrideFlagEnabled && isDestinationRepositoryExists) {
            log.debug("skipping because {} already exists", repositoryName);
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Skipping repository because it already exists: " + repositoryName);
            incrementIndexSuccess(callbackContainer);
            return;

        }
        callbackContainer.updateLogAndApplicationStatusMessage().accept("cloning " + repositoryName + " ...");
        callbackContainer.updateLogAndApplicationStatusMessage().accept("cloning repository: " + repositoryName);


        try {

            if (!isDestinationRepositoryExists) {
                callbackContainer.updateLogAndApplicationStatusMessage().accept("repository not found on destination platform: " + repositoryName);
                callbackContainer.updateLogAndApplicationStatusMessage().accept("I am going to create it: " + repositoryName);
            }

            if (!isDestinationRepositoryExists && !service.createRepository(toContext.url(), toContext.token(), repositoryName, "", RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()))) {
                callbackContainer.updateLogAndApplicationStatusMessage().accept("unable to create repository: " + repositoryName);
                log.debug("unable to create repository: {}", repositoryName);
                return;
            }

            callbackContainer.updateLogAndApplicationStatusMessage().accept("Destination repo created: " + repositoryName);

        } catch (Exception e) {
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

            boolean isPushOk = localService.pushOnRemote(destinationTokenOwnerLogin, toContext.token(), toContext.url(), repositoryName, destinationTokenOwnerLogin, new File(path), isOverrideFlagEnabled, false);

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
            boolean result = service.updateRepositoryPrivacy(toContext.token(), destinationTokenOwnerLogin, toContext.url(), repositoryName, false, RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()));
        } catch (Exception e) {
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
            log.error("Unable to push repository {}", repositoryName, e);
            return;
        }

        incrementIndexSuccess(callbackContainer);

        if (!new ThreadSleepCommand(engineContext.settingsContext().sleepTimeSeconds()).execute()) {
            throw new RuntimeException("Unable to put thread on sleep " + repositoryName);
        }
    }

    private static DeleteRepositoryRequestDTO buildDestinationDeleteRepositoryRequestDTO(ToContext
                                                                                                 toContext, String repositoryName) {
        return new DeleteRepositoryRequestDTO(Optional.of(toContext.token()), Optional.of(toContext.url()), Optional.of(toContext.token()), Optional.of(repositoryName), Optional.empty());
    }


}
