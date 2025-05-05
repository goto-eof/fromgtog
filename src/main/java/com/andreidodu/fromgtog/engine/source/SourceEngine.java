package com.andreidodu.fromgtog.engine.source;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public interface SourceEngine {

    EngineType getEngineType();

    boolean accept(EngineType sourceEngineType);

    List<RepositoryDTO> retrieveRepositoryList(EngineContext sourceEngineInputContext);

}
