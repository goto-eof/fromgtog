package com.andreidodu.fromgtog.cloner.engine.source.realengine;

import com.andreidodu.fromgtog.cloner.dto.EngineContext;
import com.andreidodu.fromgtog.cloner.dto.RepositoryDTO;
import com.andreidodu.fromgtog.cloner.engine.source.AbstractSourceEngine;
import com.andreidodu.fromgtog.cloner.type.SourceEngineType;

import java.util.List;

public class GithubSourceEngine extends AbstractSourceEngine {

    private final static SourceEngineType SOURCE_ENGINE_TYPE = SourceEngineType.GITHUB;

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
