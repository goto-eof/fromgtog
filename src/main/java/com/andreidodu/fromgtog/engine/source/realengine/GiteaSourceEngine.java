package com.andreidodu.fromgtog.engine.source.realengine;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.engine.source.AbstractSourceEngine;
import com.andreidodu.fromgtog.type.SourceEngineType;

import java.util.List;

public class GiteaSourceEngine extends AbstractSourceEngine {

    private final static SourceEngineType SOURCE_ENGINE_TYPE = SourceEngineType.GITEA;

    @Override
    public SourceEngineType getSourceEngineType() {
        return SOURCE_ENGINE_TYPE;
    }

    @Override
    public List<RepositoryDTO> retrieveRepositoryList(EngineContext engineContext) {
        // TODO
        return List.of();
    }
}
