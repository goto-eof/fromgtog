package com.andreidodu.fromgtog.dto;

import lombok.Builder;

@Builder
public record SettingsContext(int sleepTimeSeconds, boolean multithreadingEnabled, boolean chronJobEnabled,
                              String chronExpression) {
}
