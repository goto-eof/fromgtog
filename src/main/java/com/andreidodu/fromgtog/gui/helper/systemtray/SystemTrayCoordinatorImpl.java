package com.andreidodu.fromgtog.gui.helper.systemtray;

import com.andreidodu.fromgtog.gui.helper.systemtray.strategy.LinuxSystemTrayStrategyImpl;
import com.andreidodu.fromgtog.gui.helper.systemtray.strategy.MacOsSystemTrayStrategyImpl;
import com.andreidodu.fromgtog.gui.helper.systemtray.strategy.WindowsSystemTrayStrategyImpl;

import java.util.List;

public class SystemTrayCoordinatorImpl {

    private final List<SystemTrayStrategy> systemTrayStrategyList = List.of(
            new LinuxSystemTrayStrategyImpl(),
            new MacOsSystemTrayStrategyImpl(),
            new WindowsSystemTrayStrategyImpl()
    );


    public SystemTrayStrategy getSystemTrayStrategy() {
        return systemTrayStrategyList.stream()
                .filter(SystemTrayStrategy::accept)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("System tray strategy not found"));
    }

}
