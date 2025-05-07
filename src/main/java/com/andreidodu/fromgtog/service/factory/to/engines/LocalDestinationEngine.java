package com.andreidodu.fromgtog.service.factory.to.engines;

import com.andreidodu.fromgtog.service.factory.to.AbstractDestinationEngine;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.local.LocalDestinationEngineFromLocaleStrategy;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.local.LocalDestinationEngineFromRemoteStrategy;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.DestinationEngineStrategy;
import com.andreidodu.fromgtog.type.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LocalDestinationEngine extends AbstractDestinationEngine {
    private final static EngineType DESTINATION_ENGINE_TYPE = EngineType.LOCAL;
    private final static Logger log = LoggerFactory.getLogger(LocalDestinationEngine.class);
    public static final List<DestinationEngineStrategy> STRATEGIES_LIST = List.of(new LocalDestinationEngineFromRemoteStrategy(), new LocalDestinationEngineFromLocaleStrategy());

    public EngineType getDestinationEngineType() {
        return DESTINATION_ENGINE_TYPE;
    }

    public List<DestinationEngineStrategy> getStrategies() {
        return STRATEGIES_LIST;
    }
}
