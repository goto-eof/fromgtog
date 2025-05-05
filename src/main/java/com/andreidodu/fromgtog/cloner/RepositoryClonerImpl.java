package com.andreidodu.fromgtog.cloner;

import com.andreidodu.fromgtog.cloner.dto.EngineContext;
import com.andreidodu.fromgtog.cloner.engine.factory.CloneFactory;
import com.andreidodu.fromgtog.cloner.engine.factory.CloneFactoryImpl;
import com.andreidodu.fromgtog.cloner.dto.RepositoryDTO;
import com.andreidodu.fromgtog.cloner.engine.destination.DestinationEngine;
import com.andreidodu.fromgtog.cloner.engine.source.SourceEngine;
import com.andreidodu.fromgtog.cloner.type.SourceEngineType;

import java.util.List;

public class RepositoryClonerImpl implements RepositoryCloner {

    @Override
    public boolean cloneAllRepositories(EngineContext engineContext) {
        validateInput(engineContext);

        CloneFactory cloneFactory = new CloneFactoryImpl();
        SourceEngine sourceEngine = cloneFactory.buildSource(engineContext.fromContext().sourceEngineType());
        DestinationEngine destinationEngine = cloneFactory.buildDestination(engineContext.toContext().destinationEngineType());

        return cloneFromAndTo(engineContext, sourceEngine, destinationEngine);
    }

    private static void validateInput(EngineContext engineContext) {
        if (isFromLocalToLocal(engineContext)) {
            throw new IllegalArgumentException("It is not possible to clone from local to local.");
        }
    }

    private static boolean isFromLocalToLocal(EngineContext engineContext) {
        return SourceEngineType.LOCAL.equals(engineContext.fromContext().sourceEngineType()) &&
                engineContext.fromContext().sourceEngineType().getValue() == engineContext.toContext().destinationEngineType().getValue();
    }

    private boolean cloneFromAndTo(EngineContext engineContext, SourceEngine sourceEngine, DestinationEngine destinationEngine) {
        List<RepositoryDTO> repositories = sourceEngine.retrieveRepositoryList(engineContext);
        return destinationEngine.cloneAll(engineContext, repositories);

    }

}
