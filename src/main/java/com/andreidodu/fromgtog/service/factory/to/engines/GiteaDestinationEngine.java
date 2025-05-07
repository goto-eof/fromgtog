package com.andreidodu.fromgtog.service.factory.to.engines;

import com.andreidodu.fromgtog.service.factory.to.AbstractDestinationEngine;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.gitea.GiteaDestinationEngineFromLocalStrategy;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.gitea.GiteaDestinationEngineFromRemoteStrategy;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.DestinationEngineStrategy;
import com.andreidodu.fromgtog.type.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GiteaDestinationEngine extends AbstractDestinationEngine {

    private final static EngineType DESTINATION_ENGINE_TYPE = EngineType.GITEA;
    public static final List<DestinationEngineStrategy> STRATEGIES_LIST = List.of(new GiteaDestinationEngineFromRemoteStrategy(), new GiteaDestinationEngineFromLocalStrategy());
    Logger log = LoggerFactory.getLogger(GiteaDestinationEngine.class);

    public EngineType getDestinationEngineType() {
        return DESTINATION_ENGINE_TYPE;
    }


    public List<DestinationEngineStrategy> getStrategies() {
        return STRATEGIES_LIST;
    }

}
