package com.andreidodu.fromgtog.mapper;

import com.andreidodu.fromgtog.dto.RepositoryDTO;
import org.apache.commons.lang3.BooleanUtils;
import org.gitlab4j.api.models.Project;

public class GitlabRepositoryMapper {

    public RepositoryDTO toDTO(Project project) {

        return RepositoryDTO.builder()
                .login(project.getNamespace().getFullPath())
                .name(project.getName())
                .description(project.getDescription())
                .cloneAddress(project.getHttpUrlToRepo())
                .privateFlag(BooleanUtils.isFalse(project.getPublic()))
                .archivedFlag(project.getArchived())
                .build();
    }

}
