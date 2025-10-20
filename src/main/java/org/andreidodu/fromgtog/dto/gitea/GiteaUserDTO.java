package org.andreidodu.fromgtog.dto.gitea;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class GiteaUserDTO {

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

    private OffsetDateTime lastLogin;

    @JsonProperty("created")
    private OffsetDateTime created;

    private String username;
    private boolean restricted;
    private boolean active;

    @JsonProperty("prohibit_login")
    private boolean prohibitLogin;

}
