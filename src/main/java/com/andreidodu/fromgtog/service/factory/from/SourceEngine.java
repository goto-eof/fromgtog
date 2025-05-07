package com.andreidodu.fromgtog.service.factory.from;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.service.factory.Engine;
import com.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public interface SourceEngine extends Engine {

    EngineType getEngineType();

    boolean accept(EngineType sourceEngineType);

    List<RepositoryDTO> retrieveRepositoryList(EngineContext sourceEngineInputContext);

}
