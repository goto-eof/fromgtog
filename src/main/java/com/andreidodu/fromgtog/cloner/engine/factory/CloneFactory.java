package com.andreidodu.fromgtog.cloner.engine.factory;

import com.andreidodu.fromgtog.cloner.type.DestinationEngineType;
import com.andreidodu.fromgtog.cloner.type.SourceEngineType;
import com.andreidodu.fromgtog.cloner.engine.destination.DestinationEngine;
import com.andreidodu.fromgtog.cloner.engine.source.SourceEngine;

public interface CloneFactory {
    SourceEngine buildSource(SourceEngineType sourceEngineType);

    DestinationEngine buildDestination(DestinationEngineType destinationEngineType);
}
