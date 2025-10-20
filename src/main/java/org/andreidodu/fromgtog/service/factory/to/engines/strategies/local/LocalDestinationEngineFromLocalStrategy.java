package org.andreidodu.fromgtog.service.factory.to.engines.strategies.local;

import org.andreidodu.fromgtog.dto.*;
import org.andreidodu.fromgtog.dto.*;
import org.andreidodu.fromgtog.service.LocalService;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.AbstractStrategyCommon;
import org.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import org.andreidodu.fromgtog.type.EngineType;
import org.andreidodu.fromgtog.util.ThreadUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static org.andreidodu.fromgtog.constants.ApplicationConstants.CLONER_THREAD_NAME_PREFIX;

public class LocalDestinationEngineFromLocalStrategy extends AbstractStrategyCommon implements LocalDestinationEngineFromStrategy {
    Logger log = LoggerFactory.getLogger(LocalDestinationEngineFromLocalStrategy.class);

    @Override
    public boolean isApplicable(EngineType engineType) {
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
        callbackContainer.updateLogAndApplicationStatusMessage().accept("initializing the cloning process");

        List<String> pathList = repositoryDTOList.stream()
                .map(RepositoryDTO::getPath)
                .toList();

        ThreadUtil threadUtil = ThreadUtil.getInstance();
        try (final ExecutorService executorService = threadUtil.createExecutor(CLONER_THREAD_NAME_PREFIX, engineContext.settingsContext().multithreadingEnabled())) {
            LocalService localService = LocalServiceImpl.getInstance();
            super.resetIndex();
            for (String path : pathList) {
                executorService.execute(() -> processItem(engineContext, path, localService));
            }

        }

        callbackContainer.updateLogAndApplicationStatusMessage().accept(String.format("done%s", calculateStatus(pathList.size())));
        callbackContainer.updateApplicationProgressBarMax().accept(pathList.size());
        callbackContainer.updateApplicationProgressBarCurrent().accept(super.getIndex());
        return super.getIndex() == pathList.size();
    }

    private void processItem(EngineContext engineContext, String path, LocalService service) {
        CallbackContainer callbackContainer = engineContext.callbackContainer();
        ToContext toContext = engineContext.toContext();

        if (callbackContainer.isShouldStop().get()) {
            log.debug("skipping because {} because user stop request", path);
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Skipping repository because user stop request: " + path);
            return;
        }
        String repositoryName = new File(path).getName();
        String toDirectoryPath = toContext.rootPath() + File.separator + repositoryName;

        File localRepoFile = new File(toDirectoryPath);

        boolean isLocalRepoAlreadyExists = localRepoFile.exists();
        boolean isOverrideIfExistsFlagEnabled = toContext.overrideIfExists();

        if (isLocalRepoAlreadyExists) {
            if (isOverrideIfExistsFlagEnabled) {
                String message = String.format("Override flag enabled. Deleting local repository [%s]...", repositoryName);
                callbackContainer.updateLogAndApplicationStatusMessage().accept(message);

                DeleteRepositoryRequestDTO deleteRepositoryRequestDTO = buildDestinationDeleteRepositoryRequestDTO(localRepoFile);
                boolean deletionStatus = service.deleteRepository(deleteRepositoryRequestDTO);

                message = String.format("Destination repository %s deleted: %s", repositoryName, deletionStatus);
                callbackContainer.updateLogAndApplicationStatusMessage().accept(message);

                log.info("I am going to clone again the same repository...");
            } else {
                log.debug("skipping because {} already exists", repositoryName);
                callbackContainer.updateLogAndApplicationStatusMessage().accept("Skipping repository because it already exists: " + repositoryName);
                incrementIndexSuccess(callbackContainer);
                return;
            }
        }

        try {
            callbackContainer.updateLogAndApplicationStatusMessage().accept("cloning " + repositoryName + " ...");
            FileUtils.copyDirectory(new File(path), localRepoFile);
        } catch (IOException e) {
            log.error("unable to copy from {} to {} because {}", path, toDirectoryPath, e.getMessage());
            return;
        }

        incrementIndexSuccess(callbackContainer);

    }

    private static DeleteRepositoryRequestDTO buildDestinationDeleteRepositoryRequestDTO(File localRepoPath) {
        return new DeleteRepositoryRequestDTO(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(localRepoPath));
    }


}
