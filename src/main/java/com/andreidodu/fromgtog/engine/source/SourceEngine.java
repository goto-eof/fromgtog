package com.andreidodu.fromgtog.engine.source;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.type.SourceEngineType;

import java.util.List;

public interface SourceEngine {

    SourceEngineType getSourceEngineType();

    boolean accept(SourceEngineType sourceEngineType);

    List<RepositoryDTO> retrieveRepositoryList(EngineContext sourceEngineInputContext);

}
