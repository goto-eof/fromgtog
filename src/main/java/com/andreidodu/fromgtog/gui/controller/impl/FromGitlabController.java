package com.andreidodu.fromgtog.gui.controller.impl;

import com.andreidodu.fromgtog.gui.controller.GUIFromController;
import com.andreidodu.fromgtog.gui.util.GuiUtil;
import com.andreidodu.fromgtog.type.EngineType;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

@Getter
@Setter
public class FromGitlabController implements GUIFromController {

    private final static int TAB_INDEX = EngineType.GITLAB.getValue();
    private JPanel fromGitlabPanel;
    private JTextField fromGitlabUrlTextfield;
    private JTextField fromGitlabUrlTokenTextfield;
    private JCheckBox fromGitlabCloneStarredRepositoriesCheckBox;
    private JCheckBox fromGitlabCloneForkedRepositoriesCheckBox;
    private JCheckBox fromGitlabClonePrivateRepositoriesCheckBox;
    private JCheckBox fromGitlabCloneArchivedRepositoriesCheckBox;
    private JCheckBox fromGitlabClonePublicRepositoriesCheckBox;
    private JCheckBox fromGitlabCloneOrganizationsRepositoriesCheckBox;
    private JTextField fromGitlabExcludeOrganizationTextField;

    private JTabbedPane fromGitlabOptionsTabbedPane;
    private JTextField fromGitlabExcludeRepoNamesListTextField;
    private JTextField fromGitlabIncludeRepoNamesListFile;
    private JButton fromGitlabChooseRepoFileButton;

    public FromGitlabController(JSONObject settings, JPanel fromGitlabPanel, JTextField fromGitlabUrlTextfield, JTextField fromGitlabUrlTokenTextfield, JCheckBox fromGitlabCloneStarredRepositoriesCheckBox, JCheckBox fromGitlabCloneForkedRepositoriesCheckBox, JCheckBox fromGitlabClonePrivateRepositoriesCheckBox, JCheckBox fromGitlabCloneArchivedRepositoriesCheckBox, JCheckBox fromGitlabClonePublicRepositoriesCheckBox, JCheckBox fromGitlabCloneOrganizationsRepositoriesCheckBox, JTextField fromGitlabExcludeOrganizationTextField,
                                JTabbedPane fromGitlabOptionsTabbedPane,
                                JTextField fromGitlabExcludeRepoNamesListTextField,
                                JTextField fromGitlabIncludeRepoNamesListFile,
                                JButton fromGitlabChooseRepoFileButton) {
        this.fromGitlabPanel = fromGitlabPanel;
        this.fromGitlabUrlTextfield = fromGitlabUrlTextfield;
        this.fromGitlabUrlTokenTextfield = fromGitlabUrlTokenTextfield;
        this.fromGitlabCloneStarredRepositoriesCheckBox = fromGitlabCloneStarredRepositoriesCheckBox;
        this.fromGitlabCloneForkedRepositoriesCheckBox = fromGitlabCloneForkedRepositoriesCheckBox;
        this.fromGitlabClonePrivateRepositoriesCheckBox = fromGitlabClonePrivateRepositoriesCheckBox;
        this.fromGitlabCloneArchivedRepositoriesCheckBox = fromGitlabCloneArchivedRepositoriesCheckBox;
        this.fromGitlabClonePublicRepositoriesCheckBox = fromGitlabClonePublicRepositoriesCheckBox;
        this.fromGitlabCloneOrganizationsRepositoriesCheckBox = fromGitlabCloneOrganizationsRepositoriesCheckBox;
        this.fromGitlabExcludeOrganizationTextField = fromGitlabExcludeOrganizationTextField;

        this.fromGitlabOptionsTabbedPane = fromGitlabOptionsTabbedPane;
        this.fromGitlabExcludeRepoNamesListTextField = fromGitlabExcludeRepoNamesListTextField;
        this.fromGitlabIncludeRepoNamesListFile = fromGitlabIncludeRepoNamesListFile;
        this.fromGitlabChooseRepoFileButton = fromGitlabChooseRepoFileButton;

        applySettings(settings);
        GuiUtil.addActionListenerToChooseReposListFileButton(fromGitlabChooseRepoFileButton, fromGitlabIncludeRepoNamesListFile);
    }


    @Override
    public void applySettings(JSONObject settings) {
        fromGitlabUrlTextfield.setText(settings.optString(FROM_GITLAB_URL));
        fromGitlabUrlTokenTextfield.setText(settings.optString(FROM_GITLAB_TOKEN));
        fromGitlabCloneStarredRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITLAB_CLONE_STARRED_REPO_FLAG));
        fromGitlabCloneForkedRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITLAB_CLONE_FORKED_REPO_FLAG));
        fromGitlabClonePrivateRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITLAB_CLONE_PRIVATE_REPO_FLAG));
        fromGitlabClonePublicRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITLAB_CLONE_PUBLIC_REPO_FLAG));
        fromGitlabCloneArchivedRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITLAB_CLONE_ARCHIVED_REPO_FLAG));
        fromGitlabCloneOrganizationsRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITLAB_CLONE_ORGANIZATIONS_REPO_FLAG));
        fromGitlabExcludeOrganizationTextField.setText(settings.optString(FROM_GITLAB_EXCLUDE_ORGANIZATIONS));
    }

    @Override
    public boolean accept(int tabIndex) {
        return TAB_INDEX == tabIndex;
    }

    @Override
    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(FROM_GITLAB_URL, fromGitlabUrlTextfield.getText());
        jsonObject.put(FROM_GITLAB_TOKEN, fromGitlabUrlTokenTextfield.getText());
        jsonObject.put(FROM_GITLAB_CLONE_STARRED_REPO_FLAG, fromGitlabCloneStarredRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITLAB_CLONE_FORKED_REPO_FLAG, fromGitlabCloneForkedRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITLAB_CLONE_PRIVATE_REPO_FLAG, fromGitlabClonePrivateRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITLAB_CLONE_PUBLIC_REPO_FLAG, fromGitlabClonePublicRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITLAB_CLONE_ARCHIVED_REPO_FLAG, fromGitlabCloneArchivedRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITLAB_CLONE_ORGANIZATIONS_REPO_FLAG, fromGitlabCloneOrganizationsRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITLAB_EXCLUDE_ORGANIZATIONS, fromGitlabExcludeOrganizationTextField.getText());
        jsonObject.put(ENGINE_TYPE, EngineType.fromValue(TAB_INDEX));

        return jsonObject;
    }
}
