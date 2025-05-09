package com.andreidodu.fromgtog.service.factory.to.engines.strategies.github;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.DestinationEngineStrategy;

import java.util.List;

public interface GithubDestinationEngineFromStrategy extends DestinationEngineStrategy {
    boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList);

}