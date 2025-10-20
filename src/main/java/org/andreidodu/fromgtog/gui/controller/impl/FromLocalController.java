package org.andreidodu.fromgtog.gui.controller.impl;

import org.andreidodu.fromgtog.gui.controller.GUIFromController;
import org.andreidodu.fromgtog.type.EngineType;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;

import static org.andreidodu.fromgtog.gui.controller.constants.GuiKeys.ENGINE_TYPE;
import static org.andreidodu.fromgtog.gui.controller.constants.GuiKeys.FROM_LOCAL_ROOT_PATH;

@Getter
@Setter
public class FromLocalController implements GUIFromController {
    private final static Logger log = LoggerFactory.getLogger(FromLocalController.class);
    private final static int TAB_INDEX = EngineType.LOCAL.getValue();
    private JTextField fromLocalRootPathTextField;
    private JButton fromLocalChooseButton;

    public FromLocalController(JSONObject settings, JTextField fromLocalRootPathTextField, JButton fromLocalChooseButton) {
        this.fromLocalRootPathTextField = fromLocalRootPathTextField;
        this.fromLocalChooseButton = fromLocalChooseButton;

        applySettings(settings);
        addActionListenerToChooseButton();
    }

    @Override
    public void applySettings(JSONObject settings) {
        fromLocalRootPathTextField.setText(settings.optString(FROM_LOCAL_ROOT_PATH));
    }

    private void addActionListenerToChooseButton() {
        this.fromLocalChooseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select a Directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedDir = chooser.getSelectedFile();
                fromLocalRootPathTextField.setText(selectedDir.getAbsolutePath());
                log.debug("Selected Directory: {}", selectedDir.getAbsolutePath());
            }
        });
    }

    @Override
    public boolean accept(int tabIndex) {
        return TAB_INDEX == tabIndex;
    }

    @Override
    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(FROM_LOCAL_ROOT_PATH, fromLocalRootPathTextField.getText());
        jsonObject.put(ENGINE_TYPE, EngineType.fromValue(TAB_INDEX));

        return jsonObject;
    }
}
