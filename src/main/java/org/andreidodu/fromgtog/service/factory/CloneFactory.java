package org.andreidodu.fromgtog.service.factory;

import org.andreidodu.fromgtog.service.factory.from.SourceEngine;
import org.andreidodu.fromgtog.service.factory.to.engines.DestinationEngine;
import org.andreidodu.fromgtog.type.EngineType;

public interface CloneFactory {
    SourceEngine buildSource(EngineType sourceEngineType);

    DestinationEngine buildDestination(EngineType engineType);
}
