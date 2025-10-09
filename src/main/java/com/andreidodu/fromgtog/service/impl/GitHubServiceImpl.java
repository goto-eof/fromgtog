package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.exception.CloningDestinationException;
import com.andreidodu.fromgtog.exception.CloningSourceException;
import com.andreidodu.fromgtog.service.GitHubService;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GitHubServiceImpl implements GitHubService {

    private static GitHubService instance;
    private Logger log = LoggerFactory.getLogger(GitHubServiceImpl.class);

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

}
