package com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic;

import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.service.LocalService;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.AbstractFromLocalCommon;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.ThreadSleepCommand;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.UpdateStatusCommand;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.RemoteExistsCheckCommandContext;
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

import static com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands.CommandCommon.*;

public class GenericDestinationEngineFromLocalStrategy<ServiceType extends GenericDestinationEngineFromStrategyService> extends AbstractFromLocalCommon implements GenericDestinationEngineFromStrategyCommon {
    Logger log = LoggerFactory.getLogger(GenericDestinationEngineFromLocalStrategy.class);

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

        LocalService localService = LocalServiceImpl.getInstance();

        int i = 1;
        for (String path : pathList) {
            String repositoryName = new File(path).getName();

            if (isShouldStopTheProcess(repositoryName, callbackContainer)) {
                break;
            }


            callbackContainer.updateApplicationProgressBarCurrent().accept(i++);
            repositoryName = super.correctRepositoryName(repositoryName);
            log.debug("toDirectoryPath: {}", path);


            RemoteExistsCheckCommandContext remoteExistsCheckCommandContext = GenericDestinationEngineCommon.buildRemoteExistsCheckInput(engineContext, tokenOwnerLogin, repositoryName);
            if (isRemoteRepositoryAlreadyExists(remoteExistsCheckCommandContext)) {
                continue;
            }

            String remoteRepositoryUrl = toContext.url() + "/" + tokenOwnerLogin + "/" + repositoryName + ".git";
            if (localService.isRemoteRepositoryExists(tokenOwnerLogin, toContext.token(), remoteRepositoryUrl)) {
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


            if (!new ThreadSleepCommand(engineContext.settingsContext().sleepTimeSeconds()).execute()) {
                throw new RuntimeException("Unable to put thread on sleep " + repositoryName);
            }

        }

        new UpdateStatusCommand(buildUpdateStatusContext(engineContext.callbackContainer(), 100, 0, "done")).execute();

        callbackContainer.setShouldStop().accept(true);
        return true;
    }


    private static void validateInput(FromContext fromContext, ToContext toContext) {
        if (StringUtils.equals(fromContext.rootPath(), toContext.rootPath())) {
            throw new IllegalArgumentException("Root paths cannot be the same");
        }
    }


}
