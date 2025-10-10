package com.andreidodu.fromgtog.service.factory.from.engines;

import com.andreidodu.fromgtog.constants.ApplicationConstants;
import com.andreidodu.fromgtog.dto.*;
import com.andreidodu.fromgtog.mapper.GitlabRepositoryMapper;
import com.andreidodu.fromgtog.service.GitlabService;
import com.andreidodu.fromgtog.service.factory.from.AbstractSourceEngine;
import com.andreidodu.fromgtog.service.impl.GitlabServiceImpl;
import com.andreidodu.fromgtog.type.EngineOptionsType;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.util.StringUtil;
import org.gitlab4j.api.models.Project;

import java.util.List;
import java.util.Optional;

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


        if (EngineOptionsType.FILTER.equals(fromContext.engineOptionsType())) {
            List<RepositoryDTO> repositoryDTOList = retrieveFilteredRepositoryList(gitlabService, fromContext, callbackContainer);
            callbackContainer.updateApplicationStatusMessage().accept("Repositories retrieved and filtered by filters with success");
            return repositoryDTOList;
        }

        if (EngineOptionsType.FILE.equals(fromContext.engineOptionsType())) {
            List<RepositoryDTO> repostoryDTOList = retrieveRepositoryListFromCustomList(gitlabService, fromContext, callbackContainer);
            callbackContainer.updateApplicationStatusMessage().accept("Repositories retrieved and filtered by file with success");
            return repostoryDTOList;
        }

        throw new RuntimeException("Engine options type not valid. Please contact the developer for a fix (:");
    }

    private List<RepositoryDTO> retrieveRepositoryListFromCustomList(GitlabService gitlabService, FromContext fromContext, CallbackContainer callbackContainer) {
        String filename = fromContext.includeRepoNameFileNameList();
        List<String> fileContentList = fileContentToList(filename);
        List<Project> giteaRepositoryDTOList = gitlabService.tryToRetrieveUserRepositories(
                        buildRetrieveAllFilter(fromContext),
                        fromContext.url(),
                        fromContext.token()
                )
                .stream()
                .filter(project -> fileContentList.contains(project.getName().trim().toLowerCase()))
                .toList();
        return mapToResultDTO(callbackContainer, giteaRepositoryDTOList);
    }

    private List<RepositoryDTO> retrieveFilteredRepositoryList(GitlabService gitlabService, FromContext fromContext, CallbackContainer callbackContainer) {
        List<Project> giteaRepositoryDTOList = gitlabService.tryToRetrieveUserRepositories(
                buildUserRepoFilterInput(fromContext),
                fromContext.url(),
                fromContext.token()
        );

        return mapToResultDTO(callbackContainer, giteaRepositoryDTOList);
    }

    private static List<RepositoryDTO> mapToResultDTO(CallbackContainer callbackContainer, List<Project> giteaRepositoryDTOList) {
        GitlabRepositoryMapper mapper = new GitlabRepositoryMapper();

        List<RepositoryDTO> repositoryDTOList = giteaRepositoryDTOList
                .stream()
                .map(mapper::toDTO)
                .toList();

        callbackContainer.updateApplicationStatusMessage().accept("All repositories information were retrieved.");

        return repositoryDTOList;
    }

    private Filter buildUserRepoFilterInput(FromContext fromContext) {
        return Filter.builder()
                .starredFlag(fromContext.cloneStarredReposFlag())
                .privateFlag(fromContext.clonePrivateReposFlag())
                .archivedFlag(fromContext.cloneArchivedReposFlag())
                .forkedFlag(fromContext.cloneForkedReposFlag())
                .publicFlag(fromContext.clonePublicReposFlag())
                .organizationFlag(fromContext.cloneBelongingToOrganizationsReposFlag())
                .excludedOrganizations(getExcludedOrganizations(fromContext))
                .excludeRepoNameList(getExcludedRepoNameList(fromContext))
                .build();
    }

    private Filter buildRetrieveAllFilter(FromContext fromContext) {
        return Filter.builder()
                .starredFlag(true)
                .privateFlag(true)
                .archivedFlag(true)
                .forkedFlag(true)
                .publicFlag(true)
                .organizationFlag(true)
                .excludedOrganizations(new String[]{})
                .excludeRepoNameList(new String[]{})
                .build();
    }

    private String[] getExcludedRepoNameList(FromContext fromContext) {
        return StringUtil.stringSeparatedByCommaToArray(Optional.ofNullable(fromContext.excludeRepoNameList())
                .orElseGet(() -> ""), ApplicationConstants.LIST_ITEM_SEPARATOR);
    }

    private String[] getExcludedOrganizations(FromContext fromContext) {
        return StringUtil.stringSeparatedByCommaToArray(Optional.ofNullable(fromContext.excludeOrganizations())
                .orElseGet(() -> ""), ApplicationConstants.LIST_ITEM_SEPARATOR);
    }

}
