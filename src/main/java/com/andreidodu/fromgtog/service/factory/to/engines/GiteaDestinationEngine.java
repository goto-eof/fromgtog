package com.andreidodu.fromgtog.service.factory.to.engines;

import com.andreidodu.fromgtog.service.factory.to.AbstractDestinationEngine;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.DestinationEngineStrategy;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic.GenericDestinationEngineFromLocalStrategy;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic.GenericDestinationEngineFromRemoteStrategy;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic.GenericDestinationEngineFromStrategyService;
import com.andreidodu.fromgtog.service.impl.GiteaServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GiteaDestinationEngine extends AbstractDestinationEngine {

    public static final List<DestinationEngineStrategy> STRATEGIES_LIST;
    private final static EngineType DESTINATION_ENGINE_TYPE = EngineType.GITEA;

    static {
        GenericDestinationEngineFromStrategyService giteaService = GiteaServiceImpl.getInstance();
        STRATEGIES_LIST = List.of(
                new GenericDestinationEngineFromRemoteStrategy<GenericDestinationEngineFromStrategyService>(giteaService),
                new GenericDestinationEngineFromLocalStrategy<GenericDestinationEngineFromStrategyService>(giteaService)
        );
    }

    Logger log = LoggerFactory.getLogger(GiteaDestinationEngine.class);

    public EngineType getDestinationEngineType() {
        return DESTINATION_ENGINE_TYPE;
    }


    public List<DestinationEngineStrategy> getStrategies() {
        return STRATEGIES_LIST;
    }

}
