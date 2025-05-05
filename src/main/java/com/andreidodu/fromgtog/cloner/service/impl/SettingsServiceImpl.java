package com.andreidodu.fromgtog.cloner.service.impl;

import com.andreidodu.fromgtog.cloner.service.SettingsService;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class SettingsServiceImpl implements SettingsService {

    public final static String FILENAME = "settings.properties";

    public static final String PROPERTY_PREFIX = "com.andreidodu.fromgtog.";

    public static final String GITHUB_USERNAME = "from.github.username";
    public static final String GITHUB_TOKEN = "from.github.token";
    public static final String GITEA_URL = "to.gitea.url";
    public static final String GITEA_TOKEN = "to.gitea.token";
    public static final String SLEEP_TIME = "sleep-time";
    public static final String THEME = "theme";
    public static final String FORKED_FLAG = "from.github.forked-flag";
    public static final String STARRED_FLAG = "from.github.starred-flag";
    public static final String PRIVATE_FLAG = "from.github.private-flag";
    public static final String ARCHIVED_FLAG = "from.github.archived-flag";
    public static final String GITHUB_IS_CLONE_ORGANIZATION = "from.github.clone-organization-flag";
    public static final String GITHUB_ORGANIZATION_LIST = "from.github.organization-list";
    public static final String GITEA_ALL_REPOS_AS_MIRROR = "to.gitea.all-repos-as-mirror";
    public static final String GITEA_REPLACE_IF_EXISTS = "to.gitea.replace-if-exists";
    public static final String GITEA_REPOSITORY_PRIVACY = "to.gitea.repository-privacy";
    public static final String LOCAL_PATH = "to.local.path";
    public static final String LOCAL_DELETE_IF_EXISTS = "to.local.delete-if-exists";
    public static final String LOCAL_GROUP_BY_REPOSITORY_OWNER = "to.local.group-by-repository-owner";
    public static final String TO_TAB_INDEX = "to.tab-index";

    // From Gitea to ALL
    public static final String FROM_GITEA_URL = "from.gitea.url";
    public static final String FROM_GITEA_TOKEN = "from.gitea.token";
    public static final String FROM_GITEA_EXCLUDE_ORG = "from.gitea.exclude-org";
    public static final String FROM_GITEA_IS_CLONE_STARRED = "from.gitea.is-clone-starred";
    public static final String FROM_GITEA_IS_CLONE_FORKED = "from.gitea.is-clone-forked";
    public static final String FROM_GITEA_TO_LOCAL_PATH = "from.gitea.to.local.path";
    public static final String FROM_GITEA_TO_LOCAL_IS_GROUP_BY = "from.gitea.to.local.is-group-by";
    public static final String FROM_GITEA_TO_LOCAL_IS_DELETE_IF_EXISTS = "from.gitea.to.local.delete-if-exists";
    public static final String FROM_GITEA_IS_CLONE_PRIVATE = "from.gitea.is-clone-private";
    public static final String FROM_GITEA_TO_GITHUB_IS_REPLACE_REPO = "from.gitea.to.github.is-place-repo";
    public static final String FROM_GITEA_IS_CLONE_ARCHIVED = "from.gitea.is-clone-archived";
    public static final String FROM_GITEA_TO_GITHUB_PRIVACY = "from.gitea.to.github.privacy";
    public static final String FROM_GITEA_TO_GITHUB_TOKEN = "from.gitea.to.github.token";
    public static final String MAIN_TAB_INDEX = "main-tab-index";
    public static final String FROM_GITEA_TAB_INDEX = "from.gitea.tab-index";
    public static final String FROM_GITEA_IS_CLONE_ORGANIZATION = "from.gitea.is-clone-organization";

    public static final String FROM_LOCAL_ROOT_PATH = "from.local.root-path";
    public static final String FROM_LOCAL_GITHUB_TOKEN = "from.local.to.github.token";
    public static final String FROM_LOCAL_TO_GITHUB_PRIVACY = "from.local.to.github.privacy";
    public static final String FROM_LOCAL_TO_GITEA_URL = "from.local.to.gitea.url";
    public static final String FROM_LOCAL_TO_GITEA_TOKEN = "from.local.to.gitea.token";
    public static final String FROM_LOCAL_TO_GITEA_PRIVACY = "from.local.to.gitea.privacy";
    public static final String FROM_LOCAL_TABBED_PANE = "from.local.tabbed-pane.index";

    public static final String ENV_VAR_SETTINGS_FILE = "SETTINGS_FILE";


    @Override
    public void save(Map<String, String> map) {
        Configurations configs = new Configurations();
        try {
            new File(retrieveFileName()).createNewFile();
            FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configs.propertiesBuilder(retrieveFileName());
            PropertiesConfiguration config = builder.getConfiguration();
            config.setProperty(PROPERTY_PREFIX + GITHUB_USERNAME, map.get(SettingsServiceImpl.PROPERTY_PREFIX + GITHUB_USERNAME));
            config.setProperty(PROPERTY_PREFIX + GITHUB_TOKEN, map.get(SettingsServiceImpl.PROPERTY_PREFIX + GITHUB_TOKEN));
            config.setProperty(PROPERTY_PREFIX + GITEA_URL, map.get(SettingsServiceImpl.PROPERTY_PREFIX + GITEA_URL));
            config.setProperty(PROPERTY_PREFIX + GITEA_TOKEN, map.get(SettingsServiceImpl.PROPERTY_PREFIX + GITEA_TOKEN));
            config.setProperty(PROPERTY_PREFIX + SLEEP_TIME, map.get(SettingsServiceImpl.PROPERTY_PREFIX + SLEEP_TIME));
            config.setProperty(PROPERTY_PREFIX + THEME, map.get(SettingsServiceImpl.PROPERTY_PREFIX + THEME));
            config.setProperty(PROPERTY_PREFIX + GITHUB_ORGANIZATION_LIST, map.get(SettingsServiceImpl.PROPERTY_PREFIX + GITHUB_ORGANIZATION_LIST));
            config.setProperty(PROPERTY_PREFIX + FORKED_FLAG, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FORKED_FLAG));
            config.setProperty(PROPERTY_PREFIX + STARRED_FLAG, map.get(SettingsServiceImpl.PROPERTY_PREFIX + STARRED_FLAG));
            config.setProperty(PROPERTY_PREFIX + PRIVATE_FLAG, map.get(SettingsServiceImpl.PROPERTY_PREFIX + PRIVATE_FLAG));
            config.setProperty(PROPERTY_PREFIX + ARCHIVED_FLAG, map.get(SettingsServiceImpl.PROPERTY_PREFIX + ARCHIVED_FLAG));
            config.setProperty(PROPERTY_PREFIX + GITHUB_IS_CLONE_ORGANIZATION, map.get(SettingsServiceImpl.PROPERTY_PREFIX + GITHUB_IS_CLONE_ORGANIZATION));

            config.setProperty(PROPERTY_PREFIX + GITEA_ALL_REPOS_AS_MIRROR, map.get(SettingsServiceImpl.PROPERTY_PREFIX + GITEA_ALL_REPOS_AS_MIRROR));
            // config.setProperty(PROPERTY_PREFIX + GITEA_REPLACE_IF_EXISTS, map.get(SettingsUtil.PROPERTY_PREFIX + GITEA_REPLACE_IF_EXISTS));
            config.setProperty(PROPERTY_PREFIX + GITEA_REPOSITORY_PRIVACY, map.get(SettingsServiceImpl.PROPERTY_PREFIX + GITEA_REPOSITORY_PRIVACY));

            config.setProperty(PROPERTY_PREFIX + LOCAL_PATH, map.get(SettingsServiceImpl.PROPERTY_PREFIX + LOCAL_PATH));
            // config.setProperty(PROPERTY_PREFIX + LOCAL_DELETE_IF_EXISTS, map.get(SettingsUtil.PROPERTY_PREFIX + LOCAL_DELETE_IF_EXISTS));
            config.setProperty(PROPERTY_PREFIX + LOCAL_GROUP_BY_REPOSITORY_OWNER, map.get(SettingsServiceImpl.PROPERTY_PREFIX + LOCAL_GROUP_BY_REPOSITORY_OWNER));

            config.setProperty(PROPERTY_PREFIX + TO_TAB_INDEX, map.get(SettingsServiceImpl.PROPERTY_PREFIX + TO_TAB_INDEX));


            config.setProperty(PROPERTY_PREFIX + FROM_GITEA_URL, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_GITEA_URL));
            config.setProperty(PROPERTY_PREFIX + FROM_GITEA_TOKEN, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_GITEA_TOKEN));
            config.setProperty(PROPERTY_PREFIX + FROM_GITEA_EXCLUDE_ORG, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_GITEA_EXCLUDE_ORG));
            config.setProperty(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_STARRED, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_STARRED));
            config.setProperty(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_FORKED, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_FORKED));
            config.setProperty(PROPERTY_PREFIX + FROM_GITEA_TO_LOCAL_PATH, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_GITEA_TO_LOCAL_PATH));
            config.setProperty(PROPERTY_PREFIX + FROM_GITEA_TO_LOCAL_IS_GROUP_BY, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_GITEA_TO_LOCAL_IS_GROUP_BY));
            //config.setProperty(PROPERTY_PREFIX + FROM_GITEA_TO_LOCAL_IS_DELETE_IF_EXISTS, map.get(SettingsUtil.PROPERTY_PREFIX + FROM_GITEA_TO_LOCAL_IS_DELETE_IF_EXISTS));
            config.setProperty(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_PRIVATE, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_PRIVATE));
            //config.setProperty(PROPERTY_PREFIX + FROM_GITEA_TO_GITHUB_IS_REPLACE_REPO, map.get(SettingsUtil.PROPERTY_PREFIX + FROM_GITEA_TO_GITHUB_IS_REPLACE_REPO));
            config.setProperty(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_ORGANIZATION, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_ORGANIZATION));
            config.setProperty(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_ARCHIVED, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_ARCHIVED));
            config.setProperty(PROPERTY_PREFIX + FROM_GITEA_TO_GITHUB_PRIVACY, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_GITEA_TO_GITHUB_PRIVACY));
            config.setProperty(PROPERTY_PREFIX + FROM_GITEA_TO_GITHUB_TOKEN, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_GITEA_TO_GITHUB_TOKEN));
            config.setProperty(PROPERTY_PREFIX + MAIN_TAB_INDEX, map.get(SettingsServiceImpl.PROPERTY_PREFIX + MAIN_TAB_INDEX));
            config.setProperty(PROPERTY_PREFIX + FROM_GITEA_TAB_INDEX, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_GITEA_TAB_INDEX));


            config.setProperty(PROPERTY_PREFIX + FROM_LOCAL_ROOT_PATH, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_LOCAL_ROOT_PATH));
            config.setProperty(PROPERTY_PREFIX + FROM_LOCAL_GITHUB_TOKEN, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_LOCAL_GITHUB_TOKEN));
            config.setProperty(PROPERTY_PREFIX + FROM_LOCAL_TO_GITHUB_PRIVACY, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_LOCAL_TO_GITHUB_PRIVACY));
            config.setProperty(PROPERTY_PREFIX + FROM_LOCAL_TO_GITEA_URL, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_LOCAL_TO_GITEA_URL));
            config.setProperty(PROPERTY_PREFIX + FROM_LOCAL_TO_GITEA_TOKEN, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_LOCAL_TO_GITEA_TOKEN));
            config.setProperty(PROPERTY_PREFIX + FROM_LOCAL_TO_GITEA_PRIVACY, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_LOCAL_TO_GITEA_PRIVACY));
            config.setProperty(PROPERTY_PREFIX + FROM_LOCAL_TABBED_PANE, map.get(SettingsServiceImpl.PROPERTY_PREFIX + FROM_LOCAL_TABBED_PANE));


            builder.save();
        } catch (ConfigurationException | IOException cex) {
            System.err.println("unable to save settings" + cex.getMessage());
        }
    }

    @Override
    public Map<String, String> load() {
        Map<String, String> map = new HashMap<>();
        Configurations configs = new Configurations();
        try {
            File file = new File(retrieveFileName());
            if (!file.exists()) {
                file.createNewFile();
            }
            Configuration config = configs.properties(file);
            map.put(PROPERTY_PREFIX + GITHUB_USERNAME, config.getString(PROPERTY_PREFIX + GITHUB_USERNAME));
            map.put(PROPERTY_PREFIX + GITHUB_TOKEN, config.getString(PROPERTY_PREFIX + GITHUB_TOKEN));
            map.put(PROPERTY_PREFIX + GITEA_URL, config.getString(PROPERTY_PREFIX + GITEA_URL));
            map.put(PROPERTY_PREFIX + GITEA_TOKEN, config.getString(PROPERTY_PREFIX + GITEA_TOKEN));
            map.put(PROPERTY_PREFIX + SLEEP_TIME, config.getString(PROPERTY_PREFIX + SLEEP_TIME));
            map.put(PROPERTY_PREFIX + THEME, config.getString(PROPERTY_PREFIX + THEME));
            map.put(PROPERTY_PREFIX + GITHUB_ORGANIZATION_LIST, config.getString(PROPERTY_PREFIX + GITHUB_ORGANIZATION_LIST));
            map.put(PROPERTY_PREFIX + FORKED_FLAG, config.getString(PROPERTY_PREFIX + FORKED_FLAG));
            map.put(PROPERTY_PREFIX + STARRED_FLAG, config.getString(PROPERTY_PREFIX + STARRED_FLAG));
            map.put(PROPERTY_PREFIX + PRIVATE_FLAG, config.getString(PROPERTY_PREFIX + PRIVATE_FLAG));
            map.put(PROPERTY_PREFIX + ARCHIVED_FLAG, config.getString(PROPERTY_PREFIX + ARCHIVED_FLAG));
            map.put(PROPERTY_PREFIX + GITHUB_IS_CLONE_ORGANIZATION, config.getString(PROPERTY_PREFIX + GITHUB_IS_CLONE_ORGANIZATION));

            map.put(PROPERTY_PREFIX + GITEA_ALL_REPOS_AS_MIRROR, config.getString(PROPERTY_PREFIX + GITEA_ALL_REPOS_AS_MIRROR));
            // map.put(PROPERTY_PREFIX + GITEA_REPLACE_IF_EXISTS, config.getString(PROPERTY_PREFIX + GITEA_REPLACE_IF_EXISTS));
            map.put(PROPERTY_PREFIX + GITEA_REPOSITORY_PRIVACY, config.getString(PROPERTY_PREFIX + GITEA_REPOSITORY_PRIVACY));

            map.put(PROPERTY_PREFIX + LOCAL_PATH, config.getString(PROPERTY_PREFIX + LOCAL_PATH));
            //map.put(PROPERTY_PREFIX + LOCAL_DELETE_IF_EXISTS, config.getString(PROPERTY_PREFIX + LOCAL_DELETE_IF_EXISTS));
            map.put(PROPERTY_PREFIX + LOCAL_GROUP_BY_REPOSITORY_OWNER, config.getString(PROPERTY_PREFIX + LOCAL_GROUP_BY_REPOSITORY_OWNER));

            map.put(PROPERTY_PREFIX + TO_TAB_INDEX, config.getString(PROPERTY_PREFIX + TO_TAB_INDEX));

            map.put(PROPERTY_PREFIX + FROM_GITEA_URL, config.getString(PROPERTY_PREFIX + FROM_GITEA_URL));
            map.put(PROPERTY_PREFIX + FROM_GITEA_TOKEN, config.getString(PROPERTY_PREFIX + FROM_GITEA_TOKEN));
            map.put(PROPERTY_PREFIX + FROM_GITEA_EXCLUDE_ORG, config.getString(PROPERTY_PREFIX + FROM_GITEA_EXCLUDE_ORG));
            map.put(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_STARRED, config.getString(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_STARRED));
            map.put(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_FORKED, config.getString(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_FORKED));
            map.put(PROPERTY_PREFIX + FROM_GITEA_TO_LOCAL_PATH, config.getString(PROPERTY_PREFIX + FROM_GITEA_TO_LOCAL_PATH));
            map.put(PROPERTY_PREFIX + FROM_GITEA_TO_LOCAL_IS_GROUP_BY, config.getString(PROPERTY_PREFIX + FROM_GITEA_TO_LOCAL_IS_GROUP_BY));
            //map.put(PROPERTY_PREFIX + FROM_GITEA_TO_LOCAL_IS_DELETE_IF_EXISTS, config.getString(PROPERTY_PREFIX + FROM_GITEA_TO_LOCAL_IS_DELETE_IF_EXISTS));
            map.put(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_PRIVATE, config.getString(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_PRIVATE));
            //map.put(PROPERTY_PREFIX + FROM_GITEA_TO_GITHUB_IS_REPLACE_REPO, config.getString(PROPERTY_PREFIX + FROM_GITEA_TO_GITHUB_IS_REPLACE_REPO));
            map.put(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_ORGANIZATION, config.getString(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_ORGANIZATION));
            map.put(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_ARCHIVED, config.getString(PROPERTY_PREFIX + FROM_GITEA_IS_CLONE_ARCHIVED));
            map.put(PROPERTY_PREFIX + FROM_GITEA_TO_GITHUB_PRIVACY, config.getString(PROPERTY_PREFIX + FROM_GITEA_TO_GITHUB_PRIVACY));
            map.put(PROPERTY_PREFIX + FROM_GITEA_TO_GITHUB_TOKEN, config.getString(PROPERTY_PREFIX + FROM_GITEA_TO_GITHUB_TOKEN));
            map.put(PROPERTY_PREFIX + MAIN_TAB_INDEX, config.getString(PROPERTY_PREFIX + MAIN_TAB_INDEX));
            map.put(PROPERTY_PREFIX + FROM_GITEA_TAB_INDEX, config.getString(PROPERTY_PREFIX + FROM_GITEA_TAB_INDEX));


            map.put(PROPERTY_PREFIX + FROM_LOCAL_ROOT_PATH, config.getString(PROPERTY_PREFIX + FROM_LOCAL_ROOT_PATH));
            map.put(PROPERTY_PREFIX + FROM_LOCAL_GITHUB_TOKEN, config.getString(PROPERTY_PREFIX + FROM_LOCAL_GITHUB_TOKEN));
            map.put(PROPERTY_PREFIX + FROM_LOCAL_TO_GITHUB_PRIVACY, config.getString(PROPERTY_PREFIX + FROM_LOCAL_TO_GITHUB_PRIVACY));
            map.put(PROPERTY_PREFIX + FROM_LOCAL_TO_GITEA_URL, config.getString(PROPERTY_PREFIX + FROM_LOCAL_TO_GITEA_URL));
            map.put(PROPERTY_PREFIX + FROM_LOCAL_TO_GITEA_TOKEN, config.getString(PROPERTY_PREFIX + FROM_LOCAL_TO_GITEA_TOKEN));
            map.put(PROPERTY_PREFIX + FROM_LOCAL_TO_GITEA_PRIVACY, config.getString(PROPERTY_PREFIX + FROM_LOCAL_TO_GITEA_PRIVACY));
            map.put(PROPERTY_PREFIX + FROM_LOCAL_TABBED_PANE, config.getString(PROPERTY_PREFIX + FROM_LOCAL_TABBED_PANE));


        } catch (ConfigurationException | IOException e) {
            System.err.println("unable to load settings" + e.getMessage());
        }
        return map;
    }

    private static String retrieveFileName() {
        String envVarSettngsFile = System.getenv(ENV_VAR_SETTINGS_FILE);
        return StringUtils.isNotBlank(envVarSettngsFile) ? envVarSettngsFile : FILENAME;
    }

}
