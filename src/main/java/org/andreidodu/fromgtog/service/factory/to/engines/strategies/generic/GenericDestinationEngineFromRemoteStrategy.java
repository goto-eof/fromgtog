package org.andreidodu.fromgtog.service.factory.to.engines.strategies.generic;

import org.andreidodu.fromgtog.config.NoHomeGitConfigSystemReader;
import org.andreidodu.fromgtog.dto.*;
import org.andreidodu.fromgtog.dto.*;
import org.andreidodu.fromgtog.service.DeletableDestinationContentService;
import org.andreidodu.fromgtog.service.LocalService;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.AbstractStrategyCommon;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.ThreadSleepCommand;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.UpdateStatusCommand;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.RemoteExistsCheckCommandContext;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.StatusCommandContext;
import org.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import org.andreidodu.fromgtog.type.EngineType;
import org.andreidodu.fromgtog.type.RepoPrivacyType;
import org.andreidodu.fromgtog.util.ApplicationUtil;
import org.andreidodu.fromgtog.util.ThreadUtil;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
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

public class GenericDestinationEngineFromRemoteStrategy<ServiceType extends DeletableDestinationContentService> extends AbstractStrategyCommon implements GenericDestinationEngineFromStrategyCommon {
    private final ServiceType service;
    Logger log = LoggerFactory.getLogger(GenericDestinationEngineFromRemoteStrategy.class);

    public GenericDestinationEngineFromRemoteStrategy(ServiceType service) {
        this.service = service;
    }

    @Override
    public boolean isApplicable(EngineType engineType) {
        return List.of(EngineType.GITHUB, EngineType.GITEA, EngineType.GITLAB).contains(engineType);
    }

    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        CallbackContainer callbackContainer = engineContext.callbackContainer();
        ToContext toContext = engineContext.toContext();

        String login = service.getLogin(toContext.token(), toContext.url());

        StatusCommandContext initializingTheCloningProcessInput = buildUpdateStatusContext(engineContext.callbackContainer(), repositoryDTOList.size(), 0, "initializing the cloning process");
        new UpdateStatusCommand(initializingTheCloningProcessInput).execute();


        ThreadUtil threadUtil = ThreadUtil.getInstance();
        try (final ExecutorService executorService = threadUtil.createExecutor(CLONER_THREAD_NAME_PREFIX, engineContext.settingsContext().multithreadingEnabled())) {
            super.resetIndex();
            NoHomeGitConfigSystemReader.install();
            LocalService localService = LocalServiceImpl.getInstance();
            for (RepositoryDTO repositoryDTO : repositoryDTOList) {
                executorService.execute(() -> processItem(engineContext, repositoryDTO, login, localService));
            }
        }

        new UpdateStatusCommand(buildUpdateStatusContext(engineContext.callbackContainer(), repositoryDTOList.size(), super.getIndex(), String.format("done%s", calculateStatus(repositoryDTOList.size())))).execute();

        return super.getIndex() == repositoryDTOList.size();
    }

    private void processItem(EngineContext engineContext, RepositoryDTO repositoryDTO, String toContextLogin, LocalService localService) {
        String repositoryName = repositoryDTO.getName();
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        final String TEMP_DIRECTORY = ApplicationUtil.getTemporaryFolderName();

        if (isShouldStopTheProcess(repositoryName, callbackContainer)) {
            return;
        }

        callbackContainer.updateLogAndApplicationStatusMessage().accept("cloning repository: " + repositoryName);

        boolean isOverrideFlagEnabled = toContext.overrideIfExists();
        RemoteExistsCheckCommandContext remoteExistsCheckCommandContext = GenericDestinationEngineCommon.buildRemoteExistsCheckInput(engineContext, toContextLogin, repositoryName);
        Boolean isDestinationRepositoryAlreadyExists = isRemoteDestinationRepositoryAlreadyExists(remoteExistsCheckCommandContext);

        if (!isOverrideFlagEnabled && isDestinationRepositoryAlreadyExists) {
            incrementIndexSuccess(callbackContainer);
            return;
        }

        String stagedClonePath = TEMP_DIRECTORY + File.separator + repositoryName;
        try {
            log.debug("local cloning path: {}", stagedClonePath);
            log.debug("from url: {}", repositoryDTO.getCloneAddress());
            FileUtils.deleteDirectory(new File(stagedClonePath));
            boolean result = localService.clone(fromContext.login(), fromContext.token(), repositoryDTO.getCloneAddress(), stagedClonePath);
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
                boolean repositoryCreationResult = service.createRepository(toContext.url(), toContext.token(), repositoryName, "", RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()));
                callbackContainer.updateLogAndApplicationStatusMessage().accept("Destination repo created: " + repositoryName);
            }

            if (isOverrideFlagEnabled) {
                String message = String.format("isOverrideFlagEnabled: executing git push --force %s", repositoryName);
                callbackContainer.updateLogAndApplicationStatusMessage().accept(message);
            }

            String message = String.format("pushing %s on %s...", repositoryName, toContext.url());
            callbackContainer.updateLogAndApplicationStatusMessage().accept(message);

            boolean isPushOk = localService.pushOnRemote(toContextLogin, toContext.token(), toContext.url(), repositoryName, toContextLogin, new File(stagedClonePath), isOverrideFlagEnabled, true);

            message = String.format("push status for repo %s: %S", repositoryName, isPushOk);
            callbackContainer.updateLogAndApplicationStatusMessage().accept(message);

            if (!isPushOk) {
                throw new RuntimeException(String.format("push status for repo %s: %S", repositoryName, isPushOk));
            }
        } catch (IOException | GitAPIException | URISyntaxException | InterruptedException | RuntimeException e) {
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
            log.error("Unable to push repository {}", repositoryName, e);
            return;
        }

        try {
            log.debug("updating repository privacy...");
            boolean result = service.updateRepositoryPrivacy(toContext.token(), toContextLogin, toContext.url(), repositoryName, false, RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()));
            if (!result) {
                throw new RuntimeException(String.format("update repository privacy for repo %s: %s", repositoryName, result));
            }
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

    private static DeleteRepositoryRequestDTO buildDestinationDeleteRepositoryRequestDTO(String toContextLogin, ToContext toContext, String repositoryName) {
        return new DeleteRepositoryRequestDTO(Optional.of(toContext.token()), Optional.of(toContext.url()), Optional.of(toContextLogin), Optional.of(repositoryName), Optional.empty());
    }

}
