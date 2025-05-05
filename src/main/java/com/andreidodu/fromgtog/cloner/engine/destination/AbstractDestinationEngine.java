package com.andreidodu.fromgtog.cloner.engine.destination;

import com.andreidodu.fromgtog.cloner.type.DestinationEngineType;

public abstract class AbstractDestinationEngine implements DestinationEngine {

    @Override
    public boolean accept(DestinationEngineType sourceEngineType) {
        return getDestinationEngineType().equals(sourceEngineType);
    }
}
