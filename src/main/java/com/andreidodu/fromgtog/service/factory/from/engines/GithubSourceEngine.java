package com.andreidodu.fromgtog.service.factory.from.engines;

import com.andreidodu.fromgtog.constants.ApplicationConstants;
import com.andreidodu.fromgtog.dto.CallbackContainer;
import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.FromContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.exception.CloningSourceException;
import com.andreidodu.fromgtog.mapper.GithubRepositoryMapper;
import com.andreidodu.fromgtog.service.GitHubService;
import com.andreidodu.fromgtog.service.factory.from.AbstractSourceEngine;
import com.andreidodu.fromgtog.service.factory.from.engines.common.SourceEngineCommon;
import com.andreidodu.fromgtog.service.impl.GitHubServiceImpl;
import com.andreidodu.fromgtog.type.EngineOptionsType;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.util.StringUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GithubSourceEngine extends AbstractSourceEngine {
    private static final Logger log = LoggerFactory.getLogger(GithubSourceEngine.class);

    private final static EngineType SOURCE_ENGINE_TYPE = EngineType.GITHUB;
    private final GitHubService gitHubService = GitHubServiceImpl.getInstance();

    private static Pair<GHRepository, GHUser> buildPair(GHRepository ghRepository) {
        try {
            return Pair.of(ghRepository, ghRepository.getOwner());
        } catch (IOException e) {
            throw new CloningSourceException("Unable to retrieve GitHub repository owner(1).", e);
        }
    }

    @Override
    public EngineType getEngineType() {
        return SOURCE_ENGINE_TYPE;
    }

    @Override
    public List<RepositoryDTO> retrieveRepositoryList(EngineContext engineContext) {
        FromContext context = engineContext.fromContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        callbackContainer.updateLogAndApplicationStatusMessage().accept("Retrieving repositories information...");

        GitHub githubClient = gitHubService.retrieveGitHubClient(context.token());
        GHMyself myself = gitHubService.retrieveGitHubMyself(githubClient);

        Map<String, GHRepository> allUserRepositories = new HashMap<>(gitHubService.retrieveAllGitHubRepositories(githubClient));

        addStarredRepositoriesIfNecessary(context, githubClient, allUserRepositories);

        if (EngineOptionsType.FILTER.equals(context.engineOptionsType())) {
            List<RepositoryDTO> repositoryDTOList = retrieveFilteredRepositoryList(context, allUserRepositories, myself, callbackContainer);
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Repositories retrieved and filtered by filters with success");
            return repositoryDTOList;
        }

        if (EngineOptionsType.FILE.equals(context.engineOptionsType())) {
            List<RepositoryDTO> repostoryDTOList = retrieveRepositoryListFromCustomList(context, allUserRepositories);
            callbackContainer.updateLogAndApplicationStatusMessage().accept("Repositories retrieved and filtered by file with success");
            return repostoryDTOList;
        }

        throw new RuntimeException("Engine options type not valid. Please contact the developer for a fix (:");
    }

    protected List<RepositoryDTO> retrieveRepositoryListFromCustomList(FromContext context, Map<String, GHRepository> allUserRepositories) {
        String filename = context.includeRepoNameFileNameList();
        List<String> fileContentList = fileContentToList(filename);
        GithubRepositoryMapper mapper = new GithubRepositoryMapper();
        return allUserRepositories.values().stream()
                .map(GithubSourceEngine::buildPair)
                .filter(repo -> fileContentList.contains(repo.getLeft().getName().toLowerCase()))
                .map(sourceDto -> mapper.toDTO(sourceDto.getLeft(), sourceDto.getRight()))
                .toList();
    }

    private List<RepositoryDTO> retrieveFilteredRepositoryList(FromContext context, Map<String, GHRepository> allUserRepositories, GHMyself myself, CallbackContainer callbackContainer) {
        SourceEngineCommon sourceEngineCommon = new SourceEngineCommon();
        List<String> blacklistOrganizationsList = sourceEngineCommon.buildOrganizationBlacklist(context.excludeOrganizations());

        List<String> excludeRepoNameList = StringUtil.stringsSeparatedByCommaToList(context.excludeRepoNameList(), ApplicationConstants.LIST_ITEM_SEPARATOR);


        GithubRepositoryMapper mapper = new GithubRepositoryMapper();

        List<RepositoryDTO> repositoryDTOList = allUserRepositories
                .values()
                .stream()
                .map(GithubSourceEngine::buildPair)
                .filter(pair -> {
                    GHRepository ghRepository = pair.getLeft();
                    GHUser repositoryUser = pair.getRight();

                    if (excludeRepoNameList.contains(ghRepository.getName().toLowerCase())) {
                        log.info("excluding '{}' because present in the 'to exclude' list", ghRepository.getName());
                        return false;
                    }

                    if (!context.cloneArchivedReposFlag() && ghRepository.isArchived()) {
                        log.info("excluding '{}' because repo should not be archived", ghRepository.getName());
                        return false;
                    }
                    if (!context.cloneForkedReposFlag() && ghRepository.isFork()) {
                        log.info("excluding '{}' because repo should not be forked", ghRepository.getName());
                        return false;
                    }
                    if (!context.clonePrivateReposFlag() && ghRepository.isPrivate()) {
                        log.info("excluding '{}' because repo is private", ghRepository.getName());
                        return false;
                    }
                    if (!context.clonePublicReposFlag() && !ghRepository.isPrivate()) {
                        log.info("excluding '{}' because repo is not private", ghRepository.getName());
                        return false;
                    }
                    if (!context.cloneBelongingToOrganizationsReposFlag() && !repositoryUser.getLogin().equals(myself.getLogin())) {
                        log.info("excluding '{}' because repo belong to an organization", ghRepository.getName());
                        return false;
                    }
                    if (context.cloneBelongingToOrganizationsReposFlag() && isOrganizationInBlacklist(repositoryUser, blacklistOrganizationsList)) {
                        log.info("excluding '{}' because repo belong to an organization and the organization is in the black list", ghRepository.getName());
                        return false;
                    }
                    return true;
                })
                // not necessary -> .filter(ghRepository -> !context.cloneArchivedReposFlag() && ghRepository.isArchived())
                .map(ghRepositoryGHUserPair -> mapper.toDTO(ghRepositoryGHUserPair.getLeft(), ghRepositoryGHUserPair.getRight()))
                .toList();

        callbackContainer.updateLogAndApplicationStatusMessage().accept("All repositories information were retrieved.");

        return repositoryDTOList;
    }

    private boolean isOrganizationInBlacklist(GHUser repositoryUser, List<String> blackListOrganizationsList) {
        return blackListOrganizationsList.contains(repositoryUser.getLogin().toLowerCase());
    }

    private void addStarredRepositoriesIfNecessary(FromContext context, GitHub githubClient, Map<String, GHRepository> allUserRepositories) {
        if (context.cloneStarredReposFlag()) {
            Map<String, GHRepository> userStarredRepositoriesMap = gitHubService.retrieveAllStarredGitHubRepositories(githubClient);
            allUserRepositories.putAll(userStarredRepositoriesMap);
        }
    }


}
