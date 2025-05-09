package com.andreidodu.fromgtog.gui.controller.translator.impl;

import com.andreidodu.fromgtog.dto.FromContext;
import com.andreidodu.fromgtog.service.GitHubService;
import com.andreidodu.fromgtog.service.impl.GitHubServiceImpl;
import com.andreidodu.fromgtog.service.impl.GiteaServiceImpl;
import com.andreidodu.fromgtog.gui.controller.translator.JsonObjectToRecordTranslator;
import com.andreidodu.fromgtog.service.impl.GitlabServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;
import org.json.JSONException;
import org.json.JSONObject;
import org.kohsuke.github.GitHub;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class JsonObjectToFromContextTranslator implements JsonObjectToRecordTranslator<FromContext> {
    @Override
    public FromContext translate(JSONObject jsonObject) throws JSONException {
        EngineType engineType = jsonObject.getEnum(EngineType.class, ENGINE_TYPE);

        if (EngineType.GITHUB.equals(engineType)) {
            return buildFromGithubContext(engineType, jsonObject);
        }

        if (EngineType.GITEA.equals(engineType)) {
            return buildFromGiteaContext(engineType, jsonObject);
        }

        if (EngineType.LOCAL.equals(engineType)) {
            return buildFromLocalContext(engineType, jsonObject);
        }

        if (EngineType.GITLAB.equals(engineType)) {
            return buildFromGitlabContext(engineType, jsonObject);
        }

        throw new IllegalArgumentException("Invalid EngineType");

    }

    private FromContext buildFromLocalContext(EngineType engineType, JSONObject jsonObject) {
        return new FromContext(
                engineType,
                null,
                null,
                null,
                false,
                false,
                false,
                false,
                false,
                false,
                null,
                jsonObject.getString(FROM_LOCAL_ROOT_PATH)
        );
    }

    private FromContext buildFromGiteaContext(EngineType engineType, JSONObject jsonObject) {
        String giteaLogin = getGiteaLogin(jsonObject);
        return new FromContext(
                engineType,
                jsonObject.getString(FROM_GITEA_URL),
                giteaLogin,
                jsonObject.getString(FROM_GITEA_TOKEN),
                jsonObject.getBoolean(FROM_GITEA_CLONE_STARRED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITEA_CLONE_FORKED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITEA_CLONE_PRIVATE_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITEA_CLONE_PUBLIC_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITEA_CLONE_ARCHIVED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITEA_CLONE_ORGANIZATIONS_REPO_FLAG),
                jsonObject.getString(FROM_GITEA_EXCLUDE_ORGANIZATIONS),
                null
        );
    }


    private FromContext buildFromGitlabContext(EngineType engineType, JSONObject jsonObject) {
        String giteaLogin = getGitlabLogin(jsonObject);
        return new FromContext(
                engineType,
                jsonObject.getString(FROM_GITLAB_URL),
                giteaLogin,
                jsonObject.getString(FROM_GITLAB_TOKEN),
                jsonObject.getBoolean(FROM_GITLAB_CLONE_STARRED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITLAB_CLONE_FORKED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITLAB_CLONE_PRIVATE_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITLAB_CLONE_PUBLIC_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITLAB_CLONE_ARCHIVED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITLAB_CLONE_ORGANIZATIONS_REPO_FLAG),
                jsonObject.getString(FROM_GITLAB_EXCLUDE_ORGANIZATIONS),
                null
        );
    }

    private String getGiteaLogin(JSONObject jsonObject) {
        return GiteaServiceImpl.getInstance().getLogin(jsonObject.getString(FROM_GITEA_TOKEN), jsonObject.getString(FROM_GITEA_URL));
    }

    private String getGitlabLogin(JSONObject jsonObject) {
        return GitlabServiceImpl.getInstance().getLogin(jsonObject.getString(FROM_GITLAB_TOKEN), jsonObject.getString(FROM_GITLAB_URL));
    }

    private FromContext buildFromGithubContext(EngineType engineType, JSONObject jsonObject) {
        String githubLogin = getGitHubLogin(jsonObject);
        return new FromContext(
                engineType,
                null,
                githubLogin,
                jsonObject.getString(FROM_GITHUB_TOKEN),
                jsonObject.getBoolean(FROM_GITHUB_CLONE_STARRED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITHUB_CLONE_FORKED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITHUB_CLONE_PRIVATE_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITHUB_CLONE_PUBLIC_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITHUB_CLONE_ARCHIVED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITHUB_CLONE_ORGANIZATIONS_REPO_FLAG),
                jsonObject.getString(FROM_GITHUB_EXCLUDE_ORGANIZATIONS),
                null
        );
    }

    private static String getGitHubLogin(JSONObject jsonObject) {
        GitHubService gitHubService = GitHubServiceImpl.getInstance();
        GitHub githubClient = gitHubService.retrieveGitHubClient(jsonObject.getString(FROM_GITHUB_TOKEN));
        return gitHubService.retrieveGitHubMyself(githubClient).getLogin();
    }
}
