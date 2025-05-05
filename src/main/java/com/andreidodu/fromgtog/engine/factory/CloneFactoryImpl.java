package com.andreidodu.fromgtog.engine.factory;

import com.andreidodu.fromgtog.type.DestinationEngineType;
import com.andreidodu.fromgtog.type.SourceEngineType;
import com.andreidodu.fromgtog.engine.destination.DestinationEngine;
import com.andreidodu.fromgtog.engine.destination.realengine.GiteaDestinationEngine;
import com.andreidodu.fromgtog.engine.destination.realengine.GithubDestinationEngine;
import com.andreidodu.fromgtog.engine.destination.realengine.LocalDestinationEngine;
import com.andreidodu.fromgtog.exception.DestinationEngineNotFoundException;
import com.andreidodu.fromgtog.exception.SourceEngineNotFoundException;
import com.andreidodu.fromgtog.engine.source.SourceEngine;
import com.andreidodu.fromgtog.engine.source.realengine.GiteaSourceEngine;
import com.andreidodu.fromgtog.engine.source.realengine.GithubSourceEngine;
import com.andreidodu.fromgtog.engine.source.realengine.LocalSourceEngine;

import java.util.Arrays;

public class CloneFactoryImpl implements CloneFactory {

    private final SourceEngine[] sourceEngineArray = {
            new GithubSourceEngine(),
            new GiteaSourceEngine(),
            new LocalSourceEngine()
    };

    private final DestinationEngine[] destinationEngineArray = {
            new GithubDestinationEngine(),
            new GiteaDestinationEngine(),
            new LocalDestinationEngine()
    };

    @Override
    public SourceEngine buildSource(SourceEngineType sourceEngineType) {
        return Arrays.stream(sourceEngineArray)
                .filter(sourceEngine -> sourceEngine.accept(sourceEngineType))
                .findFirst()
                .orElseThrow(SourceEngineNotFoundException::new);
    }

    @Override
    public DestinationEngine buildDestination(DestinationEngineType destinationEngineType) {
        return Arrays.stream(destinationEngineArray)
                .filter(destinationEngine -> destinationEngine.accept(destinationEngineType))
                .findFirst()
                .orElseThrow(DestinationEngineNotFoundException::new);
    }
}
