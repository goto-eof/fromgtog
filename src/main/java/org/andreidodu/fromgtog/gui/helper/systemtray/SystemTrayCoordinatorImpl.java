package org.andreidodu.fromgtog.gui.helper.systemtray;

import org.andreidodu.fromgtog.gui.helper.systemtray.strategy.LinuxSystemTrayStrategyImpl;
import org.andreidodu.fromgtog.gui.helper.systemtray.strategy.MacOsSystemTrayStrategyImpl;
import org.andreidodu.fromgtog.gui.helper.systemtray.strategy.SystemTrayStrategy;
import org.andreidodu.fromgtog.gui.helper.systemtray.strategy.WindowsSystemTrayStrategyImpl;
import org.andreidodu.fromgtog.gui.helper.systemtray.strategy.common.ClassicSystemTrayStrategyImpl;

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
                .filter(SystemTrayStrategy::isSupported)
                .findFirst()
                .orElseGet(() -> new ClassicSystemTrayStrategyImpl() {
                    @Override
                    public boolean accept() {
                        return true;
                    }
                });
    }

}
