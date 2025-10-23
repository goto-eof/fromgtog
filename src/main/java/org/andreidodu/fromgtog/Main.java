package org.andreidodu.fromgtog;

import org.andreidodu.fromgtog.service.impl.GuiStarterServiceImpl;
import org.andreidodu.fromgtog.util.EnvDumpUtil;
import org.andreidodu.fromgtog.util.LoggingUtil;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        LoggingUtil.initialize();
        EnvDumpUtil.logEnvironment();
        createTmpDirectoryIfNecessary();
        new GuiStarterServiceImpl().start();
    }

    private static void createTmpDirectoryIfNecessary() {
        String snapTmp = System.getenv("SNAP_USER_DATA") + "/tmp";
        File tmpDir = new File(snapTmp);
        if (!tmpDir.exists()) tmpDir.mkdirs();
        System.setProperty("java.io.tmpdir", tmpDir.getAbsolutePath());
    }

}
