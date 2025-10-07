package com.andreidodu.fromgtog.service.factory.to.engines.strategies.local;

import com.andreidodu.fromgtog.config.NoHomeGitConfigSystemReader;
import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.AbstractStrategyCommon;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.util.ThreadUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.CLONER_THREAD_NAME_PREFIX;

public class LocalDestinationEngineFromRemoteStrategy extends AbstractStrategyCommon implements LocalDestinationEngineFromStrategy {

    private int index = 0;

    private Logger log = LoggerFactory.getLogger(LocalDestinationEngineFromRemoteStrategy.class);

    @Override
    public boolean accept(EngineType engineType) {
        return List.of(EngineType.GITHUB, EngineType.GITEA, EngineType.GITLAB).contains(engineType);
    }

    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        callbackContainer.updateApplicationProgressBarMax().accept(repositoryDTOList.size());
        callbackContainer.updateApplicationProgressBarCurrent().accept(0);
        callbackContainer.updateApplicationStatusMessage().accept("initializing the cloning process");

        ThreadUtil threadUtil = ThreadUtil.getInstance();
        try (final ExecutorService executorService = threadUtil.createExecutor(CLONER_THREAD_NAME_PREFIX, engineContext.settingsContext().multithreadingEnabled())) {
            this.resetIndex();
            NoHomeGitConfigSystemReader.install();

            for (RepositoryDTO repositoryDTO : repositoryDTOList) {
                final RepositoryDTO finalRepositoryDTO = repositoryDTO;
                executorService.execute(() -> processItem(engineContext, finalRepositoryDTO));
            }
        }

        callbackContainer.updateApplicationStatusMessage().accept(String.format("done%s", calculateStatus(repositoryDTOList.size())));
        callbackContainer.updateApplicationProgressBarMax().accept(repositoryDTOList.size());
        callbackContainer.updateApplicationProgressBarCurrent().accept(super.getIndex());

        callbackContainer.setShouldStop().accept(true);
        return super.getIndex() == repositoryDTOList.size();
    }


    private void processItem(EngineContext engineContext, RepositoryDTO repositoryDTO) {
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();
        if (callbackContainer.isShouldStop().get()) {
            log.debug("skipping because {} because user stop request", repositoryDTO.getName());
            callbackContainer.updateApplicationStatusMessage().accept("Skipping repository because user stop request: " + repositoryDTO.getName());
            return;
        }


        String cloneUrl = repositoryDTO.getCloneAddress();

        if (repositoryDTO.isPrivateFlag()) {
            cloneUrl = cloneUrl.replace("github.com", fromContext.login() + ":" + fromContext.token() + "@github.com");
        }

        callbackContainer.updateApplicationStatusMessage().accept("cloning repository: " + repositoryDTO.getName());
        File file = new File(toContext.rootPath() + "/" + repositoryDTO.getName());
        if (toContext.groupByRepositoryOwner()) {
            String repositoryOwnerName = repositoryDTO.getLogin();
            file = new File(toContext.rootPath() + "/" + repositoryOwnerName + "/" + repositoryDTO.getName());
            log.debug("path: {}", file.getAbsolutePath());
        }

        log.debug("creating repository: {}", file.getAbsolutePath());

        if (file.exists()) {
            log.debug("skipping because {} already exists", repositoryDTO.getName());
            callbackContainer.updateApplicationStatusMessage().accept("Skipping repository because it already exists: " + repositoryDTO.getName());
            incrementIndexSuccess(callbackContainer);
            return;
        }

        try {
            log.debug("starting the cloning process of {}...", repositoryDTO.getName());
            Git clonedRepo = Git.cloneRepository()
                    .setURI(cloneUrl)
                    .setDirectory(file)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(fromContext.login(), fromContext.token()))
                    .call();
            log.debug("Done! Repository {} cloned successfully.", repositoryDTO.getName());
        } catch (GitAPIException e) {
            log.error("Unable to clone repository {} because {}", repositoryDTO.getName(), e.getMessage(), e);
            callbackContainer.updateApplicationStatusMessage().accept("Unable to clone repository " + repositoryDTO.getName());
            return;
        }

        incrementIndexSuccess(callbackContainer);

        try {
            Thread.sleep(engineContext.settingsContext().sleepTimeSeconds() * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
