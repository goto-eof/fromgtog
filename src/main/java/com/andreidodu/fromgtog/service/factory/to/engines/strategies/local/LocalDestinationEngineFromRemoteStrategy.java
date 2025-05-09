package com.andreidodu.fromgtog.service.factory.to.engines.strategies.local;

import com.andreidodu.fromgtog.config.NoHomeGitConfigSystemReader;
import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.type.EngineType;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class LocalDestinationEngineFromRemoteStrategy implements LocalDestinationEngineFromStrategy {

    Logger log = LoggerFactory.getLogger(LocalDestinationEngineFromRemoteStrategy.class);

    @Override
    public boolean accept(EngineType engineType) {
        return List.of(EngineType.GITHUB, EngineType.GITEA, EngineType.GITLAB).contains(engineType);
    }

    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        callbackContainer.updateApplicationProgressBarMax().accept(repositoryDTOList.size());
        callbackContainer.updateApplicationProgressBarCurrent().accept(0);
        callbackContainer.updateApplicationStatusMessage().accept("initializing the cloning process");

        int i = 1;
        for (RepositoryDTO repositoryDTO : repositoryDTOList) {
            if (callbackContainer.isShouldStop().get()) {
                log.debug("skipping because {} because user stop request", repositoryDTO.getName());
                callbackContainer.updateApplicationStatusMessage().accept("Skipping repository because user stop request: " + repositoryDTO.getName());
                break;
            }
            callbackContainer.updateApplicationProgressBarCurrent().accept(i++);

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
                continue;
            }
            NoHomeGitConfigSystemReader.install();
            try {
                log.debug("starting the cloning process of {}...", repositoryDTO.getName());
                Git clonedRepo = Git.cloneRepository()
                        .setURI(cloneUrl)
                        .setDirectory(file)
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(fromContext.login(), fromContext.token()))
                        .call();
                log.debug("Done! Repository {} cloned successfully.", repositoryDTO.getName());
            } catch (GitAPIException e) {
                log.error("Unable to clone repository {} because {}", repositoryDTO.getName(), e.getMessage());
                callbackContainer.updateApplicationStatusMessage().accept("Unable to clone repository " + repositoryDTO.getName());
            }

            try {
                Thread.sleep(engineContext.settingsContext().sleepTimeSeconds() * 1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        callbackContainer.updateApplicationStatusMessage().accept("done!");
        callbackContainer.updateApplicationProgressBarMax().accept(100);
        callbackContainer.updateApplicationProgressBarCurrent().accept(0);
        callbackContainer.setShouldStop().accept(true);
        return true;
    }
}
