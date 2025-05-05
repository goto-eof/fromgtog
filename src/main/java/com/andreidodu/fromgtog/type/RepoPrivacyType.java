package com.andreidodu.fromgtog.type;

import lombok.Getter;

@Getter
public enum RepoPrivacyType {
    ALL_PRIVATE(0), ALL_PUBLIC(1), ALL_DEFAULT(2);
    final int value;

    RepoPrivacyType(int value) {
        this.value = value;
    }

    public static RepoPrivacyType fromValue(int value) {
        for (RepoPrivacyType enumValue : RepoPrivacyType.values()) {
            if (enumValue.getValue() == value) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
