package com.andreidodu.fromgtog.engine.destination.realengine;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.engine.destination.AbstractDestinationEngine;
import com.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public class GiteaDestinationEngine extends AbstractDestinationEngine {

    private final static EngineType DESTINATION_ENGINE_TYPE = EngineType.GITEA;

    public EngineType getDestinationEngineType() {
        return DESTINATION_ENGINE_TYPE;
    }

    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        // TODO
        return false;
    }
}
