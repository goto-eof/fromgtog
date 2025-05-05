package com.andreidodu.fromgtog.cloner.engine.destination.realengine;

import com.andreidodu.fromgtog.cloner.dto.EngineContext;
import com.andreidodu.fromgtog.cloner.dto.RepositoryDTO;
import com.andreidodu.fromgtog.cloner.engine.destination.AbstractDestinationEngine;
import com.andreidodu.fromgtog.cloner.type.DestinationEngineType;

import java.util.List;

public class LocalDestinationEngine extends AbstractDestinationEngine {
    private final static DestinationEngineType DESTINATION_ENGINE_TYPE = DestinationEngineType.LOCAL;

    public DestinationEngineType getDestinationEngineType() {
        return DESTINATION_ENGINE_TYPE;
    }

    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        return false;
    }
}
