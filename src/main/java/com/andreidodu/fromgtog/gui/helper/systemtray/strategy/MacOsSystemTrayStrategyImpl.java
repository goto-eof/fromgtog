package com.andreidodu.fromgtog.gui.helper.systemtray.strategy;

import com.andreidodu.fromgtog.gui.helper.systemtray.SystemTrayStrategy;
import com.andreidodu.fromgtog.util.OsUtil;

public class MacOsSystemTrayStrategyImpl extends ClassicSystemTrayStrategyImpl implements SystemTrayStrategy {

    @Override
    public boolean accept() {
        return OsUtil.isMac();
    }

}
