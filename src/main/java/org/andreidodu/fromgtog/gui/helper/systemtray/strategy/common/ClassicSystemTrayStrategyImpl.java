package org.andreidodu.fromgtog.gui.helper.systemtray.strategy.common;

import org.andreidodu.fromgtog.dto.Tuple;
import org.andreidodu.fromgtog.gui.helper.systemtray.strategy.SystemTrayStrategy;
import org.andreidodu.fromgtog.util.OsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.function.Consumer;

public abstract class ClassicSystemTrayStrategyImpl implements SystemTrayStrategy {

    private final static Logger log = LoggerFactory.getLogger(ClassicSystemTrayStrategyImpl.class);

    @Override
    public boolean isSupported() {
        return SystemTray.isSupported();
    }

    @Override
    public int getTrayIconSize() {
        if (OsUtil.isInsideLinuxSnap()) {
            return 16;
        }
        return (int) SystemTray.getSystemTray().getTrayIconSize().getWidth();
    }

    @Override
    public void setMenu(Image image, java.util.List<Tuple<String, Consumer<ActionEvent>>> menuList) {
        log.debug("Setting classic menu...");
        PopupMenu menu = new PopupMenu("FromGtoG System Tray");
        menuList.stream()
                .map(item -> {
                    MenuItem menuItem = new MenuItem(item.a());
                    menuItem.addActionListener(e -> item.b().accept(e));
                    return menuItem;
                })
                .forEach(menu::add);

        TrayIcon trayIcon = new TrayIcon(image, "FromGtoG System Tray", menu);

        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void setImage(Image image) {
        log.debug("Setting classic image...");
        Arrays.stream(SystemTray.getSystemTray().getTrayIcons())
                .findFirst()
                .ifPresent(systemTrayIcon -> systemTrayIcon.setImage(image));
    }

}
