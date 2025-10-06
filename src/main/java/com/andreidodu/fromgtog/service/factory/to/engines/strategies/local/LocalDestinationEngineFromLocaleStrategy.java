package com.andreidodu.fromgtog.service.factory.to.engines.strategies.local;

import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.AbstractStrategyCommon;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.util.ThreadUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.CLONER_THREAD_NAME_PREFIX;

public class LocalDestinationEngineFromLocaleStrategy extends AbstractStrategyCommon implements LocalDestinationEngineFromStrategy {
    Logger log = LoggerFactory.getLogger(LocalDestinationEngineFromLocaleStrategy.class);

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

        ThreadUtil threadUtil = ThreadUtil.getInstance();
        try (final ExecutorService executorService = threadUtil.createExecutor(CLONER_THREAD_NAME_PREFIX, engineContext.settingsContext().multithreadingEnabled())) {

            super.resetIndex();
            for (String path : pathList) {
                executorService.execute(() -> processItem(engineContext, path));
            }

            threadUtil.waitUntilShutDownCompleted(executorService);


            callbackContainer.updateApplicationStatusMessage().accept("done!");
            callbackContainer.updateApplicationProgressBarMax().accept(100);
            callbackContainer.updateApplicationProgressBarCurrent().accept(0);
            callbackContainer.setShouldStop().accept(true);
            return true;
        }
    }

    private void processItem(EngineContext engineContext, String path) {
        CallbackContainer callbackContainer = engineContext.callbackContainer();
        ToContext toContext = engineContext.toContext();

        if (callbackContainer.isShouldStop().get()) {
            log.debug("skipping because {} because user stop request", path);
            callbackContainer.updateApplicationStatusMessage().accept("Skipping repository because user stop request: " + path);
            completeTask(callbackContainer);
            return;
        }
        String repositoryName = new File(path).getName();
        String toDirectoryPath = toContext.rootPath() + File.separator + repositoryName;

        if (new File(toDirectoryPath).exists()) {
            log.debug("skipping because {} already exists", repositoryName);
            callbackContainer.updateApplicationStatusMessage().accept("Skipping repository because it already exists: " + repositoryName);
            completeTask(callbackContainer);
            return;
        }

        try {
            callbackContainer.updateApplicationStatusMessage().accept("cloning " + repositoryName + " ...");
            FileUtils.copyDirectory(new File(path), new File(toDirectoryPath));
        } catch (IOException e) {
            log.error("unable to copy from {} to {} because {}", path, toDirectoryPath, e.getMessage());
        }

        completeTask(callbackContainer);

    }


}
