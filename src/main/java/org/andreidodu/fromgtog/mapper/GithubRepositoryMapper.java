package org.andreidodu.fromgtog.mapper;

import org.andreidodu.fromgtog.dto.RepositoryDTO;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

public class GithubRepositoryMapper {

    public RepositoryDTO toDTO(GHRepository ghRepository, GHUser repositoryUser) {

        return RepositoryDTO.builder()
                .login(repositoryUser.getLogin())
                .name(ghRepository.getName())
                .description(ghRepository.getDescription())
                .cloneAddress(ghRepository.getHttpTransportUrl())
                .privateFlag(ghRepository.isPrivate())
                .archivedFlag(ghRepository.isArchived())
                .build();
    }

}
