package org.andreidodu.fromgtog.gui.controller.translator.impl;

import org.andreidodu.fromgtog.dto.ToContext;
import org.andreidodu.fromgtog.gui.controller.translator.JsonObjectToRecordTranslator;
import org.andreidodu.fromgtog.type.EngineType;
import org.andreidodu.fromgtog.type.RepoPrivacyType;
import org.json.JSONException;
import org.json.JSONObject;

import static org.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class JsonObjectToToContextTranslator implements JsonObjectToRecordTranslator<ToContext> {
    @Override
    public ToContext translate(JSONObject jsonObject) throws JSONException {
        EngineType engineType = jsonObject.getEnum(EngineType.class, ENGINE_TYPE);

        if (EngineType.GITHUB.equals(engineType)) {
            return buildToGithubContext(engineType, jsonObject);
        }

        if (EngineType.GITEA.equals(engineType)) {
            return buildToGiteaContext(engineType, jsonObject);
        }

        if (EngineType.LOCAL.equals(engineType)) {
            return buildToLocalContext(engineType, jsonObject);
        }

        if (EngineType.GITLAB.equals(engineType)) {
            return buildToGitlabContext(engineType, jsonObject);
        }

        throw new IllegalArgumentException("Invalid EngineType");

    }

    private ToContext buildToLocalContext(EngineType engineType, JSONObject jsonObject) {
        return new ToContext(
                engineType,
                null,
                null,
                null,
                jsonObject.getString(TO_LOCAL_ROOT_PATH),
                jsonObject.getBoolean(TO_LOCAL_GROUP_BY_OWNER),
                jsonObject.getBoolean(TO_LOCAL_OVERRIDE_IF_EXISTS)
        );
    }

    private ToContext buildToGiteaContext(EngineType engineType, JSONObject jsonObject) {
        return new ToContext(
                engineType,
                jsonObject.getString(TO_GITEA_URL),
                jsonObject.getString(TO_GITEA_TOKEN),
                RepoPrivacyType.fromValue(jsonObject.getInt(TO_GITEA_PRIVACY_INDEX)),
                null,
                false,
                jsonObject.getBoolean(TO_GITEA_OVERRIDE_IF_EXISTS)
        );
    }

    private ToContext buildToGitlabContext(EngineType engineType, JSONObject jsonObject) {
        return new ToContext(
                engineType,
                jsonObject.getString(TO_GITLAB_URL),
                jsonObject.getString(TO_GITLAB_TOKEN),
                RepoPrivacyType.fromValue(jsonObject.getInt(TO_GITLAB_PRIVACY_INDEX)),
                null,
                false,
                jsonObject.getBoolean(TO_GITLAB_OVERRIDE_IF_EXISTS)
        );
    }

    private ToContext buildToGithubContext(EngineType engineType, JSONObject jsonObject) {
        return new ToContext(
                engineType,
                null,
                jsonObject.getString(TO_GITHUB_TOKEN),
                RepoPrivacyType.fromValue(jsonObject.getInt(TO_GITHUB_PRIVACY_INDEX)),
                null,
                false,
                jsonObject.getBoolean(TO_GITHUB_OVERRIDE_IF_EXISTS)
        );
    }
}
