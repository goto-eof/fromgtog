package org.andreidodu.fromgtog.util;

import dorkbox.systemTray.SystemTray;

public class SystemTrayUtil {


    public static void enableDorkboxSystemTrayDebugMode() {
        SystemTray.DEBUG = true;
        System.setProperty("dorkbox.systemTray.DEBUG", "true");
    }

}
