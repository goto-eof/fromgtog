package com.andreidodu.fromgtog.translator.impl;

import com.andreidodu.fromgtog.dto.FromContext;
import com.andreidodu.fromgtog.translator.JsonObjectToRecordTranslator;
import com.andreidodu.fromgtog.type.EngineType;
import org.json.JSONException;
import org.json.JSONObject;

import static com.andreidodu.fromgtog.gui.GuiKeys.*;

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

        throw new IllegalArgumentException("Invalid EngineType");

    }

    private FromContext buildFromLocalContext(EngineType engineType, JSONObject jsonObject) {
        return new FromContext(
                engineType,
                null,
                null,
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
        return new FromContext(
                engineType,
                jsonObject.getString(FROM_GITEA_URL),
                jsonObject.getString(FROM_GITEA_TOKEN),
                jsonObject.getBoolean(FROM_GITEA_CLONE_STARRED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITEA_CLONE_FORKED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITEA_CLONE_PRIVATE_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITEA_CLONE_ARCHIVED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITEA_CLONE_ORGANIZATIONS_REPO_FLAG),
                jsonObject.getString(FROM_GITEA_EXCLUDE_ORGANIZATIONS),
                null
        );
    }

    private FromContext buildFromGithubContext(EngineType engineType, JSONObject jsonObject) {
        return new FromContext(
                engineType,
                null,
                jsonObject.getString(FROM_GITHUB_TOKEN),
                jsonObject.getBoolean(FROM_GITHUB_CLONE_STARRED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITHUB_CLONE_FORKED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITHUB_CLONE_PRIVATE_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITHUB_CLONE_ARCHIVED_REPO_FLAG),
                jsonObject.getBoolean(FROM_GITHUB_CLONE_ORGANIZATIONS_REPO_FLAG),
                jsonObject.getString(FROM_GITHUB_EXCLUDE_ORGANIZATIONS),
                null
        );
    }
}
