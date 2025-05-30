package com.andreidodu.fromgtog.mapper;

import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.dto.gitea.GiteaRepositoryDTO;

public class GiteaRepositoryMapper {

    public RepositoryDTO toDTO(GiteaRepositoryDTO giteaRepositoryDTO) {

        return RepositoryDTO.builder()
                .login(giteaRepositoryDTO.getOwner().getLogin())
                .name(giteaRepositoryDTO.getName())
                .description(giteaRepositoryDTO.getDescription())
                .cloneAddress(giteaRepositoryDTO.getCloneUrl())
                .privateFlag(giteaRepositoryDTO.isPrivate())
                .archivedFlag(giteaRepositoryDTO.isArchived())
                .build();
    }

}
