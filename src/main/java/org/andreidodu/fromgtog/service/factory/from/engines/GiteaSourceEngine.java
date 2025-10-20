package org.andreidodu.fromgtog.service.factory.from.engines;

import org.andreidodu.fromgtog.constants.ApplicationConstants;
import org.andreidodu.fromgtog.dto.CallbackContainer;
import org.andreidodu.fromgtog.dto.EngineContext;
import org.andreidodu.fromgtog.dto.FromContext;
import org.andreidodu.fromgtog.dto.RepositoryDTO;
import org.andreidodu.fromgtog.dto.gitea.GiteaRepositoryDTO;
import org.andreidodu.fromgtog.dto.gitea.GiteaUserDTO;
import org.andreidodu.fromgtog.mapper.GiteaRepositoryMapper;
import org.andreidodu.fromgtog.service.GiteaService;
import org.andreidodu.fromgtog.service.factory.from.AbstractSourceEngine;
import org.andreidodu.fromgtog.service.factory.from.engines.common.SourceEngineCommon;
import org.andreidodu.fromgtog.service.impl.GiteaServiceImpl;
import org.andreidodu.fromgtog.type.EngineOptionsType;
import org.andreidodu.fromgtog.type.EngineType;
import org.andreidodu.fromgtog.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GiteaSourceEngine extends AbstractSourceEngine {
    private static final Logger log = LoggerFactory.getLogger(GiteaSourceEngine.class);

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

        callbackContainer.updateLogAndApplicationStatusMessage().accept("Retrieving repositories information...");

        if (EngineOptionsType.FILTER.equals(context.engineOptionsType())) {
            List<RepositoryDTO> repositoryDTOList = retrieveFilteredRepositoryList(context, giteaRepositoryDTOList, myself);
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Repositories retrieved and filtered by filters with success");
            return repositoryDTOList;
        }

        if (EngineOptionsType.FILE.equals(context.engineOptionsType())) {
            List<RepositoryDTO> repostoryDTOList = retrieveRepositoryListFromCustomList(context, giteaRepositoryDTOList);
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Repositories retrieved and filtered by file with success");
            return repostoryDTOList;
        }

        throw new RuntimeException("Engine options type not valid. Please contact the developer for a fix (:");
    }

    protected List<RepositoryDTO> retrieveRepositoryListFromCustomList(FromContext context, List<GiteaRepositoryDTO> sourceRepositoryDTOList) {
        String filename = context.includeRepoNameFileNameList();
        List<String> fileContentList = fileContentToList(filename);
        GiteaRepositoryMapper mapper = new GiteaRepositoryMapper();
        return sourceRepositoryDTOList.stream()
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
                        log.info("excluding '{}' because present in the 'to exclude' list", repository.getName());
                        return false;
                    }

                    if (!context.cloneArchivedReposFlag() && repository.isArchived()) {
                        log.info("excluding '{}' because repo should not be archived", repository.getName());
                        return false;
                    }
                    if (!context.cloneForkedReposFlag() && repository.isFork()) {
                        log.info("excluding '{}' because repo should not be forked", repository.getName());
                        return false;
                    }
                    if (!context.clonePrivateReposFlag() && repository.isPrivate()) {
                        log.info("excluding '{}' because repo is private", repository.getName());
                        return false;
                    }
                    if (!context.clonePublicReposFlag() && !repository.isPrivate()) {
                        log.info("excluding '{}' because repo is not private", repository.getName());
                        return false;
                    }
                    if (!context.cloneBelongingToOrganizationsReposFlag() && !repository.getOwner().getLogin().equals(myself.getLogin())) {
                        log.info("excluding '{}' because repo belong to an organization", repository.getName());
                        return false;
                    }
                    if (context.cloneBelongingToOrganizationsReposFlag() && isOrganizationInBlacklist(repository.getOwner(), blackListOrganizationsList)) {
                        log.info("excluding '{}' because repo belong to an organization and the organization is in the black list", repository.getName());
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
