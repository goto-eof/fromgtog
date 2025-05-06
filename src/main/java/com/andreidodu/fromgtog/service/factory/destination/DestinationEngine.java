package com.andreidodu.fromgtog.service.factory.destination;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.service.factory.Engine;
import com.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public interface DestinationEngine extends Engine {

    EngineType getDestinationEngineType();

    boolean accept(EngineType sourceEngineType);

    boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList);

}
