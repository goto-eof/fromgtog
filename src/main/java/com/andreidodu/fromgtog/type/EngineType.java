package com.andreidodu.fromgtog.type;

import lombok.Getter;

@Getter
public enum EngineType {
    GITHUB(0), GITEA(1), LOCAL(2);

    private final int value;

    EngineType(int value) {
        this.value = value;
    }

    public static EngineType fromValue(int value) {
        for (EngineType enumValue : EngineType.values()) {
            if (enumValue.getValue() == value) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
