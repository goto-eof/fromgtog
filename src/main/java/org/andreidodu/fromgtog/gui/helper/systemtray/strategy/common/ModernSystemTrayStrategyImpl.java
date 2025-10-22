package org.andreidodu.fromgtog.gui.helper.systemtray.strategy.common;

import dorkbox.systemTray.SystemTray;
import org.andreidodu.fromgtog.dto.Tuple;
import org.andreidodu.fromgtog.gui.helper.systemtray.strategy.SystemTrayStrategy;
import org.andreidodu.fromgtog.util.OsUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public abstract class ModernSystemTrayStrategyImpl implements SystemTrayStrategy {

    @Override
    public boolean isSupported() {
        return SystemTray.get() != null;
    }

    @Override
    public int getTrayIconSize() {
        if (OsUtil.isLinux()) {
            return 16;
        }
        return SystemTray.get().getTrayImageSize();
    }

    @Override
    public void setMenu(Image image, java.util.List<Tuple<String, Consumer<ActionEvent>>> menuList) {
        JMenu menu = new JMenu("FromGtoG System Tray");
        menuList.stream().map(item -> {
                    JMenuItem menuItem = new JMenuItem(item.a());
                    menuItem.addActionListener(e -> {
                        item.b().accept(e);
                    });
                    return menuItem;
                })
                .forEach(menu::add);

        SystemTray.get().setMenu(menu);
        SystemTray.get().setImage(image);
    }

    @Override
    public void setImage(Image image) {
        SystemTray.get().setImage(image);
    }


}
