package com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records;

import lombok.Builder;

import java.util.function.Consumer;

@Builder
public record RemoteExistsCheckCommandContext(
        Consumer<String> updateApplicationStatusMessagesConsumer,
        String login,
        String token,
        String baseUrl,
        String repositoryName
) {
}
