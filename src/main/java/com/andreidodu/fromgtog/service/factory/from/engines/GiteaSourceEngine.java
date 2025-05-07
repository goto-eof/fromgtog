package com.andreidodu.fromgtog.service.factory.from.engines;

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
import com.andreidodu.fromgtog.service.impl.GiteaServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public class GiteaSourceEngine extends AbstractSourceEngine {

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

        callbackContainer.updateApplicationStatusMessage().accept("Retrieving repositories information...");

        List<GiteaRepositoryDTO> giteaRepositoryDTOList = giteaService.tryToRetrieveUserRepositories(context.url(), context.token());

        addStarredRepositoriesIfNecessary(context, giteaRepositoryDTOList, giteaService);

        GiteaUserDTO myself = giteaService.getMyself(context.token(), context.url());

        SourceEngineCommon sourceEngineCommon = new SourceEngineCommon();

        List<String> blackListOrganizationsList = sourceEngineCommon.buildOrganizationBlacklist(context.excludeOrganizations());

        GiteaRepositoryMapper mapper = new GiteaRepositoryMapper();

        List<RepositoryDTO> repositoryDTOList = giteaRepositoryDTOList
                .stream()
                .filter(repository -> {
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

        callbackContainer.updateApplicationStatusMessage().accept("All repositories information were retrieved.");

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
