package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.service.SettingsService;
import com.andreidodu.fromgtog.util.ApplicationUtil;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.*;


public class SettingsServiceImpl implements SettingsService {

    private Logger log = LoggerFactory.getLogger(SettingsServiceImpl.class);
    private static SettingsServiceImpl instance;


    public static SettingsServiceImpl getInstance() {
        if (instance == null) {
            instance = new SettingsServiceImpl();
        }
        return instance;
    }

    @Override
    public void save(JSONObject json) {
        Configurations configs = new Configurations();
        try {
            new File(retrieveFileName()).createNewFile();
            FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configs.propertiesBuilder(retrieveFileName());
            PropertiesConfiguration config = builder.getConfiguration();
            json.keySet().forEach(key -> config.setProperty(key, json.optString(key)));
            builder.save();
        } catch (ConfigurationException | IOException cex) {
            log.error("unable to save settings: {}", cex.getMessage());
        }
    }

    @Override
    public JSONObject load() {

        JSONObject jsonObject = new JSONObject();
        Configurations configs = new Configurations();
        try {
            File file = new File(retrieveFileName());
            if (!file.exists()) {
                file.createNewFile();
            }
            Configuration config = configs.properties(file);
            config.getKeys().forEachRemaining(key -> jsonObject.put(key, config.getString(key)));
        } catch (ConfigurationException | IOException e) {
            log.error("unable to load settings: {}", e.getMessage());
        }
        return jsonObject;
    }

    private String retrieveFileName() {
        File appDataDir = ApplicationUtil.getApplicationRootDirectory();
        File settingsDirName = new File(appDataDir, CONF_DIR_NAME);
        if (!settingsDirName.exists()) {
            settingsDirName.mkdirs();
        }
        File settingsFile = new File(settingsDirName, SETTINGS_FILENAME);
        log.debug("settings file path: {}", settingsFile.getAbsolutePath());
        String envVarSettngsFile = System.getenv(ENV_VAR_SETTINGS_FILE);
        return StringUtils.isNotBlank(envVarSettngsFile) ? envVarSettngsFile : settingsFile.getPath();
    }

}
