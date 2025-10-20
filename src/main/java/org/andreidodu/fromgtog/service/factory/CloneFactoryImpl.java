package org.andreidodu.fromgtog.service.factory;

import org.andreidodu.fromgtog.exception.DestinationEngineNotFoundException;
import org.andreidodu.fromgtog.exception.SourceEngineNotFoundException;
import org.andreidodu.fromgtog.service.factory.from.SourceEngine;
import org.andreidodu.fromgtog.service.factory.from.engines.GiteaSourceEngine;
import org.andreidodu.fromgtog.service.factory.from.engines.GithubSourceEngine;
import org.andreidodu.fromgtog.service.factory.from.engines.GitlabSourceEngine;
import org.andreidodu.fromgtog.service.factory.from.engines.LocalSourceEngine;
import org.andreidodu.fromgtog.service.factory.to.engines.*;
import org.andreidodu.fromgtog.service.factory.to.engines.*;
import org.andreidodu.fromgtog.type.EngineType;

import java.util.Arrays;

public class CloneFactoryImpl implements CloneFactory {

    private final SourceEngine[] sourceEngineArray = {
            new GithubSourceEngine(),
            new GiteaSourceEngine(),
            new GitlabSourceEngine(),
            new LocalSourceEngine()
    };

    private final DestinationEngine[] destinationEngineArray = {
            new GithubDestinationEngine(),
            new GiteaDestinationEngine(),
            new GitlabDestinationEngine(),
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
