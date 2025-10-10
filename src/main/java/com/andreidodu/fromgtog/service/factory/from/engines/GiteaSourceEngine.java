package com.andreidodu.fromgtog.service.factory.from.engines;

import com.andreidodu.fromgtog.constants.ApplicationConstants;
import com.andreidodu.fromgtog.dto.CallbackContainer;
import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.FromContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.dto.gitea.GiteaRepositoryDTO;
import com.andreidodu.fromgtog.dto.gitea.GiteaUserDTO;
import com.andreidodu.fromgtog.mapper.GiteaRepositoryMapper;
import com.andreidodu.fromgtog.service.GiteaService;
import com.andreidodu.fromgtog.service.factory.from.AbstractSourceEngine;
import com.andreidodu.fromgtog.service.factory.from.engines.common.SourceEngineCommon;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.local.LocalDestinationEngineFromLocalStrategy;
import com.andreidodu.fromgtog.service.impl.GiteaServiceImpl;
import com.andreidodu.fromgtog.type.EngineOptionsType;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GiteaSourceEngine extends AbstractSourceEngine {
    private static final Logger log = LoggerFactory.getLogger(LocalDestinationEngineFromLocalStrategy.class);

    private final static EngineType SOURCE_ENGINE_TYPE = EngineType.GITEA;

    @Override
    public EngineType getEngineType() {
        return SOURCE_ENGINE_TYPE;
    }

    @Override
    public List<RepositoryDTO> retrieveRepositoryList(EngineContext engineContext) {
        GiteaService giteaService = new GiteaServiceImpl();
        FromContext context = engineContext.fromContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        List<GiteaRepositoryDTO> giteaRepositoryDTOList = giteaService.tryToRetrieveUserRepositories(context.url(), context.token());

        addStarredRepositoriesIfNecessary(context, giteaRepositoryDTOList, giteaService);

        GiteaUserDTO myself = giteaService.getMyself(context.token(), context.url());

        callbackContainer.updateApplicationStatusMessage().accept("Retrieving repositories information...");

        if (EngineOptionsType.FILTER.equals(context.engineOptionsType())) {
            List<RepositoryDTO> repositoryDTOList = retrieveFilteredRepositoryList(context, giteaRepositoryDTOList, myself);
            callbackContainer.updateApplicationStatusMessage().accept("Repositories retrieved and filtered by filters with success");
            return repositoryDTOList;
        }

        if (EngineOptionsType.FILE.equals(context.engineOptionsType())) {
            List<RepositoryDTO> repostoryDTOList = retrieveRepositoryListFromCustomList(context, giteaRepositoryDTOList);
            callbackContainer.updateApplicationStatusMessage().accept("Repositories retrieved and filtered by file with success");
            return repostoryDTOList;
        }

        throw new RuntimeException("Engine options type not valid. Please contact the developer for a fix (:");
    }

    protected List<RepositoryDTO> retrieveRepositoryListFromCustomList(FromContext context, List<GiteaRepositoryDTO> giteaRepositoryDTOList) {
        String filename = context.includeRepoNameFileNameList();
        List<String> fileContentList = fileContentToList(filename);
        GiteaRepositoryMapper mapper = new GiteaRepositoryMapper();
        return giteaRepositoryDTOList.stream()
                .filter(repo -> fileContentList.contains(repo.getName().toLowerCase()))
                .map(mapper::toDTO)
                .toList();
    }

    private List<RepositoryDTO> retrieveFilteredRepositoryList(FromContext context, List<GiteaRepositoryDTO> giteaRepositoryDTOList, GiteaUserDTO myself) {
        SourceEngineCommon sourceEngineCommon = new SourceEngineCommon();

        List<String> blackListOrganizationsList = sourceEngineCommon.buildOrganizationBlacklist(context.excludeOrganizations());

        List<String> excludeRepoNameList = StringUtil.stringsSeparatedByCommaToList(context.excludeRepoNameList(), ApplicationConstants.LIST_ITEM_SEPARATOR);

        GiteaRepositoryMapper mapper = new GiteaRepositoryMapper();

        List<RepositoryDTO> repositoryDTOList = giteaRepositoryDTOList
                .stream()
                .filter(repository -> {
                    if (excludeRepoNameList.contains(repository.getName().toLowerCase())) {
                        return false;
                    }

                    if (!context.cloneArchivedReposFlag() && repository.isArchived()) {
                        return false;
                    }
                    if (!context.cloneForkedReposFlag() && repository.isFork()) {
                        return false;
                    }
                    if (!context.clonePrivateReposFlag() && repository.isPrivate()) {
                        return false;
                    }
                    if (!context.clonePublicReposFlag() && !repository.isPrivate()) {
                        return false;
                    }
                    if (!context.cloneBelongingToOrganizationsReposFlag() && !repository.getOwner().getLogin().equals(myself.getLogin())) {
                        return false;
                    }
                    if (context.cloneBelongingToOrganizationsReposFlag() && isOrganizationInBlacklist(repository.getOwner(), blackListOrganizationsList)) {
                        return false;
                    }
                    return true;
                })
                .map(mapper::toDTO)
                .toList();

        return repositoryDTOList;
    }

    private void addStarredRepositoriesIfNecessary(FromContext context, List<GiteaRepositoryDTO> giteaRepositoryDTOList, GiteaService giteaService) {
        if (context.cloneStarredReposFlag()) {
            giteaRepositoryDTOList.addAll(giteaService.tryToRetrieveStarredRepositories(context.url(), context.token()));
        }
    }

    private boolean isOrganizationInBlacklist(GiteaUserDTO repositoryUser, List<String> blackListOrganizationsList) {
        return blackListOrganizationsList.contains(repositoryUser.getLogin().toLowerCase());
    }

}
