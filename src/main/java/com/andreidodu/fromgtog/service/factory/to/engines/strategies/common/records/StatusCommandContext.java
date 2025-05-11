package com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records;

import lombok.Builder;

import java.util.function.Consumer;

@Builder
public record StatusCommandContext(
        Consumer<Integer> updateProgressMaxConsumer,
        Consumer<Integer> updateProgressCurrentConsumer,
        Consumer<String> updateStatusMessageConsumer,
        Integer progressMaxValue,
        Integer progressCurrentValue,
        String messageStatus
) {
}
