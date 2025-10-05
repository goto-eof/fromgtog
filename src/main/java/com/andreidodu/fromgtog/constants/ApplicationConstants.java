package com.andreidodu.fromgtog.constants;

public interface ApplicationConstants {
    String ENV_VAR_SETTINGS_FILE = "SETTINGS_FILE";
    String ENV_LOG_FILE_PATH = "ENV_LOG_FILE_PATH";
    String ENV_LOG_FILENAME = "ENV_LOG_FILENAME";
    String APP_NAME_FOR_DATA = "fromgtog";
    String LOG_DIR_NAME = "logs";
    String CONF_DIR_NAME = "conf";
    String LOG_FILENAME = "application.log";
    String SETTINGS_FILENAME = "settings.properties";
    int MAX_VIRTUAL_THREADS = 50;


    String CLONER_PLATFORM_THREAD_NAME_PREFIX = "mr-p-cloner";
    String CLONER_VIRTUAL_THREAD_NAME_PREFIX = "mr-v-cloner";
    String TERMINATOR_THREAD_NAME_PREFIX = "mr-terminator";
    String TICKER_THREAD_NAME_PREFIX = "mr-timer";
    String ORCHESTRATOR_THREAD_NAME_PREFIX = "mr-orchestrator";

}
