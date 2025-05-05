package com.andreidodu.fromgtog.service.factory;

import com.andreidodu.fromgtog.service.factory.destination.DestinationEngine;
import com.andreidodu.fromgtog.service.factory.source.SourceEngine;
import com.andreidodu.fromgtog.type.EngineType;

public interface CloneFactory {
    SourceEngine buildSource(EngineType sourceEngineType);

    DestinationEngine buildDestination(EngineType engineType);
}
