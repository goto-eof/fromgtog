package org.andreidodu.fromgtog.service.impl;

import org.andreidodu.fromgtog.dto.DeleteRepositoryRequestDTO;
import org.andreidodu.fromgtog.exception.CloningDestinationException;
import org.andreidodu.fromgtog.exception.CloningSourceException;
import org.andreidodu.fromgtog.service.GitHubService;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.andreidodu.fromgtog.util.ValidatorUtil.validateIsNotNull;
import static org.andreidodu.fromgtog.util.ValidatorUtil.validateIsNotNullAndNotBlank;

public class GitHubServiceImpl implements GitHubService {

    private static GitHubService instance;
    private final Logger log = LoggerFactory.getLogger(GitHubServiceImpl.class);

    public static synchronized GitHubService getInstance() {
        if (instance == null) {
            instance = new GitHubServiceImpl();
        }
        return instance;
    }

    @Override
    public GitHub retrieveGitHubClient(String token) {
        try {
            return GitHub.connectUsingOAuth(token);
        } catch (Exception e) {
            log.error("Error while retrieving GitHub client", e);
            throw new CloningSourceException("Unable to connect to GitHub. Invalid token?", e);
        }
    }

    @Override
    public GHMyself retrieveGitHubMyself(GitHub githubClient) {
        try {
            return githubClient.getMyself();
        } catch (Exception e) {
            log.error("Unable to retrieve GitHub Myself", e);
            throw new CloningSourceException("Unable to retrieve GitHub profile.", e);
        }
    }

    @Override
    public Map<String, GHRepository> retrieveAllGitHubRepositories(GitHub githubClient) {
        try {
            return githubClient.getMyself().getAllRepositories();
        } catch (Exception e) {
            log.error("Unable to retrieve all GitHub repositories.", e);
            throw new CloningSourceException("Unable to retrieve GitHub repositories.", e);
        }
    }

    @Override
    public Map<String, GHRepository> retrieveAllStarredGitHubRepositories(GitHub githubClient) {
        Map<String, GHRepository> result = new HashMap<>();
        try {
            githubClient.getMyself().listStarredRepositories()
                    .forEach(repo -> {
                        String starredOwnerName = repo.getOwnerName();
                        String starredRepoName = repo.getName();
                        String name = starredOwnerName + "/" + starredRepoName;
                        result.put(name, repo);
                    });
            return result;
        } catch (Exception e) {
            log.error("Unable to retrieve starred repositories.", e);
            throw new CloningSourceException("Unable to retrieve GitHub starred repositories.", e);
        }
    }


    @Override
    public void deleteAllRepositories(String token) {
        retrieveGitHubMyself(retrieveGitHubClient(token))
                .getAllRepositories()
                .forEach((s, ghRepository) -> {
                    try {
                        ghRepository.delete();
                    } catch (IOException ee) {
                        log.error("Error while trying to delete repository: {}", ee.toString());
                        throw new CloningDestinationException("Error while trying to delete repository", ee);
                    }
                });
    }

    @Override
    public boolean deleteRepository(DeleteRepositoryRequestDTO deleteRepositoryRequestDTO) {
        validateIsNotNull(deleteRepositoryRequestDTO);
        validateIsNotNullAndNotBlank(deleteRepositoryRequestDTO.baseUrl());
        validateIsNotNullAndNotBlank(deleteRepositoryRequestDTO.owner());
        validateIsNotNullAndNotBlank(deleteRepositoryRequestDTO.repoName());
        validateIsNotNullAndNotBlank(deleteRepositoryRequestDTO.token());

        try {
            retrieveGitHubMyself(retrieveGitHubClient(deleteRepositoryRequestDTO.token().get()))
                    .getRepository(deleteRepositoryRequestDTO.repoName().get())
                    .delete();
        } catch (IOException e) {
            log.error("Error while trying to delete repository: {}", e.toString());
            throw new CloningDestinationException("Error while trying to delete repository", e);
        }
        return true;
    }

}
