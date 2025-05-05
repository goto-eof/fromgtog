package com.andreidodu.fromgtog.gui.controller.impl;

import com.andreidodu.fromgtog.gui.controller.DataProviderToController;
import com.andreidodu.fromgtog.type.EngineType;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;

import java.lang.reflect.Array;

import static com.andreidodu.fromgtog.gui.GuiKeys.*;

@Getter
@Setter
public class ToGithubController implements DataProviderToController {

    private JTextField toGithubTokenTextField;
    private JComboBox toGithubPrivacyComboBox;
    final static int TAB_INDEX = EngineType.GITHUB.getValue();

    public ToGithubController(JTextField toGithubTokenTextField, JComboBox toGithubPrivacyComboBox) {
        this.toGithubTokenTextField = toGithubTokenTextField;
        this.toGithubPrivacyComboBox = toGithubPrivacyComboBox;

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(new String[]{"All private", "All public"});
        this.toGithubPrivacyComboBox.setModel(model);
    }

    @Override
    public boolean accept(int tabIndex) {
        return TAB_INDEX == tabIndex;
    }

    @Override
    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(TO_GITHUB_TOKEN, toGithubTokenTextField.getText());
        jsonObject.put(TO_GITHUB_PRIVACY_INDEX, toGithubPrivacyComboBox.getSelectedIndex());
        jsonObject.put(ENGINE_TYPE, EngineType.fromValue(TAB_INDEX));

        return jsonObject;
    }
}
