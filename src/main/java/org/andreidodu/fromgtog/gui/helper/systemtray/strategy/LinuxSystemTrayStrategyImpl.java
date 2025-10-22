package org.andreidodu.fromgtog.gui.helper.systemtray.strategy;

import org.andreidodu.fromgtog.gui.helper.systemtray.strategy.common.ClassicSystemTrayStrategyImpl;
import org.andreidodu.fromgtog.util.OsUtil;

import java.awt.*;

public class LinuxSystemTrayStrategyImpl extends ClassicSystemTrayStrategyImpl implements SystemTrayStrategy {

    @Override
    public boolean accept() {
        return OsUtil.isLinux();
    }

    @Override
    public int getTrayIconSize() {
        return 16;
    }

}
