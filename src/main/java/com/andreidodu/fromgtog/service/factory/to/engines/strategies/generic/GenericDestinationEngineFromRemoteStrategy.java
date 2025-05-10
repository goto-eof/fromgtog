package com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic;

import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.service.LocalService;
import com.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.type.RepoPrivacyType;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class GenericDestinationEngineFromRemoteStrategy<ServiceType extends GenericDestinationEngineFromStrategyService> implements GenericDestinationEngineFromStrategyCommon {
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
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");

        LocalService localService = LocalServiceImpl.getInstance();
        String login = service.getLogin(toContext.token(), toContext.url());


        callbackContainer.updateApplicationProgressBarMax().accept(repositoryDTOList.size());
        callbackContainer.updateApplicationProgressBarCurrent().accept(0);
        callbackContainer.updateApplicationStatusMessage().accept("initializing the cloning process");

        int i = 0;
        for (RepositoryDTO repositoryDTO : repositoryDTOList) {
            if (callbackContainer.isShouldStop().get()) {
                log.debug("skipping because {} because user stop request", repositoryDTO.getName());
                callbackContainer.updateApplicationStatusMessage().accept("Skipping repository because user stop request: " + repositoryDTO.getName());
                break;
            }
            callbackContainer.updateApplicationProgressBarCurrent().accept(i++);
            String repositoryName = repositoryDTO.getName();
            callbackContainer.updateApplicationStatusMessage().accept("cloning repository: " + repositoryName);

            if (localService.isRemoteRepositoryExists(login, toContext.token(), toContext.url() + "/" + login + "/" + repositoryName + ".git")) {
                log.debug("skipping because {} already exists", repositoryName);
                callbackContainer.updateApplicationStatusMessage().accept("Skipping repository because it already exists: " + repositoryName);
                continue;
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
                continue;
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
                continue;
            }

            try {
                log.debug("updating repository privacy...");
                boolean result = service.updateRepositoryPrivacy(toContext.token(), login, toContext.url(), repositoryName, false, RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()));
            } catch (Exception e) {
                callbackContainer.updateApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
                log.error("Unable to push repository {}", repositoryName, e);
                continue;
            }

            try {
                log.debug("sleeping");
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
