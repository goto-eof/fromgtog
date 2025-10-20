package org.andreidodu.fromgtog.service.factory.to.engines.strategies.generic;

import org.andreidodu.fromgtog.dto.EngineContext;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.RemoteExistsCheckCommandContext;

public class GenericDestinationEngineCommon {

    public static RemoteExistsCheckCommandContext buildRemoteExistsCheckInput(EngineContext engineContext, String tokenOwnerLogin, String repositoryName) {
        return RemoteExistsCheckCommandContext.builder()
                .updateApplicationStatusMessagesConsumer(engineContext.callbackContainer().updateLogAndApplicationStatusMessage())
                .login(tokenOwnerLogin)
                .token(engineContext.toContext().token())
                .baseUrl(engineContext.toContext().url())
                .repositoryName(repositoryName)
                .build();
    }
}
