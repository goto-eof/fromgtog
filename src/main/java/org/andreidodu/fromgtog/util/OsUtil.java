package org.andreidodu.fromgtog.util;

public class OsUtil {

    public static boolean isWindows() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.toLowerCase().startsWith("windows");
    }

    public static boolean isMac() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.toLowerCase().startsWith("mac");
    }

    public static boolean isLinux() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("nix") || osName.contains("nux") || osName.startsWith("linux");
    }
}
