package org.andreidodu.fromgtog.dto.gitea;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GiteaRepositoryDTO {
    private Long id;
    private GiteaUserDTO owner;

    @JsonProperty("full_name")
    private String fullName;

    private boolean empty;
    private String template;
    private GiteaRepositoryDTO parent;

    private String name;
    private String description;

    @JsonProperty("private")
    private boolean isPrivate;

    private boolean fork;

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("ssh_url")
    private String sshUrl;

    @JsonProperty("clone_url")
    private String cloneUrl;

    @JsonProperty("original_url")
    private String originalUrl;

    private String website;

    @JsonProperty("stars_count")
    private int starsCount;

    @JsonProperty("forks_count")
    private int forksCount;

    @JsonProperty("watchers_count")
    private int watchersCount;

    @JsonProperty("open_issues_count")
    private int openIssuesCount;

    @JsonProperty("default_branch")
    private String defaultBranch;

    private boolean archived;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    private Map<String, Boolean> permissions;

    @JsonProperty("internal_tracker")
    private InternalTracker internalTracker;

    @JsonProperty("mirror")
    private boolean mirror;

    private long size;
    @JsonProperty("languages_url")
    private String languagesUrl;

    private String url;

    @JsonProperty("release")
    private Release release;

    private String language;

    @JsonProperty("has_issues")
    private boolean hasIssues;

    @JsonProperty("has_wiki")
    private boolean hasWiki;

    @JsonProperty("has_pull_requests")
    private boolean hasPullRequests;

    @JsonProperty("has_projects")
    private boolean hasProjects;

    @JsonProperty("has_releases")
    private boolean hasReleases;

    @JsonProperty("has_actions")
    private boolean hasActions;

    @JsonProperty("has_packages")
    private boolean hasPackages;

    @JsonProperty("has_pages")
    private boolean hasPages;

    @JsonProperty("has_discussions")
    private boolean hasDiscussions;

    @Data
    public static class User {
        private int id;
        private String login;
        private String login_name;
        @JsonProperty("source_id")
        private String sourceId;
        @JsonProperty("html_url")
        private String htmlUrl;
        @JsonProperty("full_name")
        private String fullName;
        private String email;
        @JsonProperty("avatar_url")
        private String avatarUrl;
        private String language;
        private String location;
        private String website;
        private String visibility;
        private long followers_count;
        private long following_count;
        private long starred_repos_count;
        private String description;
        @JsonProperty("is_admin")
        private boolean isAdmin;
        @JsonProperty("last_login")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
        private LocalDateTime lastLogin;
        private String created;
        private String username;
        private boolean restricted;
        private boolean active;
        @JsonProperty("prohibit_login")
        private boolean prohibitLogin;

    }

    @Data
    @NoArgsConstructor
    public static class InternalTracker {
        @JsonProperty("enable_time_tracker")
        private boolean enableTimeTracker;

        @JsonProperty("allow_only_contributors_to_track_time")
        private boolean allowOnlyContributorsToTrackTime;

        @JsonProperty("enable_issue_dependencies")
        private boolean enableIssueDependencies;
    }

    @Data
    @NoArgsConstructor
    public static class Mirror {
        @JsonProperty("interval")
        private String interval;

        @JsonProperty("last_updated")
        private OffsetDateTime lastUpdated;

    }

    @Data
    @NoArgsConstructor
    public static class Release {
        @JsonProperty("tag_name")
        private String tagName;

        @JsonProperty("target_commitish")
        private String targetCommitish;

        @JsonProperty("name")
        private String name;

        @JsonProperty("body")
        private String body;

        @JsonProperty("draft")
        private boolean draft;

        @JsonProperty("prerelease")
        private boolean prerelease;

        @JsonProperty("created_at")
        private OffsetDateTime createdAt;

        @JsonProperty("published_at")
        private OffsetDateTime publishedAt;
    }

}
