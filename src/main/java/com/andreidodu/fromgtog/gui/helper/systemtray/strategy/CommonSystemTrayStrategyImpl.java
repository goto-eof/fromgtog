package com.andreidodu.fromgtog.gui.helper.systemtray.strategy;

import com.andreidodu.fromgtog.dto.Tuple;
import com.andreidodu.fromgtog.gui.helper.systemtray.SystemTrayStrategy;
import dorkbox.systemTray.SystemTray;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public abstract class CommonSystemTrayStrategyImpl implements SystemTrayStrategy {


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

    public void setImage(Image image) {
        SystemTray.get().setImage(image);
    }


}
