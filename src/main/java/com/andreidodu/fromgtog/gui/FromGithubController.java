package com.andreidodu.fromgtog.gui;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;

import static com.andreidodu.fromgtog.gui.GuiKeys.*;

@Getter
@Setter
public class FromGithubController implements DataProviderFromController {

    private final static int TAB_INDEX = 1;

    private JTextField fromGithubTokenTextField;
    private JCheckBox fromGithubCloneStarredRepositoriesCheckBox;
    private JCheckBox fromGithubCloneForkedRepositoriesCheckBox;
    private JCheckBox fromGithubClonePrivateRepositoriesCheckBox;
    private JCheckBox fromGithubCloneArchivedRepositoriesCheckBox;
    private JCheckBox fromGithubCloneOrganizationSRepositoriesCheckBox;
    private JTextField fromGithubExcludeOrganizationTextField;

    public FromGithubController(JTextField fromGithubTokenTextField, JCheckBox fromGithubCloneStarredRepositoriesCheckBox, JCheckBox fromGithubCloneForkedRepositoriesCheckBox, JCheckBox fromGithubClonePrivateRepositoriesCheckBox, JCheckBox fromGithubCloneArchivedRepositoriesCheckBox, JCheckBox fromGithubCloneOrganizationSRepositoriesCheckBox, JTextField fromGithubExcludeOrganizationTextField) {
        this.fromGithubTokenTextField = fromGithubTokenTextField;
        this.fromGithubCloneStarredRepositoriesCheckBox = fromGithubCloneStarredRepositoriesCheckBox;
        this.fromGithubCloneForkedRepositoriesCheckBox = fromGithubCloneForkedRepositoriesCheckBox;
        this.fromGithubClonePrivateRepositoriesCheckBox = fromGithubClonePrivateRepositoriesCheckBox;
        this.fromGithubCloneArchivedRepositoriesCheckBox = fromGithubCloneArchivedRepositoriesCheckBox;
        this.fromGithubCloneOrganizationSRepositoriesCheckBox = fromGithubCloneOrganizationSRepositoriesCheckBox;
        this.fromGithubExcludeOrganizationTextField = fromGithubExcludeOrganizationTextField;
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
        jsonObject.put(FROM_GITHUB_CLONE_ARCHIVED_REPO_FLAG, fromGithubCloneArchivedRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITHUB_CLONE_ORGANIZATIONS_REPO_FLAG, fromGithubCloneOrganizationSRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITHUB_EXCLUDE_ORGANIZATIONS, fromGithubExcludeOrganizationTextField.getText());

        return jsonObject;
    }
}
