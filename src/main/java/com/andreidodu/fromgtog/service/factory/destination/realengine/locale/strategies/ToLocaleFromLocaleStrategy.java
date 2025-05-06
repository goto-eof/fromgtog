package com.andreidodu.fromgtog.service.factory.destination.realengine.locale.strategies;

import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.type.EngineType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ToLocaleFromLocaleStrategy implements ToLocaleFromStrategy {
    Logger log = LoggerFactory.getLogger(ToLocaleFromLocaleStrategy.class);

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

        int i = 1;
        for (String path : pathList) {
            callbackContainer.updateApplicationProgressBarCurrent().accept(i++);
            String repositoryName = new File(path).getName();
            String toDirectoryPath = toContext.rootPath() + File.separator + repositoryName;

            if (new File(toDirectoryPath).exists()) {
                log.debug("skipping because {} already exists", repositoryName);
                callbackContainer.updateApplicationStatusMessage().accept("Skipping repository because it already exists: " + repositoryName);
                continue;
            }

            try {
                callbackContainer.updateApplicationStatusMessage().accept("cloning " + repositoryName + " ...");

                FileUtils.copyDirectory(new File(path), new File(toDirectoryPath));
            } catch (IOException e) {
                log.error("unable to copy from {} to {} because {}", path, toDirectoryPath, e.getMessage());
            }
        }
        callbackContainer.updateApplicationStatusMessage().accept("done!");
        callbackContainer.updateApplicationProgressBarMax().accept(100);
        callbackContainer.updateApplicationProgressBarCurrent().accept(0);
        return true;
    }


}
