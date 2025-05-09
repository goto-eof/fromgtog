package com.andreidodu.fromgtog.service.factory.from.engines;

import com.andreidodu.fromgtog.dto.CallbackContainer;
import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.FromContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.dto.gitea.GiteaRepositoryDTO;
import com.andreidodu.fromgtog.dto.gitea.GiteaUserDTO;
import com.andreidodu.fromgtog.mapper.GiteaRepositoryMapper;
import com.andreidodu.fromgtog.mapper.GitlabRepositoryMapper;
import com.andreidodu.fromgtog.service.GiteaService;
import com.andreidodu.fromgtog.service.GitlabService;
import com.andreidodu.fromgtog.service.factory.from.AbstractSourceEngine;
import com.andreidodu.fromgtog.service.factory.from.engines.common.SourceEngineCommon;
import com.andreidodu.fromgtog.service.impl.GiteaServiceImpl;
import com.andreidodu.fromgtog.service.impl.GitlabServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;
import org.gitlab4j.api.models.Owner;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;

import java.util.List;

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
        FromContext context = engineContext.fromContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        callbackContainer.updateApplicationStatusMessage().accept("Retrieving repositories information...");

        List<Project> giteaRepositoryDTOList = gitlabService.tryToRetrieveUserRepositories(context.url(), context.token());

       //  addStarredRepositoriesIfNecessary(context, giteaRepositoryDTOList, gitlabService);

        User myself = gitlabService.getMyself(context.token(), context.url());

        SourceEngineCommon sourceEngineCommon = new SourceEngineCommon();

        List<String> blackListOrganizationsList = sourceEngineCommon.buildOrganizationBlacklist(context.excludeOrganizations());

        GitlabRepositoryMapper mapper = new GitlabRepositoryMapper();

        List<RepositoryDTO> repositoryDTOList = giteaRepositoryDTOList
                .stream()
                .filter(repository -> {
                    if (!context.cloneArchivedReposFlag() && repository.getArchived()) {
                        return false;
                    }
                    if (!context.cloneForkedReposFlag() && repository.getForkedFromProject() != null) {
                        return false;
                    }
                    if (!context.clonePrivateReposFlag() && !repository.getPublic()) {
                        return false;
                    }
                    if (!context.clonePublicReposFlag() && repository.getPublic()) {
                        return false;
                    }
                    if (!context.cloneBelongingToOrganizationsReposFlag() && repository.getOwner() == null && repository.getNamespace().getKind().equals("group")) {
                        return false;
                    }
                    if (context.cloneBelongingToOrganizationsReposFlag() && isOrganizationInBlacklist(repository.getNamespace().getFullPath(), blackListOrganizationsList)) {
                        return false;
                    }
                    return true;
                })
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
