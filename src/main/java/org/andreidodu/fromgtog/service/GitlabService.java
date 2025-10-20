package org.andreidodu.fromgtog.service;

import org.andreidodu.fromgtog.dto.Filter;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;

import java.util.Collection;
import java.util.List;

public interface GitlabService extends DeletableDestinationContentService {

    User getMyself(String token, String urlString);

    List<Project> tryToRetrieveUserRepositories(Filter privacy, String url, String token);

    Collection<Project> tryToRetrieveStarredRepositories(String url, String token);

}
