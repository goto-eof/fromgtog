package com.andreidodu.fromgtog.dto.gitea;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GiteaOrganizationDTO {

    private long id;
    private String name;
    private String full_name;
    private String email;
    private String description;
    private String website;
    private String location;
    private String visibility;
    private String repo_admin_change_team_access;
    private String avatar_url;
    private String username;

}
