package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.service.RepositoryCloner;
import com.andreidodu.fromgtog.service.factory.CloneFactory;
import com.andreidodu.fromgtog.service.factory.CloneFactoryImpl;
import com.andreidodu.fromgtog.service.factory.from.SourceEngine;
import com.andreidodu.fromgtog.service.factory.to.engines.DestinationEngine;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RepositoryClonerServiceImpl implements RepositoryCloner {

    private static RepositoryClonerServiceImpl instance;
    Logger log = LoggerFactory.getLogger(RepositoryClonerServiceImpl.class);

    public static RepositoryClonerServiceImpl getInstance() {
        if (instance == null) {
            instance = new RepositoryClonerServiceImpl();
        }
        return instance;
    }

    @Override
    public boolean cloneAllRepositories(EngineContext engineContext) {
        validateInput(engineContext);

        CloneFactory cloneFactory = new CloneFactoryImpl();
        SourceEngine sourceEngine = cloneFactory.buildSource(engineContext.fromContext().sourceEngineType());
        DestinationEngine destinationEngine = cloneFactory.buildDestination(engineContext.toContext().engineType());

        log.debug("Source: {}, Destination: {}", sourceEngine.getEngineType(), destinationEngine.getDestinationEngineType());
        executeOnNewThread(engineContext, sourceEngine, destinationEngine);
        return true;
    }

    private void executeOnNewThread(EngineContext engineContext, SourceEngine sourceEngine, DestinationEngine destinationEngine) {
        ThreadUtil.getInstance().executeOnSeparateThread(() -> {
                    TimeCounterService timeCounterService = new TimeCounterService(engineContext.callbackContainer().updateTimeLabel());
                    try {
                        EngineType from = engineContext.fromContext().sourceEngineType();
                        EngineType to = engineContext.toContext().engineType();
                        log.info("Start to clone from {} to {}", from, to);
                        engineContext.callbackContainer().updateApplicationStatusMessage().accept("Start to clone from " + from + " to " + to);

                        engineContext.callbackContainer().setEnabledUI().accept(false);
                        cloneFromAndTo(engineContext, sourceEngine, destinationEngine);
                        engineContext.callbackContainer().setEnabledUI().accept(true);
                        engineContext.callbackContainer().showSuccessMessage().accept("Clone procedure completed successfully!");
                        timeCounterService.stopCounter();
                    } catch (Exception e) {
                        engineContext.callbackContainer().setEnabledUI().accept(true);
                        engineContext.callbackContainer().setShouldStop().accept(true);
                        engineContext.callbackContainer().showErrorMessage().accept("Something went wrong while cloning: " + e.getMessage());
                        timeCounterService.stopCounter();
                    }
                }
        );
    }

    private static void validateInput(EngineContext engineContext) {
        // TODO
    }


    private boolean cloneFromAndTo(EngineContext engineContext, SourceEngine sourceEngine, DestinationEngine destinationEngine) {
        List<RepositoryDTO> repositories = sourceEngine.retrieveRepositoryList(engineContext);
        log.debug("Number of repositories size: {}", repositories.size());
        log.debug("Repositories: {}", repositories);
        return destinationEngine.cloneAll(engineContext, repositories);

    }

}
