package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.dto.gitea.GiteaUserDTO;

public interface GiteaService {
    GiteaUserDTO getMyself(String token, String urlString);
}
