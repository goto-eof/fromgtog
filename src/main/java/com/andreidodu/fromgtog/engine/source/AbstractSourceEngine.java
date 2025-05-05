package com.andreidodu.fromgtog.engine.source;

import com.andreidodu.fromgtog.type.SourceEngineType;

public abstract class AbstractSourceEngine implements SourceEngine {

    @Override
    public boolean accept(SourceEngineType sourceEngineType) {
        return getSourceEngineType().equals(sourceEngineType);
    }
}
