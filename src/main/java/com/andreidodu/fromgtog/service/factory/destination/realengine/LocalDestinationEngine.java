package com.andreidodu.fromgtog.service.factory.destination.realengine;

import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.exception.CloningDestinationException;
import com.andreidodu.fromgtog.service.factory.destination.AbstractDestinationEngine;
import com.andreidodu.fromgtog.service.factory.destination.realengine.locale.strategies.ToLocaleFromLocaleStrategy;
import com.andreidodu.fromgtog.service.factory.destination.realengine.locale.strategies.ToLocaleFromRemoteStrategy;
import com.andreidodu.fromgtog.type.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LocalDestinationEngine extends AbstractDestinationEngine {
    private final static EngineType DESTINATION_ENGINE_TYPE = EngineType.LOCAL;
    private final static Logger log = LoggerFactory.getLogger(LocalDestinationEngine.class);

    public EngineType getDestinationEngineType() {
        return DESTINATION_ENGINE_TYPE;
    }

    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        EngineType sourceEngineType = engineContext.fromContext().sourceEngineType();
        return List.of(new ToLocaleFromRemoteStrategy(), new ToLocaleFromLocaleStrategy())
                .stream().filter(fromStrategy -> fromStrategy.accept(sourceEngineType))
                .findFirst()
                .orElseThrow(() -> new CloningDestinationException("Invalid from strategy"))
                .cloneAll(engineContext, repositoryDTOList);
    }
}
