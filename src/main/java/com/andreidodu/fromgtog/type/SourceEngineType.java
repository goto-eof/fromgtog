package com.andreidodu.fromgtog.type;

import lombok.Getter;

@Getter
public enum SourceEngineType {
    GITHUB(1), GITEA(2), LOCAL(3);

    private final int value;

    SourceEngineType(int value) {
        this.value = value;
    }
}
