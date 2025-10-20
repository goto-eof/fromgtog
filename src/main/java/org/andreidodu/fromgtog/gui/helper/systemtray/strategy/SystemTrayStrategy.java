package org.andreidodu.fromgtog.gui.helper.systemtray.strategy;

import org.andreidodu.fromgtog.dto.Tuple;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public interface SystemTrayStrategy {
    boolean accept();

    boolean isSupported();

    int getTrayIconSize();

    void setMenu(Image image, java.util.List<Tuple<String, Consumer<ActionEvent>>> menuList);

    void setImage(Image image);
}
