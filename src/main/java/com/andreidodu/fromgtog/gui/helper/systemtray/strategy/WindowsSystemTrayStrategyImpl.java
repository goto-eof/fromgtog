package com.andreidodu.fromgtog.gui.helper.systemtray.strategy;

import com.andreidodu.fromgtog.gui.helper.systemtray.SystemTrayStrategy;
import com.andreidodu.fromgtog.util.OsUtil;

public class WindowsSystemTrayStrategyImpl extends ClassicSystemTrayStrategyImpl implements SystemTrayStrategy {

    @Override
    public boolean accept() {
        return OsUtil.isWindows();
    }

}
