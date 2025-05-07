package com.andreidodu.fromgtog.service.factory.destination.realengine;

import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.dto.gitea.GiteaUserDTO;
import com.andreidodu.fromgtog.service.GiteaService;
import com.andreidodu.fromgtog.service.LocalService;
import com.andreidodu.fromgtog.service.factory.destination.AbstractDestinationEngine;
import com.andreidodu.fromgtog.service.factory.destination.realengine.local.strategies.ToLocalFromRemoteStrategy;
import com.andreidodu.fromgtog.service.impl.GiteaServiceImpl;
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

public class GiteaDestinationEngine extends AbstractDestinationEngine {

    private final static EngineType DESTINATION_ENGINE_TYPE = EngineType.GITEA;
    Logger log = LoggerFactory.getLogger(GiteaDestinationEngine.class);

    public EngineType getDestinationEngineType() {
        return DESTINATION_ENGINE_TYPE;
    }

    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");

        LocalService localService = LocalServiceImpl.getInstance();

        GiteaService giteaService = GiteaServiceImpl.getInstance();
        GiteaUserDTO giteaUserDTO = giteaService.getMyself(toContext.token(), toContext.url());

        callbackContainer.updateApplicationProgressBarMax().accept(repositoryDTOList.size());
        callbackContainer.updateApplicationProgressBarCurrent().accept(0);
        callbackContainer.updateApplicationStatusMessage().accept("initializing the cloning process");

        int i = 0;
        for (RepositoryDTO repositoryDTO : repositoryDTOList) {
            callbackContainer.updateApplicationProgressBarCurrent().accept(i++);
            callbackContainer.updateApplicationStatusMessage().accept("cloning repository: " + repositoryDTO.getName());

            if (localService.isRemoteRepositoryExists(giteaUserDTO.getLogin(), toContext.token(), toContext.url() + "/" + giteaUserDTO.getLogin() + "/" + repositoryDTO.getName() + ".git")) {
                log.debug("skipping because {} already exists", repositoryDTO.getName());
                callbackContainer.updateApplicationStatusMessage().accept("Skipping repository because it already exists: " + repositoryDTO.getName());
                continue;
            }

            String stagedClonePath = TEMP_DIRECTORY + File.separator + repositoryDTO.getName();

            try {
                log.debug("local cloning path: {}", stagedClonePath);
                log.debug("from url: {}", repositoryDTO.getCloneAddress());
                FileUtils.deleteDirectory(new File(stagedClonePath));
                boolean result = localService.clone(fromContext.login(), fromContext.token(), repositoryDTO.getCloneAddress(), stagedClonePath);
            } catch (Exception e) {
                callbackContainer.updateApplicationStatusMessage().accept("Unable to clone repository " + repositoryDTO.getName());
                log.error("Unable to clone repository {}", repositoryDTO.getName(), e);
                continue;
            }

            try {
                log.debug("pushing...");
                boolean result = localService.pushOnRemote(giteaUserDTO.getLogin(), toContext.token(), toContext.url(), repositoryDTO.getName(), giteaUserDTO.getLogin(), new File(stagedClonePath));
            } catch (IOException | GitAPIException | URISyntaxException e) {
                callbackContainer.updateApplicationStatusMessage().accept("Unable to push repository " + repositoryDTO.getName());
                log.error("Unable to push repository {}", repositoryDTO.getName(), e);
                continue;
            }


            try {
                log.debug("updating repository privacy...");
                boolean result = giteaService.updateRepositoryPrivacy(toContext.token(), giteaUserDTO.getLogin(), toContext.url(), repositoryDTO.getName(), false, RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()));
            } catch (IOException | InterruptedException e) {
                callbackContainer.updateApplicationStatusMessage().accept("Unable to push repository " + repositoryDTO.getName());
                log.error("Unable to push repository {}", repositoryDTO.getName(), e);
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

        return false;
    }
}
