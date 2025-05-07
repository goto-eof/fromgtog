package com.andreidodu.fromgtog.service.factory;

import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.service.factory.to.engines.DestinationEngine;
import com.andreidodu.fromgtog.service.factory.to.engines.GiteaDestinationEngine;
import com.andreidodu.fromgtog.service.factory.to.engines.GithubDestinationEngine;
import com.andreidodu.fromgtog.service.factory.to.engines.LocalDestinationEngine;
import com.andreidodu.fromgtog.exception.DestinationEngineNotFoundException;
import com.andreidodu.fromgtog.exception.SourceEngineNotFoundException;
import com.andreidodu.fromgtog.service.factory.from.SourceEngine;
import com.andreidodu.fromgtog.service.factory.from.engines.GiteaSourceEngine;
import com.andreidodu.fromgtog.service.factory.from.engines.GithubSourceEngine;
import com.andreidodu.fromgtog.service.factory.from.engines.LocalSourceEngine;

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
    public SourceEngine buildSource(EngineType sourceEngineType) {
        return Arrays.stream(sourceEngineArray)
                .filter(sourceEngine -> sourceEngine.accept(sourceEngineType))
                .findFirst()
                .orElseThrow(SourceEngineNotFoundException::new);
    }

    @Override
    public DestinationEngine buildDestination(EngineType engineType) {
        return Arrays.stream(destinationEngineArray)
                .filter(destinationEngine -> destinationEngine.accept(engineType))
                .findFirst()
                .orElseThrow(DestinationEngineNotFoundException::new);
    }
}
