package com.andreidodu.fromgtog.service;

import com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic.GenericDestinationEngineFromStrategyService;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;

import java.util.Collection;
import java.util.List;

public interface GitlabService extends GenericDestinationEngineFromStrategyService {

    User getMyself(String token, String urlString);

    List<Project> tryToRetrieveUserRepositories(String url, String token);

    Collection<Project> tryToRetrieveStarredRepositories(String url, String token);

    void deleteRepository(String baseUrl, String token, String owner, String repoName);
}
