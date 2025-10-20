package org.andreidodu.fromgtog.service.factory.to;

import org.andreidodu.fromgtog.dto.EngineContext;
import org.andreidodu.fromgtog.dto.RepositoryDTO;
import org.andreidodu.fromgtog.exception.CloningDestinationException;
import org.andreidodu.fromgtog.service.factory.to.engines.DestinationEngine;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.DestinationEngineStrategy;
import org.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public abstract class AbstractDestinationEngine implements DestinationEngine {

    @Override
    public boolean accept(EngineType sourceEngineType) {
        return getDestinationEngineType().equals(sourceEngineType);
    }

    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        EngineType sourceEngineType = engineContext.fromContext().sourceEngineType();
        return getStrategies()
                .stream()
                .filter(fromStrategy -> fromStrategy.isApplicable(sourceEngineType))
                .findFirst()
                .orElseThrow(() -> new CloningDestinationException("Invalid from strategy"))
                .cloneAll(engineContext, repositoryDTOList);
    }

    public abstract List<DestinationEngineStrategy> getStrategies();
}
