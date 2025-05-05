package com.andreidodu.fromgtog.engine.destination;

import com.andreidodu.fromgtog.type.DestinationEngineType;

public abstract class AbstractDestinationEngine implements DestinationEngine {

    @Override
    public boolean accept(DestinationEngineType sourceEngineType) {
        return getDestinationEngineType().equals(sourceEngineType);
    }
}
