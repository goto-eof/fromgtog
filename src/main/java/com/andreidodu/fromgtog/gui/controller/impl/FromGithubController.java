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
public class FromGithubController implements GUIFromController {

    private final static int TAB_INDEX = EngineType.GITHUB.getValue();

    private JTextField fromGithubTokenTextField;
    private JCheckBox fromGithubCloneStarredRepositoriesCheckBox;
    private JCheckBox fromGithubCloneForkedRepositoriesCheckBox;
    private JCheckBox fromGithubClonePrivateRepositoriesCheckBox;
    private JCheckBox fromGithubClonePublicRepositoriesCheckBox;
    private JCheckBox fromGithubCloneArchivedRepositoriesCheckBox;
    private JCheckBox fromGithubCloneOrganizationSRepositoriesCheckBox;
    private JTextField fromGithubExcludeOrganizationTextField;

    private JTabbedPane fromGithubOptionsTabbedPane;
    private JTextField fromGithubIncludeRepoNamesListFile;
    private JTextField fromGithubExcludeRepoNamesListTextField;
    private JButton FromGithubChooseRepoFileButton;

    public FromGithubController(JSONObject settings, JTextField fromGithubTokenTextField, JCheckBox fromGithubCloneStarredRepositoriesCheckBox, JCheckBox fromGithubCloneForkedRepositoriesCheckBox, JCheckBox fromGithubClonePrivateRepositoriesCheckBox, JCheckBox fromGithubClonePublicRepositoriesCheckBox, JCheckBox fromGithubCloneArchivedRepositoriesCheckBox, JCheckBox fromGithubCloneOrganizationSRepositoriesCheckBox, JTextField fromGithubExcludeOrganizationTextField,
                                JTabbedPane fromGithubOptionsTabbedPane,
                                JTextField fromGithubExcludeRepoNamesListTextField,
                                JTextField fromGithubIncludeRepoNamesListFile,
                                JButton fromGithubChooseRepoFileButton
    ) {
        this.fromGithubTokenTextField = fromGithubTokenTextField;
        this.fromGithubCloneStarredRepositoriesCheckBox = fromGithubCloneStarredRepositoriesCheckBox;
        this.fromGithubCloneForkedRepositoriesCheckBox = fromGithubCloneForkedRepositoriesCheckBox;
        this.fromGithubClonePrivateRepositoriesCheckBox = fromGithubClonePrivateRepositoriesCheckBox;
        this.fromGithubClonePublicRepositoriesCheckBox = fromGithubClonePublicRepositoriesCheckBox;
        this.fromGithubCloneArchivedRepositoriesCheckBox = fromGithubCloneArchivedRepositoriesCheckBox;
        this.fromGithubCloneOrganizationSRepositoriesCheckBox = fromGithubCloneOrganizationSRepositoriesCheckBox;

        this.fromGithubOptionsTabbedPane = fromGithubOptionsTabbedPane;
        this.fromGithubExcludeOrganizationTextField = fromGithubExcludeRepoNamesListTextField;
        this.fromGithubIncludeRepoNamesListFile = fromGithubIncludeRepoNamesListFile;
        this.FromGithubChooseRepoFileButton = fromGithubChooseRepoFileButton;

        applySettings(settings);
    }

    @Override
    public void applySettings(JSONObject settings) {
        fromGithubTokenTextField.setText(settings.optString(FROM_GITHUB_TOKEN));
        fromGithubCloneStarredRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITHUB_CLONE_STARRED_REPO_FLAG));
        fromGithubCloneForkedRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITHUB_CLONE_FORKED_REPO_FLAG));
        fromGithubClonePrivateRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITHUB_CLONE_PRIVATE_REPO_FLAG));
        fromGithubClonePublicRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITHUB_CLONE_PUBLIC_REPO_FLAG));
        fromGithubCloneArchivedRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITHUB_CLONE_ARCHIVED_REPO_FLAG));
        fromGithubCloneOrganizationSRepositoriesCheckBox.setSelected(settings.optBooleanObject(FROM_GITHUB_CLONE_ORGANIZATIONS_REPO_FLAG));
        fromGithubExcludeOrganizationTextField.setText(settings.optString(FROM_GITHUB_EXCLUDE_ORGANIZATIONS));
    }

    @Override
    public boolean accept(int tabIndex) {
        return TAB_INDEX == tabIndex;
    }

    @Override
    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(FROM_GITHUB_TOKEN, fromGithubTokenTextField.getText());
        jsonObject.put(FROM_GITHUB_CLONE_STARRED_REPO_FLAG, fromGithubCloneStarredRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITHUB_CLONE_FORKED_REPO_FLAG, fromGithubCloneForkedRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITHUB_CLONE_PRIVATE_REPO_FLAG, fromGithubClonePrivateRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITHUB_CLONE_PUBLIC_REPO_FLAG, fromGithubClonePublicRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITHUB_CLONE_ARCHIVED_REPO_FLAG, fromGithubCloneArchivedRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITHUB_CLONE_ORGANIZATIONS_REPO_FLAG, fromGithubCloneOrganizationSRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITHUB_EXCLUDE_ORGANIZATIONS, fromGithubExcludeOrganizationTextField.getText());
        jsonObject.put(ENGINE_TYPE, EngineType.fromValue(TAB_INDEX));

        return jsonObject;
    }
}
