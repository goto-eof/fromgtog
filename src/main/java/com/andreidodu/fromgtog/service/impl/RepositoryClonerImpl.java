package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.engine.factory.CloneFactory;
import com.andreidodu.fromgtog.engine.factory.CloneFactoryImpl;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.engine.factory.destination.DestinationEngine;
import com.andreidodu.fromgtog.engine.factory.source.SourceEngine;
import com.andreidodu.fromgtog.service.RepositoryCloner;
import com.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public class RepositoryClonerImpl implements RepositoryCloner {

    @Override
    public boolean cloneAllRepositories(EngineContext engineContext) {
        validateInput(engineContext);

        CloneFactory cloneFactory = new CloneFactoryImpl();
        SourceEngine sourceEngine = cloneFactory.buildSource(engineContext.fromContext().sourceEngineType());
        DestinationEngine destinationEngine = cloneFactory.buildDestination(engineContext.toContext().engineType());

        return cloneFromAndTo(engineContext, sourceEngine, destinationEngine);
    }

    private static void validateInput(EngineContext engineContext) {
        if (isFromLocalToLocal(engineContext)) {
            throw new IllegalArgumentException("It is not possible to clone from local to local.");
        }
    }

    private static boolean isFromLocalToLocal(EngineContext engineContext) {
        return EngineType.LOCAL.equals(engineContext.fromContext().sourceEngineType()) &&
                engineContext.fromContext().sourceEngineType().getValue() == engineContext.toContext().engineType().getValue();
    }

    private boolean cloneFromAndTo(EngineContext engineContext, SourceEngine sourceEngine, DestinationEngine destinationEngine) {
        List<RepositoryDTO> repositories = sourceEngine.retrieveRepositoryList(engineContext);
        return destinationEngine.cloneAll(engineContext, repositories);

    }

}
