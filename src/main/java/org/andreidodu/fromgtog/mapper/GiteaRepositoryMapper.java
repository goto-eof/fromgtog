package org.andreidodu.fromgtog.mapper;

import org.andreidodu.fromgtog.dto.RepositoryDTO;
import org.andreidodu.fromgtog.dto.gitea.GiteaRepositoryDTO;

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
