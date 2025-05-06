package com.andreidodu.fromgtog.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RepositoryDTO {

    private String login;
    private String name;
    private String description;
    private String cloneAddress;
    private boolean privateFlag;
    private boolean archivedFlag;

}
