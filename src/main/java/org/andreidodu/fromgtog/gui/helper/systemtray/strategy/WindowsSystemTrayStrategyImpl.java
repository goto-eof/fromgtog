package org.andreidodu.fromgtog.gui.helper.systemtray.strategy;

import org.andreidodu.fromgtog.gui.helper.systemtray.strategy.common.ClassicSystemTrayStrategyImpl;
import org.andreidodu.fromgtog.util.OsUtil;

public class WindowsSystemTrayStrategyImpl extends ClassicSystemTrayStrategyImpl implements SystemTrayStrategy {

    @Override
    public boolean accept() {
        return OsUtil.isWindows();
    }

}
