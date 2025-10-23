package org.andreidodu.fromgtog.gui.helper.systemtray.strategy.common;

import org.andreidodu.fromgtog.dto.Tuple;
import org.andreidodu.fromgtog.gui.helper.systemtray.strategy.SystemTrayStrategy;
import org.andreidodu.fromgtog.util.OsUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.function.Consumer;

public abstract class ClassicSystemTrayStrategyImpl implements SystemTrayStrategy {

    @Override
    public boolean isSupported() {
        return SystemTray.isSupported();
    }

    @Override
    public int getTrayIconSize() {
        if (OsUtil.isLinux()) {
            return 16;
        }
        return (int) SystemTray.getSystemTray().getTrayIconSize().getWidth();
    }

    @Override
    public void setMenu(Image image, java.util.List<Tuple<String, Consumer<ActionEvent>>> menuList) {
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
            e.printStackTrace();
        }
    }

    @Override
    public void setImage(Image image) {
        Arrays.stream(SystemTray.getSystemTray().getTrayIcons())
                .findFirst()
                .ifPresent(systemTrayIcon -> systemTrayIcon.setImage(image));
    }

}
