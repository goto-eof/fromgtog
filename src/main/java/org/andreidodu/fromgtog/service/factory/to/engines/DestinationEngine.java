package org.andreidodu.fromgtog.service.factory.to.engines;

import org.andreidodu.fromgtog.dto.EngineContext;
import org.andreidodu.fromgtog.dto.RepositoryDTO;
import org.andreidodu.fromgtog.service.factory.Engine;
import org.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public interface DestinationEngine extends Engine {

    EngineType getDestinationEngineType();

    boolean accept(EngineType sourceEngineType);

    boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList);

}
