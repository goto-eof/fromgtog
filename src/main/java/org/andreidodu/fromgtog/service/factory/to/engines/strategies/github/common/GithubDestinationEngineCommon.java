package org.andreidodu.fromgtog.service.factory.to.engines.strategies.github.common;

import org.andreidodu.fromgtog.dto.EngineContext;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.RemoteExistsCheckCommandContext;

public class GithubDestinationEngineCommon {

    public static RemoteExistsCheckCommandContext buildRemoteDestinationExistsCheckInput(EngineContext engineContext, String tokenOwnerLogin, String repositoryName) {
        return RemoteExistsCheckCommandContext.builder()
                .updateApplicationStatusMessagesConsumer(engineContext.callbackContainer().updateLogAndApplicationStatusMessage())
                .login(tokenOwnerLogin)
                .token(engineContext.toContext().token())
                .baseUrl("https://github.com")
                .repositoryName(repositoryName)
                .build();
    }

}
