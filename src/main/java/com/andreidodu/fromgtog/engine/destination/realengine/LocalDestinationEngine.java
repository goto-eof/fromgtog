package com.andreidodu.fromgtog.engine.destination.realengine;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.engine.destination.AbstractDestinationEngine;
import com.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public class LocalDestinationEngine extends AbstractDestinationEngine {
    private final static EngineType DESTINATION_ENGINE_TYPE = EngineType.LOCAL;

    public EngineType getDestinationEngineType() {
        return DESTINATION_ENGINE_TYPE;
    }

    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        return false;
    }
}
