package com.andreidodu.fromgtog.engine.factory.destination;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public interface DestinationEngine {

    EngineType getDestinationEngineType();

    boolean accept(EngineType sourceEngineType);

    boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList);

}
