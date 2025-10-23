package org.andreidodu.fromgtog.gui.helper.systemtray.strategy.common;

import dorkbox.systemTray.SystemTray;
import org.andreidodu.fromgtog.dto.Tuple;
import org.andreidodu.fromgtog.gui.helper.systemtray.strategy.SystemTrayStrategy;
import org.andreidodu.fromgtog.util.OsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public abstract class ModernSystemTrayStrategyImpl implements SystemTrayStrategy {

    private final static Logger log = LoggerFactory.getLogger(ClassicSystemTrayStrategyImpl.class);

    @Override
    public boolean isSupported() {
        return SystemTray.get() != null;
    }

    @Override
    public int getTrayIconSize() {
        return SystemTray.get().getTrayImageSize();
    }

    @Override
    public void setMenu(Image image, java.util.List<Tuple<String, Consumer<ActionEvent>>> menuList) {
        log.debug("Setting modern menu...");
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
        log.debug("Setting modern image...");
        SystemTray.get().setImage(image);
    }


}
