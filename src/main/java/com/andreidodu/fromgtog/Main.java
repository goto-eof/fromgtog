package com.andreidodu.fromgtog;

import com.andreidodu.fromgtog.service.impl.GuiStarterServiceImpl;
import com.andreidodu.fromgtog.util.LoggingUtil;

public class Main {

    public static void main(String[] args) {
        LoggingUtil.initialize();
        new GuiStarterServiceImpl().start();
    }

}
