package com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records;

import lombok.Builder;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Builder
public record ThreadStopCommandContext(
        Supplier<Boolean> isShouldStopSupplier,
        Consumer<String> updateApplicationStatusMessageConsumer,
        String repositoryName
) {
}
