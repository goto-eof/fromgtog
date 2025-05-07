package com.andreidodu.fromgtog.dto;

import lombok.Builder;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Builder
public record CallbackContainer(
        Consumer<Integer> updateApplicationProgressBarMax,
        Consumer<Integer> updateApplicationProgressBarCurrent,
        Consumer<String> updateApplicationStatusMessage,
        Consumer<Boolean> setEnabledUI,
        Consumer<String> showErrorMessage,
        Consumer<String> showSuccessMessage,
        Supplier<Boolean> isShouldStop,
        Consumer<Boolean> setShouldStop
) {
}
