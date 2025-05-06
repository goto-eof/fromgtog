package com.andreidodu.fromgtog.gui.controller.impl;

import com.andreidodu.fromgtog.gui.controller.DataProviderToController;
import com.andreidodu.fromgtog.type.EngineType;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import java.io.File;

import static com.andreidodu.fromgtog.gui.GuiKeys.*;

@Getter
@Setter
public class ToLocalController implements DataProviderToController {

    private final static Logger log = LoggerFactory.getLogger(ToLocalController.class);

    private JTextField toLocalRootPathTextField;
    private JCheckBox toLocalGroupByRepositoryOwnerCheckBox;
    private JButton toLocalChooseButton;
    final static int TAB_INDEX = EngineType.LOCAL.getValue();

    public ToLocalController(JSONObject settings, JTextField toLocalRootPathTextField, JCheckBox toLocalGroupByRepositoryOwnerCheckBox, JButton toLocalChooseButton) {
        this.toLocalRootPathTextField = toLocalRootPathTextField;
        this.toLocalGroupByRepositoryOwnerCheckBox = toLocalGroupByRepositoryOwnerCheckBox;
        this.toLocalChooseButton = toLocalChooseButton;


        applySettings(settings);
        addActionListenerToChooseButton();

    }

    private void applySettings(JSONObject settings) {

    }

    private void addActionListenerToChooseButton() {
        this.toLocalChooseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select a Directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedDir = chooser.getSelectedFile();
                toLocalRootPathTextField.setText(selectedDir.getAbsolutePath());
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

        jsonObject.put(TO_LOCAL_ROOT_PATH, toLocalRootPathTextField.getText());
        jsonObject.put(TO_LOCAL_GROUP_BY_OWNER, toLocalGroupByRepositoryOwnerCheckBox.isSelected());
        jsonObject.put(ENGINE_TYPE, EngineType.fromValue(TAB_INDEX));

        return jsonObject;
    }
}
