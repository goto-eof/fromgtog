package com.andreidodu.fromgtog.engine.factory;

import com.andreidodu.fromgtog.type.DestinationEngineType;
import com.andreidodu.fromgtog.type.SourceEngineType;
import com.andreidodu.fromgtog.engine.destination.DestinationEngine;
import com.andreidodu.fromgtog.engine.source.SourceEngine;

public interface CloneFactory {
    SourceEngine buildSource(SourceEngineType sourceEngineType);

    DestinationEngine buildDestination(DestinationEngineType destinationEngineType);
}
