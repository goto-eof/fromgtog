package com.andreidodu.fromgtog.engine.destination.realengine;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.engine.destination.AbstractDestinationEngine;
import com.andreidodu.fromgtog.type.DestinationEngineType;

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
