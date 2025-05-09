package com.andreidodu.fromgtog.service.impl;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<Project> tryToRetrieveUserRepositories(String url, String token) {
        try {
            GitLabApi gitLabApi = new GitLabApi(url, token);
            String currentUsername = gitLabApi.getUserApi().getCurrentUser().getUsername();

            List<Project> projects = new ArrayList<>();

            projects.addAll(addPersonalProjects(gitLabApi));


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
        }
        return List.of();
    }

    private List<Project> addPersonalProjects(GitLabApi gitLabApi) throws GitLabApiException {
        List<Project> projectList = gitLabApi.getProjectApi().getProjects(
                false,                  // archived
                Visibility.PRIVATE,     // visibility
                "ID",     // orderBy (e.g., "id", "name", "created_at", "last_activity_at")
                "DESC",                 // sort ("asc" or "desc")
                null,                   // search (e.g., "my-repo")
                false,                  // simple (true = return only ID, name, path, etc.)
                true,                   // owned
                true,                  // membership
                false,                  // starred
                false                   // statistics
        );
        projectList.addAll(projectList);

        List<Project> personalProjectsPublic = gitLabApi.getProjectApi().getProjects(
                false,                  // archived
                Visibility.PUBLIC,     // visibility
                "ID",     // orderBy (e.g., "id", "name", "created_at", "last_activity_at")
                "DESC",                 // sort ("asc" or "desc")
                null,                   // search (e.g., "my-repo")
                false,                  // simple (true = return only ID, name, path, etc.)
                true,                   // owned
                true,                  // membership
                false,                  // starred
                false                   // statistics
        );
        projectList.addAll(personalProjectsPublic);
        personalProjectsPublic = gitLabApi.getProjectApi().getProjects(
                true,                  // archived
                Visibility.PUBLIC,     // visibility
                "ID",     // orderBy (e.g., "id", "name", "created_at", "last_activity_at")
                "DESC",                 // sort ("asc" or "desc")
                null,                   // search (e.g., "my-repo")
                false,                  // simple (true = return only ID, name, path, etc.)
                true,                   // owned
                true,                  // membership
                false,                  // starred
                false                   // statistics
        );
        projectList.addAll(personalProjectsPublic);
        projectList.addAll(personalProjectsPublic);
        personalProjectsPublic = gitLabApi.getProjectApi().getProjects(
                true,                  // archived
                Visibility.PRIVATE,     // visibility
                "ID",     // orderBy (e.g., "id", "name", "created_at", "last_activity_at")
                "DESC",                 // sort ("asc" or "desc")
                null,                   // search (e.g., "my-repo")
                false,                  // simple (true = return only ID, name, path, etc.)
                true,                   // owned
                true,                  // membership
                false,                  // starred
                false                   // statistics
        );
        projectList.addAll(personalProjectsPublic);


        List<Project> allProjects = gitLabApi.getProjectApi().getMemberProjects();

        // Filter for forked projects
        List<Project> forkedProjects = allProjects.stream()
                .filter(p -> p.getForkedFromProject() != null)
                .collect(Collectors.toList());
        projectList.addAll(forkedProjects);


        return projectList;
    }

    private List<Project> addStarredProjects(GitLabApi gitLabApi) throws GitLabApiException {
        List<Project> projectList = gitLabApi.getProjectApi().getProjects(
                false,                  // archived
                Visibility.PRIVATE,     // visibility
                "ID",     // orderBy (e.g., "id", "name", "created_at", "last_activity_at")
                "DESC",                 // sort ("asc" or "desc")
                null,                   // search (e.g., "my-repo")
                false,                  // simple (true = return only ID, name, path, etc.)
                false,                   // owned
                false,                  // membership
                true,                  // starred
                false                   // statistics
        );
        projectList.addAll(projectList);

        List<Project> personalProjectsPublic = gitLabApi.getProjectApi().getProjects(
                false,                  // archived
                Visibility.PUBLIC,     // visibility
                "ID",     // orderBy (e.g., "id", "name", "created_at", "last_activity_at")
                "DESC",                 // sort ("asc" or "desc")
                null,                   // search (e.g., "my-repo")
                false,                  // simple (true = return only ID, name, path, etc.)
                false,                   // owned
                false,                  // membership
                true,                  // starred
                false                   // statistics
        );
        projectList.addAll(personalProjectsPublic);
        return projectList;
    }

    @Override
    public Collection<Project> tryToRetrieveStarredRepositories(String url, String token) {
        try {
            GitLabApi gitLabApi = new GitLabApi(url, token);
            return addStarredProjects(gitLabApi);
        } catch (Exception e) {
            log.error("{}", e.toString(), e);
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
        try {
            GitLabApi gitLabApi = new GitLabApi(urlString, token);
            String username = gitLabApi.getUserApi().getCurrentUser().getUsername();

            Namespace userNamespace = gitLabApi.getNamespaceApi().getNamespaces().stream()
                    .filter(ns -> username.equals(ns.getPath()) && "user".equals(ns.getKind()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User namespace not found"));

            log.debug("userNameSpace: {}", userNamespace);

            Project newProject = gitLabApi.getProjectApi().createProject(repositoryName, userNamespace.getFullPath());
        } catch (GitLabApiException e) {
            log.error("error", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean updateRepositoryPrivacy(String token, String login, String url, String repositoryName, boolean isArchived, boolean isPrivates) throws IOException, InterruptedException {
//        try {
//
//
////            if (isArchived) {
////                GitLabApi gitLabApi = new GitLabApi(url, token);   Project archivedProject = gitLabApi.getProjectApi().archiveProject(createdProject.getId());
////            }
//        } catch (GitLabApiException e) {
//            log.error("error", e);
//            return false;
//        }
        return true;
    }

    @Override
    public void deleteRepository(String baseUrl, String token, String owner, String repoName) {
        try {
            GitLabApi gitLabApi = new GitLabApi(baseUrl, token);
            String projectIdOrPath = owner + "/" + repoName;
            gitLabApi.getProjectApi().deleteProject(projectIdOrPath);
            log.debug("Project deleted: {}", projectIdOrPath);
        } catch (Exception e) {
            log.error("Failed to delete repository {}, because {}", repoName, e.getMessage());
        }
    }
}
