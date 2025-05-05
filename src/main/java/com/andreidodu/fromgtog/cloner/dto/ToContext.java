package com.andreidodu.fromgtog.cloner.dto;

import com.andreidodu.fromgtog.cloner.type.DestinationEngineType;
import com.andreidodu.fromgtog.cloner.type.RepoPrivacyType;
import lombok.Builder;

@Builder
public record ToContext(
        DestinationEngineType destinationEngineType,

        String url,
        String token,
        RepoPrivacyType repositoryPrivacy,
        String rootPath,
        boolean groupByRepositoryOwner
) {
}
