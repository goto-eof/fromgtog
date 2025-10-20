package org.andreidodu.fromgtog.service;

import org.andreidodu.fromgtog.dto.DeleteRepositoryRequestDTO;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.generic.GenericDestinationEngineFromStrategyService;

public interface DeletableDestinationContentService extends GenericDestinationEngineFromStrategyService {

    void deleteAllRepositories(String baseUrl, String token);

    boolean deleteRepository(DeleteRepositoryRequestDTO deleteRepositoryRequestDTO);

}
