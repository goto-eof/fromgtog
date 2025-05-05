package com.andreidodu.fromgtog.service.factory.destination.realengine;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.service.factory.destination.AbstractDestinationEngine;
import com.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public class GithubDestinationEngine extends AbstractDestinationEngine {
    private final static EngineType DESTINATION_ENGINE_TYPE = EngineType.GITHUB;

    public EngineType getDestinationEngineType() {
        return DESTINATION_ENGINE_TYPE;
    }

    @Override
    public boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList) {
        // TODO
        return false;
    }
}
