package org.andreidodu.fromgtog.service;

import org.andreidodu.fromgtog.dto.DeleteRepositoryRequestDTO;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.util.Map;

public interface GitHubService {
    GitHub retrieveGitHubClient(String token);

    GHMyself retrieveGitHubMyself(GitHub githubClient);

    Map<String, GHRepository> retrieveAllGitHubRepositories(GitHub githubClient);

    Map<String, GHRepository> retrieveAllStarredGitHubRepositories(GitHub githubClient);

    void deleteAllRepositories(String token);

    boolean deleteRepository(DeleteRepositoryRequestDTO  deleteRepositoryRequestDTO);
}
