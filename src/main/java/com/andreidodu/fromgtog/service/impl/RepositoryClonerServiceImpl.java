package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.service.JobService;
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

import static com.andreidodu.fromgtog.constants.ApplicationConstants.ORCHESTRATOR_THREAD_NAME_PREFIX;

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
        CloneFactory cloneFactory = new CloneFactoryImpl();
        SourceEngine sourceEngine = cloneFactory.buildSource(engineContext.fromContext().sourceEngineType());
        DestinationEngine destinationEngine = cloneFactory.buildDestination(engineContext.toContext().engineType());

        log.debug("Source: {}, Destination: {}", sourceEngine.getEngineType(), destinationEngine.getDestinationEngineType());

        if (engineContext.settingsContext().chronJobEnabled()) {
            JobService jobService = new JobServiceImpl(engineContext);
            try {
                log.debug("Starting TicTac Job Service, isShouldStop: {}", engineContext.callbackContainer().isShouldStop());
                engineContext.callbackContainer().setEnabledUI().accept(false);
                jobService.run(() -> executeOnNewThread(engineContext, sourceEngine, destinationEngine));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                jobService.shutdown();
                engineContext.callbackContainer().setEnabledUI().accept(true);
                engineContext.callbackContainer().setShouldStop().accept(true);
                engineContext.callbackContainer().setWorking().accept(false);
                return false;
            }
        } else {
            executeOnNewThread(engineContext, sourceEngine, destinationEngine);
        }
        return true;
    }

    private void executeOnNewThread(EngineContext engineContext, SourceEngine sourceEngine, DestinationEngine destinationEngine) {
        ThreadUtil.getInstance().executeOnSeparateThread(ORCHESTRATOR_THREAD_NAME_PREFIX, () -> {
                    TimeCounterService timeCounterService = new TimeCounterService(engineContext.callbackContainer().updateTimeLabel());
                    try {
                        EngineType from = engineContext.fromContext().sourceEngineType();
                        EngineType to = engineContext.toContext().engineType();
                        log.info("Start to clone from {} to {}", from, to);
                        engineContext.callbackContainer().updateLogAndApplicationStatusMessage().accept("Start to clone from " + from + " to " + to);

                        engineContext.callbackContainer().setEnabledUI().accept(false);
                        engineContext.callbackContainer().setWorking().accept(true);
                        engineContext.callbackContainer().setShouldStop().accept(false);
                        boolean isSuccess = cloneFromAndTo(engineContext, sourceEngine, destinationEngine);
                        engineContext.callbackContainer().setWorking().accept(false);
                        if (!engineContext.settingsContext().chronJobEnabled()) {
                            engineContext.callbackContainer().setEnabledUI().accept(true);
                            engineContext.callbackContainer().setShouldStop().accept(true);
                        }
                        engineContext.callbackContainer().jobTicker().accept(true);
                        timeCounterService.stopCounter();
                        if (isSuccess && !engineContext.settingsContext().chronJobEnabled()) {
                            engineContext.callbackContainer().showSuccessMessage().accept("Clone procedure completed successfully!");
                            return;
                        }
                        if (!isSuccess && !engineContext.settingsContext().chronJobEnabled()) {
                            engineContext.callbackContainer().showErrorMessage().accept("Something went wrong while cloning: not all repositories were cloned. Please check the log file for further details.");
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        engineContext.callbackContainer().setEnabledUI().accept(true);
                        engineContext.callbackContainer().setShouldStop().accept(true);
                        engineContext.callbackContainer().setWorking().accept(false);
                        if (!engineContext.settingsContext().chronJobEnabled()) {
                            engineContext.callbackContainer().showErrorMessage().accept("Something went wrong while cloning: " + e.getMessage() + ". Please check the log file for further details.");
                        }
                        timeCounterService.stopCounter();
                    }
                }
        );
    }

    private boolean cloneFromAndTo(EngineContext engineContext, SourceEngine sourceEngine, DestinationEngine destinationEngine) {
        List<RepositoryDTO> repositories = sourceEngine.retrieveRepositoryList(engineContext);
        log.debug("Number of repositories size: {}", repositories.size());
        log.debug("Repositories: {}", repositories);
        return destinationEngine.cloneAll(engineContext, repositories);

    }

}
