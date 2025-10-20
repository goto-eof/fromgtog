package org.andreidodu.fromgtog.service.factory.from;

import org.andreidodu.fromgtog.dto.EngineContext;
import org.andreidodu.fromgtog.dto.RepositoryDTO;
import org.andreidodu.fromgtog.service.factory.Engine;
import org.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public interface SourceEngine extends Engine {

    EngineType getEngineType();

    boolean accept(EngineType sourceEngineType);

    List<RepositoryDTO> retrieveRepositoryList(EngineContext sourceEngineInputContext);

}
