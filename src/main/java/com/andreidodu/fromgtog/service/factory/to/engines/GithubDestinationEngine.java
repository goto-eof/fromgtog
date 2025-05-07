package com.andreidodu.fromgtog.service.factory.to.engines;

import com.andreidodu.fromgtog.service.factory.to.AbstractDestinationEngine;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.github.GithubDestinationEngineFromLocaleStrategy;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.github.GithubDestinationEngineFromRemoteStrategy;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.DestinationEngineStrategy;
import com.andreidodu.fromgtog.type.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GithubDestinationEngine extends AbstractDestinationEngine {
    private final static EngineType DESTINATION_ENGINE_TYPE = EngineType.GITHUB;
    public static final List<DestinationEngineStrategy> STRATEGIES_LIST = List.of(new GithubDestinationEngineFromLocaleStrategy(), new GithubDestinationEngineFromRemoteStrategy());
    Logger log = LoggerFactory.getLogger(GithubDestinationEngine.class);

    public EngineType getDestinationEngineType() {
        return DESTINATION_ENGINE_TYPE;
    }

    public List<DestinationEngineStrategy> getStrategies() {
        return STRATEGIES_LIST;
    }

}
