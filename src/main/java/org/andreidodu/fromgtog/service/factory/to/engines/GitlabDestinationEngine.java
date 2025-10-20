package org.andreidodu.fromgtog.service.factory.to.engines;

import org.andreidodu.fromgtog.service.DeletableDestinationContentService;
import org.andreidodu.fromgtog.service.factory.to.AbstractDestinationEngine;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.DestinationEngineStrategy;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.generic.GenericDestinationEngineFromLocalStrategy;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.generic.GenericDestinationEngineFromRemoteStrategy;
import org.andreidodu.fromgtog.service.impl.GitlabServiceImpl;
import org.andreidodu.fromgtog.type.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GitlabDestinationEngine extends AbstractDestinationEngine {

    public static final List<DestinationEngineStrategy> STRATEGIES_LIST;
    private final static EngineType DESTINATION_ENGINE_TYPE = EngineType.GITLAB;

    static {
        DeletableDestinationContentService gitlabService = GitlabServiceImpl.getInstance();
        STRATEGIES_LIST = List.of(
                new GenericDestinationEngineFromRemoteStrategy<DeletableDestinationContentService>(gitlabService),
                new GenericDestinationEngineFromLocalStrategy<DeletableDestinationContentService>(gitlabService)
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
