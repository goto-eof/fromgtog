package com.andreidodu.fromgtog.service.factory.from.engines.common;

import java.util.List;
import java.util.stream.Stream;

public class SourceEngineCommon {

    public List<String> buildOrganizationBlacklist(String excludedOrganizations) {
        return Stream.of(excludedOrganizations.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .distinct()
                .toList();
    }
}
