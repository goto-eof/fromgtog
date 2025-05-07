package com.andreidodu.fromgtog.service.factory;

import com.andreidodu.fromgtog.service.factory.to.engines.DestinationEngine;
import com.andreidodu.fromgtog.service.factory.from.SourceEngine;
import com.andreidodu.fromgtog.type.EngineType;

public interface CloneFactory {
    SourceEngine buildSource(EngineType sourceEngineType);

    DestinationEngine buildDestination(EngineType engineType);
}
