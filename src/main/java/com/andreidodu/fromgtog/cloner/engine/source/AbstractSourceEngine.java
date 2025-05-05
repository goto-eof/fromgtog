package com.andreidodu.fromgtog.cloner.engine.source;

import com.andreidodu.fromgtog.cloner.type.SourceEngineType;

public abstract class AbstractSourceEngine implements SourceEngine {

    @Override
    public boolean accept(SourceEngineType sourceEngineType) {
        return getSourceEngineType().equals(sourceEngineType);
    }
}
