package com.andreidodu.fromgtog.service;

import com.andreidodu.fromgtog.dto.gitea.GiteaRepositoryDTO;
import com.andreidodu.fromgtog.dto.gitea.GiteaUserDTO;

import java.util.List;

public interface GiteaService {
    GiteaUserDTO getMyself(String token, String urlString);

    List<GiteaRepositoryDTO> tryToRetrieveUserRepositories(String baseUrl, String token);
}
