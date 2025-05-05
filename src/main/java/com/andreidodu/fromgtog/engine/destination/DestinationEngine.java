package com.andreidodu.fromgtog.engine.destination;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.type.DestinationEngineType;

import java.util.List;

public interface DestinationEngine {

    DestinationEngineType getDestinationEngineType();

    boolean accept(DestinationEngineType sourceEngineType);

    boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList);

}
