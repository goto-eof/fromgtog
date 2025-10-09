package com.andreidodu.fromgtog.gui.controller.impl;

import com.andreidodu.fromgtog.gui.controller.GUIFromController;
import com.andreidodu.fromgtog.type.EngineType;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

@Getter
@Setter
public class FromGiteaController implements GUIFromController {

    private final static int TAB_INDEX = EngineType.GITEA.getValue();

    private JTextField fromGiteaUrlTextField;
    private JTextField fromGiteaTokenTextField;
    private JCheckBox fromGiteaCloneStarredRepositoriesCheckBox;
    private JCheckBox fromGiteaCloneForkedRepositoriesCheckBox;
    private JCheckBox fromGiteaClonePrivateRepositoriesCheckBox;
    private JCheckBox fromGiteaClonePublicRepositoriesCheckBox;
    private JCheckBox fromGiteaCloneArchivedRepositoriesCheckBox;
    private JCheckBox fromGiteaCloneOrganizationsRepositoriesCheckBox;
    private JTextField fromGiteaExcludeOrganizationTextField;

    private JTabbedPane fromGiteaOptionsTabbedPane;
    private JTextField fromGiteaExcludeRepoNamesListTextField;
    private JTextField fromGiteaIncludeRepoNamesListFile;
    private JButton fromGiteaChooseRepoFileButton;

    public FromGiteaController(JSONObject settings, JTextField fromGiteaUrlTextField, JTextField fromGiteaTokenTextField, JCheckBox fromGiteaCloneStarredRepositoriesCheckBox, JCheckBox fromGiteaCloneForkedRepositoriesCheckBox, JCheckBox fromGiteaClonePrivateRepositoriesCheckBox, JCheckBox fromGiteaClonePublicRepositoriesCheckBox, JCheckBox fromGiteaCloneArchivedRepositoriesCheckBox, JCheckBox fromGiteaCloneOrganizationsRepositoriesCheckBox, JTextField fromGiteaExcludeOrganizationTextField,
                               JTabbedPane fromGiteaOptionsTabbedPane,
                               JTextField fromGiteaExcludeRepoNamesListTextField,
                               JTextField fromGiteaIncludeRepoNamesListFile,
                               JButton fromGiteaChooseRepoFileButton) {
        this.fromGiteaUrlTextField = fromGiteaUrlTextField;
        this.fromGiteaTokenTextField = fromGiteaTokenTextField;
        this.fromGiteaCloneStarredRepositoriesCheckBox = fromGiteaCloneStarredRepositoriesCheckBox;
        this.fromGiteaCloneForkedRepositoriesCheckBox = fromGiteaCloneForkedRepositoriesCheckBox;
        this.fromGiteaClonePrivateRepositoriesCheckBox = fromGiteaClonePrivateRepositoriesCheckBox;
        this.fromGiteaClonePublicRepositoriesCheckBox = fromGiteaClonePublicRepositoriesCheckBox;
        this.fromGiteaCloneArchivedRepositoriesCheckBox = fromGiteaCloneArchivedRepositoriesCheckBox;
        this.fromGiteaCloneOrganizationsRepositoriesCheckBox = fromGiteaCloneOrganizationsRepositoriesCheckBox;
        this.fromGiteaExcludeOrganizationTextField = fromGiteaExcludeOrganizationTextField;

        this.fromGiteaOptionsTabbedPane=fromGiteaOptionsTabbedPane;
        this.fromGiteaExcludeRepoNamesListTextField =  fromGiteaExcludeRepoNamesListTextField;
        this.fromGiteaIncludeRepoNamesListFile = fromGiteaIncludeRepoNamesListFile;
        this.fromGiteaChooseRepoFileButton = fromGiteaChooseRepoFileButton;

        applySettings(settings);
    }

    @Override
    public void applySettings(JSONObject settings) {
        fromGiteaUrlTextField.setText(settings.optString(FROM_GITEA_URL));
        fromGiteaTokenTextField.setText(settings.optString(FROM_GITEA_TOKEN));
        fromGiteaCloneStarredRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITEA_CLONE_STARRED_REPO_FLAG));
        fromGiteaCloneForkedRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITEA_CLONE_FORKED_REPO_FLAG));
        fromGiteaClonePrivateRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITEA_CLONE_PRIVATE_REPO_FLAG));
        fromGiteaClonePublicRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITEA_CLONE_PUBLIC_REPO_FLAG));
        fromGiteaCloneArchivedRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITEA_CLONE_ARCHIVED_REPO_FLAG));
        fromGiteaCloneOrganizationsRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITEA_CLONE_ORGANIZATIONS_REPO_FLAG));
        fromGiteaExcludeOrganizationTextField.setText(settings.optString(FROM_GITEA_EXCLUDE_ORGANIZATIONS));

    }

    @Override
    public boolean accept(int tabIndex) {
        return TAB_INDEX == tabIndex;
    }

    @Override
    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(FROM_GITEA_URL, fromGiteaUrlTextField.getText());
        jsonObject.put(FROM_GITEA_TOKEN, fromGiteaTokenTextField.getText());
        jsonObject.put(FROM_GITEA_CLONE_STARRED_REPO_FLAG, fromGiteaCloneStarredRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITEA_CLONE_FORKED_REPO_FLAG, fromGiteaCloneForkedRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITEA_CLONE_PRIVATE_REPO_FLAG, fromGiteaClonePrivateRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITEA_CLONE_PUBLIC_REPO_FLAG, fromGiteaClonePublicRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITEA_CLONE_ARCHIVED_REPO_FLAG, fromGiteaCloneArchivedRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITEA_CLONE_ORGANIZATIONS_REPO_FLAG, fromGiteaCloneOrganizationsRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITEA_EXCLUDE_ORGANIZATIONS, fromGiteaExcludeOrganizationTextField.getText());
        jsonObject.put(ENGINE_TYPE, EngineType.fromValue(TAB_INDEX));

        return jsonObject;
    }
}
