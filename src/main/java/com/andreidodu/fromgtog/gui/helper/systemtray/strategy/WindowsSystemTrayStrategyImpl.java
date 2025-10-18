package com.andreidodu.fromgtog.gui.helper.systemtray.strategy;

import com.andreidodu.fromgtog.gui.helper.systemtray.SystemTrayStrategy;
import com.andreidodu.fromgtog.util.OsUtil;
import dorkbox.systemTray.SystemTray;

public class WindowsSystemTrayStrategyImpl extends CommonSystemTrayStrategyImpl implements SystemTrayStrategy {

    @Override
    public boolean accept() {
        return OsUtil.isWindows();
    }


    @Override
    public boolean isSupported() {
        return SystemTray.get() != null;
    }

    @Override
    public int getTrayIconSize() {
        return SystemTray.get().getTrayImageSize();
    }

}
