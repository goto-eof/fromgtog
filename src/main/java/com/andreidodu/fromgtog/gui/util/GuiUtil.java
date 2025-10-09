package com.andreidodu.fromgtog.gui.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;

public class GuiUtil {

    final static Logger log = LoggerFactory.getLogger(GuiUtil.class);


    public static void addActionListenerToChooseReposListFileButton(final JButton button, final JTextField textField) {
        button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select a file");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(true);
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedDir = chooser.getSelectedFile();
                textField.setText(selectedDir.getAbsolutePath());
                log.debug("Selected file: {}", selectedDir.getAbsolutePath());
            }
        });
    }

}
