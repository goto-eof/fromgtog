package org.andreidodu.fromgtog.service.factory.to.engines.strategies.github;

import org.andreidodu.fromgtog.dto.EngineContext;
import org.andreidodu.fromgtog.dto.RepositoryDTO;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.DestinationEngineStrategy;

import java.util.List;

public interface GithubDestinationEngineFromStrategy extends DestinationEngineStrategy {
    boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList);

}