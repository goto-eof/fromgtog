package org.andreidodu.fromgtog.util;

import java.io.File;
import java.io.IOException;

import static org.andreidodu.fromgtog.constants.ApplicationConstants.*;
import static org.andreidodu.fromgtog.util.ApplicationUtil.getApplicationRootDirectory;

public class LoggingUtil {

    public static void initialize() {
        File logDir = getLogDir();
        System.setProperty(ENV_LOG_FILE_PATH, logDir.getAbsolutePath());
        System.setProperty(ENV_LOG_FILENAME, LOG_FILENAME);
        prepareLogFile(logDir);
    }

    private static void prepareLogFile(File logDir) {
        File logFile = new File(logDir, LOG_FILENAME);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Unable to create log file: " + e.getMessage());
            }
        }
        System.out.println("log dir: " + logDir.getAbsolutePath());
    }

    private static File getLogDir() {
        File appDataDir = getApplicationRootDirectory();
        File logDir = new File(appDataDir, LOG_DIR_NAME);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        return logDir;
    }


}
