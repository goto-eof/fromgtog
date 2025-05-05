package com.andreidodu.fromgtog.cloner.dto;

import lombok.Builder;

@Builder
public record EngineContext(
        SettingsContext settingsContext,
        FromContext fromContext,
        ToContext toContext,
        CallbackContainer callbackContainer
) {
}
