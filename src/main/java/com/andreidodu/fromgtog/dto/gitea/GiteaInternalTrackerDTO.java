package com.andreidodu.fromgtog.dto.gitea;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GiteaInternalTrackerDTO {
    @JsonProperty("enable_time_tracker")
    private boolean enableTimeTracker;

    @JsonProperty("allow_only_contributors_to_track_time")
    private boolean allowOnlyContributorsToTrackTime;

    @JsonProperty("enable_issue_dependencies")
    private boolean enableIssueDependencies;

}
