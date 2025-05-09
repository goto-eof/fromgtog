package com.andreidodu.fromgtog.service.factory.from.engines;

import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.mapper.GitlabRepositoryMapper;
import com.andreidodu.fromgtog.service.GitlabService;
import com.andreidodu.fromgtog.service.factory.from.AbstractSourceEngine;
import com.andreidodu.fromgtog.service.factory.from.engines.common.SourceEngineCommon;
import com.andreidodu.fromgtog.service.impl.GitlabServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;
import org.gitlab4j.api.models.Visibility;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO adapt for GITLAB
public class GitlabSourceEngine extends AbstractSourceEngine {

    private final static EngineType SOURCE_ENGINE_TYPE = EngineType.GITLAB;

    @Override
    public EngineType getEngineType() {
        return SOURCE_ENGINE_TYPE;
    }

    @Override
    public List<RepositoryDTO> retrieveRepositoryList(EngineContext engineContext) {
        GitlabService gitlabService = new GitlabServiceImpl();
        FromContext fromContext = engineContext.fromContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        callbackContainer.updateApplicationStatusMessage().accept("Retrieving repositories information...");

        List<Project> giteaRepositoryDTOList = gitlabService.tryToRetrieveUserRepositories(
                Filter.builder()
                        .starredFlag(fromContext.cloneStarredReposFlag())
                        .privateFlag(fromContext.clonePrivateReposFlag())
                        .archivedFlag(fromContext.cloneArchivedReposFlag())
                        .forkedFlag(fromContext.cloneForkedReposFlag())
                        .publicFlag(fromContext.clonePublicReposFlag())
                        .organizationFlag(fromContext.cloneBelongingToOrganizationsReposFlag())
                        .excludedOrganizations(
                                (String[]) Arrays
                                        .stream(Optional.ofNullable(fromContext.excludeOrganizations()).orElseGet(() -> "").split(","))
                                        .map(String::trim)
                                        .<String>toArray(String[]::new))
                        .build(),
                fromContext.url(),
                fromContext.token()
        );

        // addStarredRepositoriesIfNecessary(fromContext, giteaRepositoryDTOList, gitlabService);

        User myself = gitlabService.getMyself(fromContext.token(), fromContext.url());

        SourceEngineCommon sourceEngineCommon = new SourceEngineCommon();

        List<String> blackListOrganizationsList = sourceEngineCommon.buildOrganizationBlacklist(fromContext.excludeOrganizations());

        GitlabRepositoryMapper mapper = new GitlabRepositoryMapper();

        List<RepositoryDTO> repositoryDTOList = giteaRepositoryDTOList
                .stream()
                .map(mapper::toDTO)
                .toList();

        callbackContainer.updateApplicationStatusMessage().accept("All repositories information were retrieved.");

        return repositoryDTOList;
    }

    private void addStarredRepositoriesIfNecessary(FromContext context, List<Project> projects, GitlabService gitlabService) {
        if (context.cloneStarredReposFlag()) {
            projects.addAll(gitlabService.tryToRetrieveStarredRepositories(context.url(), context.token()));
        }
    }

    private boolean isOrganizationInBlacklist(String ownerLogin, List<String> blackListOrganizationsList) {
        return blackListOrganizationsList.contains(ownerLogin.toLowerCase());
    }

}
