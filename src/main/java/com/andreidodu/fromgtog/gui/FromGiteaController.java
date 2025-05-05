package com.andreidodu.fromgtog.gui;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;

import static com.andreidodu.fromgtog.gui.GuiKeys.*;

@Getter
@Setter
public class FromGiteaController implements DataProviderFromController {

    private final static int TAB_INDEX = 2;

    private JTextField fromGiteaUrlTextField;
    private JTextField fromGiteaTokenTextField;
    private JCheckBox fromGiteaCloneStarredRepositoriesCheckBox;
    private JCheckBox fromGiteaCloneForkedRepositoriesCheckBox;
    private JCheckBox fromGiteaClonePrivateRepositoriesCheckBox;
    private JCheckBox fromGiteaCloneArchivedRepositoriesCheckBox;
    private JCheckBox fromGiteaCloneOrganizationsRepositoriesCheckBox;
    private JTextField fromGiteaExcludeOrganizationTextField;

    public FromGiteaController(JTextField fromGiteaUrlTextField, JTextField fromGiteaTokenTextField, JCheckBox fromGiteaCloneStarredRepositoriesCheckBox, JCheckBox fromGiteaCloneForkedRepositoriesCheckBox, JCheckBox fromGiteaClonePrivateRepositoriesCheckBox, JCheckBox fromGiteaCloneArchivedRepositoriesCheckBox, JCheckBox fromGiteaCloneOrganizationsRepositoriesCheckBox, JTextField fromGiteaExcludeOrganizationTextField) {
        this.fromGiteaUrlTextField = fromGiteaUrlTextField;
        this.fromGiteaTokenTextField = fromGiteaTokenTextField;
        this.fromGiteaCloneStarredRepositoriesCheckBox = fromGiteaCloneStarredRepositoriesCheckBox;
        this.fromGiteaCloneForkedRepositoriesCheckBox = fromGiteaCloneForkedRepositoriesCheckBox;
        this.fromGiteaClonePrivateRepositoriesCheckBox = fromGiteaClonePrivateRepositoriesCheckBox;
        this.fromGiteaCloneArchivedRepositoriesCheckBox = fromGiteaCloneArchivedRepositoriesCheckBox;
        this.fromGiteaCloneOrganizationsRepositoriesCheckBox = fromGiteaCloneOrganizationsRepositoriesCheckBox;
        this.fromGiteaExcludeOrganizationTextField = fromGiteaExcludeOrganizationTextField;
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
        jsonObject.put(FROM_GITEA_CLONE_ARCHIVED_REPO_FLAG, fromGiteaCloneArchivedRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITEA_CLONE_ORGANIZATIONS_REPO_FLAG, fromGiteaCloneOrganizationsRepositoriesCheckBox.isSelected());
        jsonObject.put(FROM_GITEA_EXCLUDE_ORGANIZATIONS, fromGiteaExcludeOrganizationTextField.getText());

        return jsonObject;
    }
}
