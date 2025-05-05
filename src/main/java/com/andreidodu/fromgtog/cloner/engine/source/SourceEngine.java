package com.andreidodu.fromgtog.cloner.engine.source;

import com.andreidodu.fromgtog.cloner.dto.EngineContext;
import com.andreidodu.fromgtog.cloner.dto.RepositoryDTO;
import com.andreidodu.fromgtog.cloner.type.SourceEngineType;

import java.util.List;

public interface SourceEngine {

    SourceEngineType getSourceEngineType();

    boolean accept(SourceEngineType sourceEngineType);

    List<RepositoryDTO> retrieveRepositoryList(EngineContext sourceEngineInputContext);

}
