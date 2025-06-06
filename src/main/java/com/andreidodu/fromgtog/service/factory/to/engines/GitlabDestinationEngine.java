package com.andreidodu.fromgtog.service.factory.to.engines;

import com.andreidodu.fromgtog.service.factory.to.AbstractDestinationEngine;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.DestinationEngineStrategy;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic.GenericDestinationEngineFromLocalStrategy;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic.GenericDestinationEngineFromRemoteStrategy;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic.GenericDestinationEngineFromStrategyService;
import com.andreidodu.fromgtog.service.impl.GitlabServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GitlabDestinationEngine extends AbstractDestinationEngine {

    private final static EngineType DESTINATION_ENGINE_TYPE = EngineType.GITLAB;
    public static final List<DestinationEngineStrategy> STRATEGIES_LIST;

    static {
        GenericDestinationEngineFromStrategyService gitlabService = GitlabServiceImpl.getInstance();
        STRATEGIES_LIST = List.of(
                new GenericDestinationEngineFromRemoteStrategy<GenericDestinationEngineFromStrategyService>(gitlabService),
                new GenericDestinationEngineFromLocalStrategy<GenericDestinationEngineFromStrategyService>(gitlabService)
        );
    }

    Logger log = LoggerFactory.getLogger(GitlabDestinationEngine.class);

    public EngineType getDestinationEngineType() {
        return DESTINATION_ENGINE_TYPE;
    }


    public List<DestinationEngineStrategy> getStrategies() {
        return STRATEGIES_LIST;
    }

}
