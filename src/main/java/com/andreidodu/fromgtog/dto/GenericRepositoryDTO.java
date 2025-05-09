package com.andreidodu.fromgtog.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GenericRepositoryDTO {
    private String ownerLogin;
    private String repositoryName;
}
