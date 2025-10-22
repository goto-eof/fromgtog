package org.andreidodu.fromgtog.gui.helper.systemtray.strategy;

import org.andreidodu.fromgtog.gui.helper.systemtray.strategy.common.ModernSystemTrayStrategyImpl;
import org.andreidodu.fromgtog.util.OsUtil;

public class LinuxSystemTrayStrategyImpl extends ModernSystemTrayStrategyImpl implements SystemTrayStrategy {

    @Override
    public boolean accept() {
        return OsUtil.isLinux();
    }

    @Override
    public int getTrayIconSize() {
        return 16;
    }

}
