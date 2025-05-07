package com.andreidodu.fromgtog.service.factory.from;

import com.andreidodu.fromgtog.type.EngineType;

public abstract class AbstractSourceEngine implements SourceEngine {

    @Override
    public boolean accept(EngineType sourceEngineType) {
        return getEngineType().equals(sourceEngineType);
    }
}
