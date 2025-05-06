package com.andreidodu.fromgtog.dto;

import lombok.Builder;

import java.util.function.Consumer;

@Builder
public record CallbackContainer(
        Consumer<Integer> updateApplicationProgressBarMax,
        Consumer<Integer> updateApplicationProgressBarCurrent,
        Consumer<String> updateApplicationStatusMessage
) {
}
