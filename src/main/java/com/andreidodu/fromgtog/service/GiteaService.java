package com.andreidodu.fromgtog.service;

import com.andreidodu.fromgtog.dto.gitea.GiteaRepositoryDTO;
import com.andreidodu.fromgtog.dto.gitea.GiteaUserDTO;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic.GenericDestinationEngineFromStrategyService;

import java.io.IOException;
import java.util.List;

public interface GiteaService extends GenericDestinationEngineFromStrategyService {
    GiteaUserDTO getMyself(String token, String urlString);

    List<GiteaRepositoryDTO> tryToRetrieveUserRepositories(String baseUrl, String token);

    List<GiteaRepositoryDTO> tryToRetrieveStarredRepositories(String baseUrl, String token);

    boolean updateRepositoryPrivacy(String token, String ownerName, String giteaUrl, String repositoryName, boolean isArchived, boolean isPrivate) throws IOException, InterruptedException;

    boolean createRepository(String baseUrl, String token, String repoName, String description, boolean isPrivate);

    void deleteRepository(String baseUrl, String token, String owner, String repoName);
}
