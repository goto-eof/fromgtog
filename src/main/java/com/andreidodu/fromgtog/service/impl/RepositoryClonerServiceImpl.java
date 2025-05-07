package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.service.factory.CloneFactory;
import com.andreidodu.fromgtog.service.factory.CloneFactoryImpl;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.service.factory.to.engines.DestinationEngine;
import com.andreidodu.fromgtog.service.factory.from.SourceEngine;
import com.andreidodu.fromgtog.service.RepositoryCloner;
import com.andreidodu.fromgtog.type.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
                    try {
                        engineContext.callbackContainer().setEnabledUI().accept(false);
                        cloneFromAndTo(engineContext, sourceEngine, destinationEngine);
                        engineContext.callbackContainer().setEnabledUI().accept(true);
                        engineContext.callbackContainer().showSuccessMessage().accept("Clone procedure completed successfully!");
                    } catch (Exception e) {
                        engineContext.callbackContainer().showErrorMessage().accept("Something went wrong while cloning: " + e.getMessage());
                    }
                }
        );
        executor.shutdown();
    }

    private static void validateInput(EngineContext engineContext) {
        // TODO
    }

    private static boolean isFromLocalToLocal(EngineContext engineContext) {
        return EngineType.LOCAL.equals(engineContext.fromContext().sourceEngineType()) &&
                engineContext.fromContext().sourceEngineType().getValue() == engineContext.toContext().engineType().getValue();
    }

    private boolean cloneFromAndTo(EngineContext engineContext, SourceEngine sourceEngine, DestinationEngine destinationEngine) {
        List<RepositoryDTO> repositories = sourceEngine.retrieveRepositoryList(engineContext);
        log.debug("Number of repositories size: {}", repositories.size());
        log.debug("Repositories: {}", repositories);
        return destinationEngine.cloneAll(engineContext, repositories);

    }

}
