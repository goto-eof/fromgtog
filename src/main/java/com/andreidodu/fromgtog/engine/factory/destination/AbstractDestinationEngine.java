package com.andreidodu.fromgtog.engine.factory.destination;

import com.andreidodu.fromgtog.type.EngineType;

public abstract class AbstractDestinationEngine implements DestinationEngine {

    @Override
    public boolean accept(EngineType sourceEngineType) {
        return getDestinationEngineType().equals(sourceEngineType);
    }
}
