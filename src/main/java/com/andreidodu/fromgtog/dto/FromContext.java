package com.andreidodu.fromgtog.dto;

import com.andreidodu.fromgtog.type.EngineType;
import lombok.Builder;

@Builder
public record FromContext(
        EngineType sourceEngineType,

        String url,

        String token,
        boolean cloneStarredReposFlag,
        boolean cloneForkedReposFlag,
        boolean clonePrivateReposFlag,
        boolean cloneArchivedReposFlag,
        boolean cloneBelongingToOrganizationsReposFlag,
        String excludeOrganizations,

        String rootPath
) {
}
