package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.dto.Filter;
import com.andreidodu.fromgtog.exception.CloningSourceException;
import com.andreidodu.fromgtog.service.GitlabService;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic.GenericDestinationEngineFromStrategyService;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Namespace;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;
import org.gitlab4j.api.models.Visibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitlabServiceImpl implements GitlabService {

    private final static Logger log = LoggerFactory.getLogger(GitlabServiceImpl.class);
    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "token ";
    private static final String CONTENT_TYPE_JSON = "application/json";

    private static GitlabService instance;

    public static GitlabService getInstance() {
        if (instance == null) {
            instance = new GitlabServiceImpl();
        }
        return instance;
    }

    public static GenericDestinationEngineFromStrategyService getFullInstance() {
        if (instance == null) {
            instance = new GitlabServiceImpl();
        }
        return instance;
    }

    @Override
    public User getMyself(String token, String urlString) {
        try {
            GitLabApi gitLabApi = new GitLabApi(urlString, token);
            return gitLabApi.getUserApi().getCurrentUser();
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Project> tryToRetrieveUserRepositories(Filter privacy, String url, String token) {
        try {
            GitLabApi gitLabApi = new GitLabApi(url, token);

            List<Project> projects = new ArrayList<>();

            projects.addAll(retrievePersonalProjects(privacy, gitLabApi));

            projects.forEach(repo -> {
                if ("group".equalsIgnoreCase(repo.getNamespace().getKind())) {
                    log.debug("Group-owned project ({}). Group name: {}", repo.getName(), repo.getNamespace().getName());
                } else if ("user".equalsIgnoreCase(repo.getNamespace().getKind())) {
                    log.debug("User-owned project ({}). Group name: {}", repo.getName(), repo.getNamespace().getName());
                }
            });

            return projects;
        } catch (Exception e) {
            log.error("{}", e.toString());
            throw new CloningSourceException("all repositories retrieval failed.", e);
        }
    }

    private List<Project> retrievePersonalProjects(Filter privacy, GitLabApi gitLabApi) throws GitLabApiException {
        boolean privateFlag = Optional.ofNullable(privacy)
                .map(Filter::privateFlag)
                .orElseGet(() -> true);
        boolean starredFlag = Optional.ofNullable(privacy)
                .map(Filter::starredFlag)
                .orElseGet(() -> true);
        boolean publicFlag = Optional.ofNullable(privacy)
                .map(Filter::publicFlag)
                .orElseGet(() -> true);
        boolean archivedFlag = Optional.ofNullable(privacy)
                .map(Filter::archivedFlag)
                .orElseGet(() -> true);
        boolean forkedFlag = Optional.ofNullable(privacy)
                .map(Filter::forkedFlag)
                .orElseGet(() -> true);
        boolean organizationFlag = Optional.ofNullable(privacy)
                .map(Filter::organizationFlag)
                .orElseGet(() -> true);

        Set<Long> idList = new HashSet<>();

        String[] excluded = Optional.ofNullable(privacy).map(Filter::excludedOrganizations).orElseGet(() -> new String[0]);
        if (privateFlag) {
            idList.addAll(retrieveProjectsByVisibility(gitLabApi, Visibility.PRIVATE, true, false, false, false, organizationFlag, excluded));
            idList.addAll(retrieveProjectsByVisibility(gitLabApi, Visibility.INTERNAL, true, false, false, false, organizationFlag, excluded));
        }

        if (publicFlag) {
            idList.addAll(retrieveProjectsByVisibility(gitLabApi, Visibility.PUBLIC, true, false, false, false, organizationFlag, excluded));
        }

        if (archivedFlag) {
            idList.addAll(retrieveProjectsByVisibility(gitLabApi, Visibility.PRIVATE, true, false, true, false, organizationFlag, excluded));
            idList.addAll(retrieveProjectsByVisibility(gitLabApi, Visibility.INTERNAL, true, false, true, false, organizationFlag, excluded));
            idList.addAll(retrieveProjectsByVisibility(gitLabApi, Visibility.PUBLIC, true, false, true, false, organizationFlag, excluded));
        }

        if (forkedFlag) {
            idList.addAll(retrieveForkedProjects(gitLabApi));
        }

        if (organizationFlag) {
            idList.addAll(retrieveProjectsByVisibility(gitLabApi, Visibility.PRIVATE, true, false, false, false, true, excluded));
            idList.addAll(retrieveProjectsByVisibility(gitLabApi, Visibility.INTERNAL, true, false, false, false, true, excluded));
            idList.addAll(retrieveProjectsByVisibility(gitLabApi, Visibility.PUBLIC, true, false, false, false, true, excluded));
        }

        if (starredFlag) {
            idList.addAll(retrieveProjectsByVisibility(gitLabApi, Visibility.PRIVATE, false, false, false, true, true, excluded));
            idList.addAll(retrieveProjectsByVisibility(gitLabApi, Visibility.INTERNAL, false, false, false, true, true, excluded));
            idList.addAll(retrieveProjectsByVisibility(gitLabApi, Visibility.PUBLIC, false, false, false, true, true, excluded));
        }

        return idList.stream().map(id -> {
            try {
                return gitLabApi.getProjectApi().getProject(id);
            } catch (GitLabApiException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    private static Set<Long> retrieveForkedProjects(GitLabApi gitLabApi) throws GitLabApiException {
        Set<Long> forkedProjects = gitLabApi.getProjectApi()
                .getMemberProjects()
                .stream()
                .filter(p -> p.getForkedFromProject() != null)
                .map(Project::getId)
                .collect(Collectors.toSet());
        return forkedProjects;
    }

    private static Set<Long> retrieveProjectsByVisibility(GitLabApi gitLabApi, Visibility visibility, boolean isOwned, boolean isForked, boolean isArchived, boolean isStarred, boolean isOrganization, String[] excluded) throws GitLabApiException {
        Stream<Project> stream = gitLabApi.getProjectApi()
                .getProjects(
                        isArchived,
                        visibility,
                        "ID",
                        "DESC",
                        null,
                        true,
                        isOwned,
                        isOwned,
                        isStarred,
                        false
                )
                .stream();

        if (isOrganization) {
            stream = filterByOrganization(excluded, stream);
        }

        if (!isForked) {
            stream = stream.filter(project -> project.getForkedFromProject() == null);
        }

        return stream
                .map(Project::getId)
                .collect(Collectors.toSet());
    }

    private static Stream<Project> filterByOrganization(String[] excluded, Stream<Project> stream) {
        stream = stream
                .filter(project -> Arrays.stream(excluded).noneMatch(ex -> ex.equalsIgnoreCase(project.getNamespace().getFullPath().toLowerCase())));
        return stream;
    }

    private List<Project> addStarredProjects(GitLabApi gitLabApi) throws GitLabApiException {
        return List.of();
    }

    @Override
    public Collection<Project> tryToRetrieveStarredRepositories(String url, String token) {
        try {
            GitLabApi gitLabApi = new GitLabApi(url, token);
            return addStarredProjects(gitLabApi);
        } catch (Exception e) {
            log.error("{}", e, e);
            return List.of();
        }

    }

    @Override
    public String getLogin(String token, String urlString) {
        try {
            GitLabApi gitLabApi = new GitLabApi(urlString, token);
            return gitLabApi.getUserApi().getCurrentUser().getUsername();
        } catch (GitLabApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean createRepository(String urlString, String token, String repositoryName, String isArchived, boolean isPrivate) throws IOException, InterruptedException {
        return true;
    }

    @Override
    public boolean updateRepositoryPrivacy(String token, String login, String url, String repositoryName, boolean isArchived, boolean isPrivate) throws IOException, InterruptedException {
        try {
            GitLabApi gitLabApi = new GitLabApi(url, token);
            Project project = gitLabApi.getProjectApi().getProject(login + "/" + repositoryName);
            project.setVisibility(isPrivate ? Visibility.PRIVATE : Visibility.PUBLIC);
            project.setArchived(isArchived);
            gitLabApi.getProjectApi().updateProject(project);
            return true;
        } catch (GitLabApiException e) {
            log.error("error", e);
            return false;
        }
    }

    private void deleteRepository(String baseUrl, String token, String owner, String repoName) {
        try {
            log.debug("deleting repository {}...", repoName);
            GitLabApi gitLabApi = new GitLabApi(baseUrl, token);
            String projectIdOrPath = owner + "/" + repoName;
            gitLabApi.getProjectApi().deleteProject(projectIdOrPath);
            log.debug("Project deleted: {}", projectIdOrPath);
        } catch (Exception e) {
            log.error("Failed to delete repository {}, because {}", repoName, e.getMessage());
            throw new CloningSourceException("Failed to delete repository " + repoName, e);
        }
    }

    @Override
    public void deleteAllRepositories(String baseUrl, String token) {
        tryToRetrieveUserRepositories(null, baseUrl, token)
                .forEach(repo -> deleteRepository(baseUrl, token, repo.getNamespace().getFullPath(), repo.getPath()));
    }
}
