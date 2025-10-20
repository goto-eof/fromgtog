package org.andreidodu.fromgtog.dto;

import lombok.Builder;

@Builder
public record EngineContext(
        SettingsContext settingsContext,
        FromContext fromContext,
        ToContext toContext,
        CallbackContainer callbackContainer
) {
}
