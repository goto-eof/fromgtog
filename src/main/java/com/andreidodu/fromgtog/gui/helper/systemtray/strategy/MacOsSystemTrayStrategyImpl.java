package com.andreidodu.fromgtog.gui.helper.systemtray.strategy;

import com.andreidodu.fromgtog.dto.Tuple;
import com.andreidodu.fromgtog.gui.helper.systemtray.SystemTrayStrategy;
import com.andreidodu.fromgtog.util.OsUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.function.Consumer;

public class MacOsSystemTrayStrategyImpl implements SystemTrayStrategy {

    @Override
    public boolean accept() {
        return OsUtil.isMac();
    }

    @Override
    public boolean isSupported() {
        return SystemTray.isSupported();
    }

    @Override
    public int getTrayIconSize() {
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
