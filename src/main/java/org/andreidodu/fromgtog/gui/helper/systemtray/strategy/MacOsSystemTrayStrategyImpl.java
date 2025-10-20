package org.andreidodu.fromgtog.gui.helper.systemtray.strategy;

import org.andreidodu.fromgtog.gui.helper.systemtray.strategy.common.ClassicSystemTrayStrategyImpl;
import org.andreidodu.fromgtog.util.OsUtil;

public class MacOsSystemTrayStrategyImpl extends ClassicSystemTrayStrategyImpl implements SystemTrayStrategy {

    @Override
    public boolean accept() {
        return OsUtil.isMac();
    }

}
