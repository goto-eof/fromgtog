package org.andreidodu.fromgtog.service;

import org.andreidodu.fromgtog.dto.gitea.GiteaRepositoryDTO;
import org.andreidodu.fromgtog.dto.gitea.GiteaUserDTO;

import java.io.IOException;
import java.util.List;

public interface GiteaService extends DeletableDestinationContentService {
    GiteaUserDTO getMyself(String token, String urlString);

    List<GiteaRepositoryDTO> tryToRetrieveUserRepositories(String baseUrl, String token);

    List<GiteaRepositoryDTO> tryToRetrieveStarredRepositories(String baseUrl, String token);

    boolean updateRepositoryPrivacy(String token, String ownerName, String giteaUrl, String repositoryName, boolean isArchived, boolean isPrivate) throws IOException, InterruptedException;

    boolean createRepository(String baseUrl, String token, String repoName, String description, boolean isPrivate);

}
