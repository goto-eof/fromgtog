package com.andreidodu.fromgtog.service.factory.to.engines.strategies.github;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.RemoteExistsCheckCommandContext;

public class GithubDestinationEngineCommon {

    public static RemoteExistsCheckCommandContext buildRemoteExistsCheckInput(EngineContext engineContext, String tokenOwnerLogin, String repositoryName) {
        return RemoteExistsCheckCommandContext.builder()
                .updateApplicationStatusMessagesConsumer(engineContext.callbackContainer().updateApplicationStatusMessage())
                .login(tokenOwnerLogin)
                .token(engineContext.toContext().token())
                .baseUrl("https://github.com")
                .repositoryName(repositoryName)
                .build();
    }

}
