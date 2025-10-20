package org.andreidodu.fromgtog.type;

import lombok.Getter;

@Getter
public enum EngineOptionsType {
    FILTER(0), FILE(1);

    private final int value;

    EngineOptionsType(int value) {
        this.value = value;
    }

    public static EngineOptionsType fromValue(int value) {
        for (EngineOptionsType enumValue : EngineOptionsType.values()) {
            if (enumValue.getValue() == value) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
