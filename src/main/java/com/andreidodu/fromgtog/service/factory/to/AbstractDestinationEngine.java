package com.andreidodu.fromgtog.service.factory.to;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.exception.CloningDestinationException;
import com.andreidodu.fromgtog.service.factory.to.engines.DestinationEngine;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.DestinationEngineStrategy;
import com.andreidodu.fromgtog.type.EngineType;

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
