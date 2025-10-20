package com.andreidodu.fromgtog.util;

import java.io.File;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.APP_NAME_FOR_DATA;

public class ApplicationUtil {

    public static File getApplicationRootDirectory() {
        String userHome = System.getProperty("user.home");
        return new File(userHome, APP_NAME_FOR_DATA);
    }

    public static String getTemporaryFolderName() {
        return System.getProperty("java.io.tmpdir") + File.separator + "fromgtog";
    }
}
