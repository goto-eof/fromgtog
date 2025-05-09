package com.andreidodu.fromgtog.service.factory.to.engines.strategies.common;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public interface DestinationEngineStrategy {
    boolean accept(EngineType engineType);

    boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList);


}
