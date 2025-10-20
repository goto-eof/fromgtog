package org.andreidodu.fromgtog.dto;

import org.andreidodu.fromgtog.type.EngineType;
import org.andreidodu.fromgtog.type.RepoPrivacyType;
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
