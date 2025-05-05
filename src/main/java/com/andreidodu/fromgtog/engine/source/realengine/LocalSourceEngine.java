package com.andreidodu.fromgtog.engine.source.realengine;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.engine.source.AbstractSourceEngine;
import com.andreidodu.fromgtog.type.EngineType;


import java.util.List;

public class LocalSourceEngine extends AbstractSourceEngine {

    private final static EngineType SOURCE_ENGINE_TYPE = EngineType.LOCAL;

    @Override
    public EngineType getEngineType() {
        return SOURCE_ENGINE_TYPE;
    }

    @Override
    public List<RepositoryDTO> retrieveRepositoryList(EngineContext engineContext) {
        // TODO
        return List.of();
    }
}
