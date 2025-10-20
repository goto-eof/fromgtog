package org.andreidodu.fromgtog.service.factory.to.engines.strategies.common;

import org.andreidodu.fromgtog.dto.EngineContext;
import org.andreidodu.fromgtog.dto.RepositoryDTO;
import org.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public interface DestinationEngineStrategy {
    boolean isApplicable(EngineType engineType);

    boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList);


}
