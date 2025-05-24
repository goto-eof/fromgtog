package com.andreidodu.fromgtog;

import com.andreidodu.fromgtog.service.impl.GuiStarterServiceImpl;

import java.io.File;

public class Main {
    private static final String FROMGTOG_LOG_FILE_PATH = "FROMGTOG_LOG_FILE_PATH";
    private static final String APP_NAME_FOR_DATA = ".fromgtog";
    private static final String LOG_DIR_NAME = "logs";
    public static final String CONF_DIR_NAME = "conf";

    public static void main(String[] args) {
        File appDataDir = getApplicationRootDirectory();
        File logDir = new File(appDataDir, LOG_DIR_NAME);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }


        System.setProperty(FROMGTOG_LOG_FILE_PATH, logDir.getAbsolutePath());
        new GuiStarterServiceImpl().start();
    }

    public static File getApplicationRootDirectory() {
        String userHome = System.getProperty("user.home");
        return new File(userHome, APP_NAME_FOR_DATA);
    }
}
