package com.andreidodu.fromgtog.service.factory.to.engines.strategies.local;

import com.andreidodu.fromgtog.config.NoHomeGitConfigSystemReader;
import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.service.LocalService;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.AbstractStrategyCommon;
import com.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.util.ThreadUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.CLONER_THREAD_NAME_PREFIX;

public class LocalDestinationEngineFromRemoteStrategy extends AbstractStrategyCommon implements LocalDestinationEngineFromStrategy {

    private final Logger log = LoggerFactory.getLogger(LocalDestinationEngineFromRemoteStrategy.class);

    @Override
    public boolean isApplicable(EngineType engineType) {
        return List.of(EngineType.GITHUB, EngineType.GITEA, EngineType.GITLAB).contains(engineType);
    }

    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        callbackContainer.updateApplicationProgressBarMax().accept(repositoryDTOList.size());
        callbackContainer.updateApplicationProgressBarCurrent().accept(0);
        callbackContainer.updateLogAndApplicationStatusMessage().accept("initializing the cloning process");

        ThreadUtil threadUtil = ThreadUtil.getInstance();
        try (final ExecutorService executorService = threadUtil.createExecutor(CLONER_THREAD_NAME_PREFIX, engineContext.settingsContext().multithreadingEnabled())) {
            this.resetIndex();
            NoHomeGitConfigSystemReader.install();
            LocalService localService = LocalServiceImpl.getInstance();
            for (RepositoryDTO repositoryDTO : repositoryDTOList) {
                final RepositoryDTO finalRepositoryDTO = repositoryDTO;
                executorService.execute(() -> processItem(engineContext, finalRepositoryDTO, localService));
            }
        }

        callbackContainer.updateLogAndApplicationStatusMessage().accept(String.format("done%s", calculateStatus(repositoryDTOList.size())));
        callbackContainer.updateApplicationProgressBarMax().accept(repositoryDTOList.size());
        callbackContainer.updateApplicationProgressBarCurrent().accept(super.getIndex());

        return super.getIndex() == repositoryDTOList.size();
    }


    private void processItem(EngineContext engineContext, RepositoryDTO repositoryDTO, LocalService service) {
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();
        if (callbackContainer.isShouldStop().get()) {
            log.debug("skipping because {} because user stop request", repositoryDTO.getName());
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Skipping repository because user stop request: " + repositoryDTO.getName());
            return;
        }

        String cloneUrl = repositoryDTO.getCloneAddress();

        callbackContainer.updateLogAndApplicationStatusMessage().accept("cloning repository: " + repositoryDTO.getName());
        File localRepoFile = new File(toContext.rootPath() + "/" + repositoryDTO.getName());
        if (toContext.groupByRepositoryOwner()) {
            String repositoryOwnerName = repositoryDTO.getLogin();
            localRepoFile = new File(toContext.rootPath() + "/" + repositoryOwnerName + "/" + repositoryDTO.getName());
        }
        log.info("repo path: {}", localRepoFile.getAbsolutePath());

        boolean isOverrideIfExistsFlagEnabled = toContext.overrideIfExists();
        boolean isLocalRepoAlreadyExists = localRepoFile.exists();

        if (isLocalRepoAlreadyExists) {
            if (isOverrideIfExistsFlagEnabled) {

                String message = String.format("Override flag enabled. Deleting local repository [%s]...", repositoryDTO.getName());
                callbackContainer.updateLogAndApplicationStatusMessage().accept(message);

                DeleteRepositoryRequestDTO deleteRepositoryRequestDTO = buildDestinationDeleteRepositoryRequestDTO(localRepoFile);
                boolean deletionStatus = service.deleteRepository(deleteRepositoryRequestDTO);

                message = String.format("Destination repository %s deleted: %s", repositoryDTO.getName(), deletionStatus);
                callbackContainer.updateLogAndApplicationStatusMessage().accept(message);

                log.info("I am going to clone again the same repository...");
            } else {
                log.debug("skipping because {} already exists", repositoryDTO.getName());
                callbackContainer.updateLogAndApplicationStatusMessage().accept("Skipping repository because it already exists: " + repositoryDTO.getName());
                incrementIndexSuccess(callbackContainer);
                return;
            }
        }

        log.debug("creating repository: {}", localRepoFile.getAbsolutePath());

        try {
            log.debug("starting the cloning process of {}...", repositoryDTO.getName());
            try (Git clonedRepo = Git.cloneRepository()
                    .setURI(cloneUrl)
                    .setDirectory(localRepoFile)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(fromContext.login(), fromContext.token()))
                    .call()) {
                log.debug("Done! Repository {} cloned successfully.", repositoryDTO.getName());
            } catch (TransportException e) {
                log.error("Unable to clone repository {}...", repositoryDTO.getName(), e);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                throw e;
            }
        } catch (GitAPIException e) {
            log.error("Unable to clone repository {} because {}", repositoryDTO.getName(), e.getMessage(), e);
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Unable to clone repository " + repositoryDTO.getName());
            return;
        }

        incrementIndexSuccess(callbackContainer);

        try {
            Thread.sleep(engineContext.settingsContext().sleepTimeSeconds() * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static DeleteRepositoryRequestDTO buildDestinationDeleteRepositoryRequestDTO(File localRepoFile) {
        return new DeleteRepositoryRequestDTO(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(localRepoFile));
    }


}
