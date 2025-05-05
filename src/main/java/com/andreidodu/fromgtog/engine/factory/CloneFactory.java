package com.andreidodu.fromgtog.engine.factory;

import com.andreidodu.fromgtog.engine.factory.destination.DestinationEngine;
import com.andreidodu.fromgtog.engine.factory.source.SourceEngine;
import com.andreidodu.fromgtog.type.EngineType;

public interface CloneFactory {
    SourceEngine buildSource(EngineType sourceEngineType);

    DestinationEngine buildDestination(EngineType engineType);
}
