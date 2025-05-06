package com.andreidodu.fromgtog.service.factory.source.realengine;

import com.andreidodu.fromgtog.dto.CallbackContainer;
import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.FromContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.exception.CloningSourceException;
import com.andreidodu.fromgtog.mapper.GithubRepositoryMapper;
import com.andreidodu.fromgtog.service.GitHubService;
import com.andreidodu.fromgtog.service.impl.GitHubServiceImpl;
import com.andreidodu.fromgtog.service.factory.source.AbstractSourceEngine;
import com.andreidodu.fromgtog.type.EngineType;
import org.apache.commons.lang3.tuple.Pair;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class GithubSourceEngine extends AbstractSourceEngine {

    private final static EngineType SOURCE_ENGINE_TYPE = EngineType.GITHUB;
    private final GitHubService gitHubService = GitHubServiceImpl.getInstance();

    @Override
    public EngineType getEngineType() {
        return SOURCE_ENGINE_TYPE;
    }

    @Override
    public List<RepositoryDTO> retrieveRepositoryList(EngineContext engineContext) {
        FromContext context = engineContext.fromContext();
        CallbackContainer callbackContainer = engineContext.callbackContainer();

        callbackContainer.updateApplicationStatusMessage().accept("Retrieving repositories information...");

        GitHub githubClient = gitHubService.retrieveGitHubClient(context.token());
        GHMyself myself = gitHubService.retrieveGitHubMyself(githubClient);

        Map<String, GHRepository> allUserRepositories = new HashMap<>(gitHubService.retrieveAllGitHubRepositories(githubClient));

        addStarredRepositoriesIfNecessary(context, githubClient, allUserRepositories);

        SourceEngineCommon sourceEngineCommon = new SourceEngineCommon();
        List<String> blacklistOrganizationsList = sourceEngineCommon.buildOrganizationBlacklist(context.excludeOrganizations());

        GithubRepositoryMapper mapper = new GithubRepositoryMapper();

        List<RepositoryDTO> repositoryDTOList = allUserRepositories
                .values()
                .stream()
                .map(GithubSourceEngine::buildPair)
                .filter(pair -> {
                    GHRepository ghRepository = pair.getLeft();
                    GHUser repositoryUser = pair.getRight();
                    if (!context.cloneArchivedReposFlag() && ghRepository.isArchived()) {
                        return false;
                    }
                    if (!context.cloneForkedReposFlag() && ghRepository.isFork()) {
                        return false;
                    }
                    if (!context.clonePrivateReposFlag() && ghRepository.isPrivate()) {
                        return false;
                    }
                    if (!context.clonePublicReposFlag() && !ghRepository.isPrivate()) {
                        return false;
                    }
                    if (!context.cloneBelongingToOrganizationsReposFlag() && !repositoryUser.getLogin().equals(myself.getLogin())) {
                        return false;
                    }
                    if (context.cloneBelongingToOrganizationsReposFlag() && isOrganizationInBlacklist(repositoryUser, blacklistOrganizationsList)) {
                        return false;
                    }
                    return true;
                })
                // not necessary -> .filter(ghRepository -> !context.cloneArchivedReposFlag() && ghRepository.isArchived())
                .map(ghRepositoryGHUserPair -> mapper.toDTO(ghRepositoryGHUserPair.getLeft(), ghRepositoryGHUserPair.getRight()))
                .toList();

        callbackContainer.updateApplicationStatusMessage().accept("All repositories information were retrieved.");

        return repositoryDTOList;
    }

    private static Pair<GHRepository, GHUser> buildPair(GHRepository ghRepository) {
        try {
            return Pair.of(ghRepository, ghRepository.getOwner());
        } catch (IOException e) {
            throw new CloningSourceException("Unable to retrieve GitHub repository owner(1).", e);
        }
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
