package com.andreidodu.fromgtog.translator;

import com.andreidodu.fromgtog.dto.ToContext;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.type.RepoPrivacyType;
import org.json.JSONException;
import org.json.JSONObject;

import static com.andreidodu.fromgtog.gui.GuiKeys.*;

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

        throw new IllegalArgumentException("Invalid EngineType");

    }

    private ToContext buildToLocalContext(EngineType engineType, JSONObject jsonObject) {
        return new ToContext(
                engineType,
                null,
                null,
                null,
                jsonObject.getString(TO_LOCAL_ROOT_PATH),
                jsonObject.getBoolean(TO_LOCAL_GROUP_BY_OWNER)
        );
    }

    private ToContext buildToGiteaContext(EngineType engineType, JSONObject jsonObject) {
        return new ToContext(
                engineType,
                jsonObject.getString(TO_GITEA_URL),
                jsonObject.getString(TO_GITEA_TOKEN),
                RepoPrivacyType.fromValue(jsonObject.getInt(TO_GITEA_PRIVACY_INDEX)),
                null,
                false
        );
    }

    private ToContext buildToGithubContext(EngineType engineType, JSONObject jsonObject) {
        return new ToContext(
                engineType,
                null,
                jsonObject.getString(TO_GITHUB_TOKEN),
                RepoPrivacyType.fromValue(jsonObject.getInt(TO_GITHUB_PRIVACY_INDEX)),
                null,
                false
        );
    }
}
