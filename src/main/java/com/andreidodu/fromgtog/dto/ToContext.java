package com.andreidodu.fromgtog.dto;

import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.type.RepoPrivacyType;
import lombok.Builder;

@Builder
public record ToContext(
        EngineType engineType,
        String url,
        String token,
        RepoPrivacyType repositoryPrivacy,
        String rootPath,
        boolean groupByRepositoryOwner,
        boolean overrideIfExists
) {
}
