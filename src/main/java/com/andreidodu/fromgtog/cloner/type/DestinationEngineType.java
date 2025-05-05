package com.andreidodu.fromgtog.cloner.type;

import lombok.Getter;

@Getter
public enum DestinationEngineType {
    GITHUB(1), GITEA(2), LOCAL(3);

    private final int value;

    DestinationEngineType(int value) {
        this.value = value;
    }
}
