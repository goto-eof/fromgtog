package com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.RemoteExistsCheckCommandContext;

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
