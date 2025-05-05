package com.andreidodu.fromgtog.cloner.dto;

import com.andreidodu.fromgtog.cloner.type.SourceEngineType;
import lombok.Builder;

@Builder
public record FromContext(
        SourceEngineType sourceEngineType,

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
