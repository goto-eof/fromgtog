package org.andreidodu.fromgtog;

import org.andreidodu.fromgtog.service.impl.GuiStarterServiceImpl;
import org.andreidodu.fromgtog.util.EnvDumpUtil;
import org.andreidodu.fromgtog.util.LoggingUtil;

public class Main {

    public static void main(String[] args) {
        LoggingUtil.initialize();
        EnvDumpUtil.logEnvironment();
        new GuiStarterServiceImpl().start();
    }

}
