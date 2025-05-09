package com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic;

import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.service.LocalService;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.AbstractFromLocalCommon;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.github.GithubDestinationEngineFromLocaleStrategy;
import com.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.type.RepoPrivacyType;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class GenericDestinationEngineFromLocalStrategy<Service extends GenericDestinationEngineFromStrategyService> extends AbstractFromLocalCommon implements GenericDestinationEngineFromStrategyCommon {
    Logger log = LoggerFactory.getLogger(GenericDestinationEngineFromLocalStrategy.class);

    private final Service service;

    public GenericDestinationEngineFromLocalStrategy(Service service) {
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

        if (StringUtils.equals(fromContext.rootPath(), toContext.rootPath())) {
            throw new IllegalArgumentException("Root paths cannot be the same");
        }

        callbackContainer.updateApplicationProgressBarMax().accept(repositoryDTOList.size());
        callbackContainer.updateApplicationProgressBarCurrent().accept(0);
        callbackContainer.updateApplicationStatusMessage().accept("initializing the cloning process");

        List<String> pathList = repositoryDTOList.stream()
                .map(RepositoryDTO::getPath)
                .toList();

        String tokenOwnerLogin = service.getLogin(toContext.token(), toContext.url());

        log.debug("tokenOwnerLogin: {}", tokenOwnerLogin);

        LocalService localService = LocalServiceImpl.getInstance();

        int i = 1;
        for (String path : pathList) {
            if (callbackContainer.isShouldStop().get()) {
                log.debug("skipping because {} because user stop request", path);
                callbackContainer.updateApplicationStatusMessage().accept("Skipping repository because user stop request: " + path);
                break;
            }
            callbackContainer.updateApplicationProgressBarCurrent().accept(i++);
            String repositoryName = new File(path).getName();
            repositoryName = super.correctRepositoryName(repositoryName);

            log.debug("toDirectoryPath: {}", path);


            if (localService.isRemoteRepositoryExists(tokenOwnerLogin, toContext.token(), toContext.url() + "/" + tokenOwnerLogin + "/" + repositoryName + ".git")) {
                log.debug("skipping because {} already exists", repositoryName);
                callbackContainer.updateApplicationStatusMessage().accept("Skipping repository because it already exists: " + repositoryName);
                continue;
            }

            callbackContainer.updateApplicationStatusMessage().accept("cloning " + repositoryName + " ...");
            callbackContainer.updateApplicationStatusMessage().accept("cloning repository: " + repositoryName);

            try {
                if (!service.createRepository(toContext.url(), toContext.token(), repositoryName, "", RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()))) {
                    callbackContainer.updateApplicationStatusMessage().accept("unable to create repository: " + repositoryName);
                    log.debug("unable to create repository: {}", repositoryName);
                    continue;
                }
            } catch (Exception e) {
                callbackContainer.updateApplicationStatusMessage().accept("unable to create repository: " + repositoryName);
                log.debug("unable to create repository: {}", repositoryName, e);
                continue;
            }

            try {
                log.debug("pushing...");
                boolean result = localService.pushOnRemote(tokenOwnerLogin, toContext.token(), toContext.url(), repositoryName, tokenOwnerLogin, new File(path));
            } catch (IOException | GitAPIException | URISyntaxException e) {
                callbackContainer.updateApplicationStatusMessage().accept("Unable to push repository " + repositoryName);
                log.error("Unable to push repository {}", repositoryName, e);
                continue;
            }

            try {
                log.debug("updating repository privacy...");
                boolean result = service.updateRepositoryPrivacy(toContext.token(), tokenOwnerLogin, toContext.url(), repositoryName, false, RepoPrivacyType.ALL_PRIVATE.equals(toContext.repositoryPrivacy()));
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
