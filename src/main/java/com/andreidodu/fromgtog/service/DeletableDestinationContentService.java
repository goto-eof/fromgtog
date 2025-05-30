package com.andreidodu.fromgtog.service;

import com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic.GenericDestinationEngineFromStrategyService;

public interface DeletableDestinationContentService extends GenericDestinationEngineFromStrategyService {

    void deleteAllRepositories(String baseUrl, String token);

}
