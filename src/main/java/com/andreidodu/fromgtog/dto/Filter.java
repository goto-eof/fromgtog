package com.andreidodu.fromgtog.dto;


import lombok.Builder;

@Builder
public record Filter(boolean privateFlag,
                     boolean starredFlag,
                     boolean publicFlag,
                     boolean archivedFlag,
                     boolean forkedFlag,
                     boolean organizationFlag,
                     String[] excludedOrganizations,
                     String[] excludeRepoNameList) {
}
