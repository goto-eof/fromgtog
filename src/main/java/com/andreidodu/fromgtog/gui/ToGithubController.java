package com.andreidodu.fromgtog.gui;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;

import static com.andreidodu.fromgtog.gui.GuiKeys.*;

@Getter
@Setter
public class ToGithubController implements DataProviderToController {

    private JTextField toGithubTokenTextField;
    private JComboBox toGithubPrivacyComboBox;
    final static int TAB_INDEX = 1;

    public ToGithubController(JTextField toGithubTokenTextField, JComboBox toGithubPrivacyComboBox) {
        this.toGithubTokenTextField = toGithubTokenTextField;
        this.toGithubPrivacyComboBox = toGithubPrivacyComboBox;
    }

    @Override
    public boolean accept(int tabIndex) {
        return TAB_INDEX == tabIndex;
    }

    @Override
    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(TO_GITHUB_TOKEN, toGithubTokenTextField.getText());
        jsonObject.put(TO_GITHUB_TAB_INDEX, toGithubPrivacyComboBox.getSelectedIndex());

        return jsonObject;
    }
}
