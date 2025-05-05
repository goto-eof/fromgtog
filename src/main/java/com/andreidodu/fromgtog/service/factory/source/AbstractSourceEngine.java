package com.andreidodu.fromgtog.service.factory.source;

import com.andreidodu.fromgtog.type.EngineType;

public abstract class AbstractSourceEngine implements SourceEngine {

    @Override
    public boolean accept(EngineType sourceEngineType) {
        return getEngineType().equals(sourceEngineType);
    }
}
