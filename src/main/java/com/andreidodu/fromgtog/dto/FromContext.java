package com.andreidodu.fromgtog.dto;

import com.andreidodu.fromgtog.type.EngineOptionsType;
import com.andreidodu.fromgtog.type.EngineType;
import lombok.Builder;

@Builder
public record FromContext(
        EngineType sourceEngineType,

        String url,

        String login,
        String token,
        boolean cloneStarredReposFlag,
        boolean cloneForkedReposFlag,
        boolean clonePrivateReposFlag,
        boolean clonePublicReposFlag,
        boolean cloneArchivedReposFlag,
        boolean cloneBelongingToOrganizationsReposFlag,
        String excludeOrganizations,

        String rootPath,
        EngineOptionsType engineOptionsType,
        String excludeRepoNameList,
        String includeRepoNameFileNameList
) {
}
