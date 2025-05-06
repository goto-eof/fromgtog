package com.andreidodu.fromgtog.service.factory.destination.realengine;

import com.andreidodu.fromgtog.config.NoHomeGitConfigSystemReader;
import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.service.factory.destination.AbstractDestinationEngine;
import com.andreidodu.fromgtog.type.EngineType;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class LocalDestinationEngine extends AbstractDestinationEngine {
    private final static EngineType DESTINATION_ENGINE_TYPE = EngineType.LOCAL;
    private final static Logger log = LoggerFactory.getLogger(LocalDestinationEngine.class);

    public EngineType getDestinationEngineType() {
        return DESTINATION_ENGINE_TYPE;
    }

    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        FromContext fromContext = engineContext.fromContext();
        ToContext toContext = engineContext.toContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        callbackContainer.updateApplicationProgressBarCurrent().accept(repositoryDTOList.size());
        callbackContainer.updateApplicationProgressBarCurrent().accept(0);
        callbackContainer.updateApplicationStatusMessage().accept("initializing the cloning process");

        int i = 1;
        for (RepositoryDTO repositoryDTO : repositoryDTOList) {
            callbackContainer.updateApplicationProgressBarCurrent().accept(i++);

            String cloneUrl = repositoryDTO.getCloneAddress();

            if (repositoryDTO.isPrivateFlag()) {
                cloneUrl = cloneUrl.replace("github.com", fromContext.login() + ":" + fromContext.token() + "@github.com");
            }

            callbackContainer.updateApplicationStatusMessage().accept("processing repository: " + repositoryDTO.getName());
            File file = new File(toContext.rootPath() + "/" + repositoryDTO.getName());
            if (toContext.groupByRepositoryOwner()) {
                String repositoryOwnerName = repositoryDTO.getLogin();
                file = new File(toContext.rootPath() + "/" + repositoryOwnerName + "/" + repositoryDTO.getName());
                log.debug("path: {}", file.getAbsolutePath());
            }

            log.debug("creating repository: {}", file.getAbsolutePath());

            if (file.exists()) {
                log.debug("skipping because {} already exists", repositoryDTO.getName());
                continue;
            }
            NoHomeGitConfigSystemReader.install();
            try {
                Git clonedRepo = Git.cloneRepository()
                        .setURI(cloneUrl)
                        .setDirectory(file)
                        .call();
            } catch (GitAPIException e) {
                log.error("Unable to clone repository {}", repositoryDTO.getName());
            }
        }


        return true;
    }
}
