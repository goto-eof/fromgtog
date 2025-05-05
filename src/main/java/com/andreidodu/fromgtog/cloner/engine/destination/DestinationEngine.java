package com.andreidodu.fromgtog.cloner.engine.destination;

import com.andreidodu.fromgtog.cloner.dto.EngineContext;
import com.andreidodu.fromgtog.cloner.dto.RepositoryDTO;
import com.andreidodu.fromgtog.cloner.type.DestinationEngineType;

import java.util.List;

public interface DestinationEngine {

    DestinationEngineType getDestinationEngineType();

    boolean accept(DestinationEngineType sourceEngineType);

    boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList);

}
