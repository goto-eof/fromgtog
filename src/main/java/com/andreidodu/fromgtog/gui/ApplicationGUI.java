package com.andreidodu.fromgtog.gui;

import com.andreidodu.fromgtog.gui.controller.GUIFromController;
import com.andreidodu.fromgtog.gui.controller.GUIToController;
import com.andreidodu.fromgtog.gui.controller.impl.*;
import com.andreidodu.fromgtog.gui.helper.TrayIconHelper;
import com.andreidodu.fromgtog.gui.helper.systemtray.SystemTrayCoordinatorImpl;
import com.andreidodu.fromgtog.service.impl.SettingsServiceImpl;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class ApplicationGUI extends JFrame {

    List<? extends JComponent> allComponentsList;
    private JTabbedPane fromTabbedPane;
    private JTabbedPane toTabbedPane;
    private JPanel mainPanel;
    private JPanel fromGithubPanel;
    private JPanel fromGiteaPanel;
    private JPanel fromLocalPanel;
    private JTextArea appLogTextArea;
    private JTextField appSleepTimeTextField;
    private JButton appSaveConfigurationButton;
    private JProgressBar appProgressBar;
    private JLabel appProgressStatusLabel;
    private JLabel appProgressMessageStatusLabel;
    private JButton appStartButton;
    private JPasswordField fromGithubTokenTextField;
    private JCheckBox fromGithubCloneStarredRepositoriesCheckBox;
    private JCheckBox fromGithubCloneForkedRepositoriesCheckBox;
    private JCheckBox fromGithubClonePrivateRepositoriesCheckBox;
    private JCheckBox fromGithubCloneArchivedRepositoriesCheckBox;
    private JCheckBox fromGithubCloneOrganizationSRepositoriesCheckBox;
    private JTextField fromGithubExcludeOrganizationTextField;
    private JTextField fromGiteaUrlTextField;
    private JPasswordField fromGiteaTokenTextField;
    private JCheckBox fromGiteaCloneStarredRepositoriesCheckBox;
    private JCheckBox fromGiteaCloneForkedRepositoriesCheckBox;
    private JCheckBox fromGiteaClonePrivateRepositoriesCheckBox;
    private JCheckBox fromGiteaCloneArchivedRepositoriesCheckBox;
    private JCheckBox fromGiteaCloneOrganizationsRepositoriesCheckBox;
    private JTextField fromGiteaExcludeOrganizationTextField;
    private JTextField fromLocalRootPathTextField;
    private JTextField toLocalRootPathTextField;
    private JCheckBox toLocalGroupByRepositoryOwnerCheckBox;
    private JTextField toGiteaUrlTextField;
    private JPasswordField toGiteaTokenTextField;
    private JComboBox toGiteaPrivacyComboBox;
    private JPasswordField toGithubTokenTextField;
    private JComboBox toGithubPrivacyComboBox;
    private JCheckBox fromGithubClonePublicRepositoriesCheckBox;
    private JButton toLocalChooseButton;
    private JCheckBox fromGiteaClonePublicRepositoriesCheckBox;
    private JButton fromLocalChooseButton;
    private JButton appOpenLogFileButton;
    private JButton appStopButton;
    private JButton toolsDeleteALLGitHubRepositoriesButton;
    private JButton toolsDeleteALLGiteaRepositoriesButton;
    private JButton toolsDeleteALLGitlabRepositoriesButton;
    private JPanel fromGitlabPanel;
    private JTextField fromGitlabUrlTextfield;
    private JPasswordField fromGitlabUrlTokenTextfield;
    private JCheckBox fromGitlabCloneStarredRepositoriesCheckBox;
    private JCheckBox fromGitlabCloneForkedRepositoriesCheckBox;
    private JCheckBox fromGitlabClonePrivateRepositoriesCheckBox;
    private JCheckBox fromGitlabCloneArchivedRepositoriesCheckBox;
    private JCheckBox fromGitlabClonePublicRepositoriesCheckBox;
    private JCheckBox fromGitlabCloneOrganizationsRepositoriesCheckBox;
    private JTextField fromGitlabExcludeOrganizationTextField;
    private JPanel toGitlabPanel;
    private JTextField toGitlabUrlTextField;
    private JPasswordField toGitlabTokenTextField;
    private JComboBox toGitlabPrivacyComboBox;
    private JTabbedPane mainTabbedPane;
    private JButton buyMeACoffeeButton;
    private JButton projectWebsiteButton;
    private JButton reportAnIssueButton;
    private JPanel statusContainerJPanel;
    private JCheckBox multithreadingEnabled;
    private JButton clearLogFileButton;
    private JLabel timeLable;
    private JButton contactMe;
    private JPanel githubFilterAndClone;
    private JTabbedPane fromGithubOptionsTabbedPane;
    private JTextField fromGithubExcludeRepoNamesListTextField;
    private JTextField fromGithubIncludeRepoNamesListFile;
    private JButton fromGithubChooseRepoFileButton;
    private JTabbedPane fromGiteaOptionsTabbedPane;
    private JTextField fromGiteaExcludeRepoNamesListTextField;
    private JTextField fromGiteaIncludeRepoNamesListFile;
    private JButton fromGiteaChooseRepoFileButton;
    private JTabbedPane fromGitlabOptionsTabbedPane;
    private JTextField fromGitlabExcludeRepoNamesListTextField;
    private JTextField fromGitlabIncludeRepoNamesListFile;
    private JButton fromGitlabChooseRepoFileButton;

    private JCheckBox chronJobCheckBox;
    private JTextField chronExpressionTextField;

    private JCheckBox toGithubOverrideIfExistsCheckBox;
    private JCheckBox toGiteaOverrideIfExistsCheckBox;
    private JCheckBox toGitlabOverrideIfExistsCheckBox;
    private JCheckBox toLocalOverrideIfExistsCheckBox;

    private TrayIconHelper trayIconHelper;

    public ApplicationGUI() {
        setTitle("FromGtoG 9.1.2");
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);


        JSONObject settings = SettingsServiceImpl.getInstance().load();

        List<GUIFromController> fromControllerList = List.of(
                buildFromGithubController(settings),
                buildFromGiteaController(settings),
                buildFromLocalController(settings),
                buildFromGitlabController(settings)
        );

        List<GUIToController> toControllerList = List.of(
                buildToGithubController(settings),
                buildToGiteaController(settings),
                buildToLocalController(settings),
                buildToGitlabController(settings)
        );

        this.allComponentsList = buildListOfComponents();
        buildToolsController();
        buildAboutController();

        AppController appController = buildAppController(settings, fromControllerList, toControllerList);

        disableGroupByRepositoryOwnerIfNecessary();
        addFromTabbedPaneListener();

        SwingUtilities.invokeLater(() -> {
            if (!new SystemTrayCoordinatorImpl().getSystemTrayStrategy().isSupported()) {
                JOptionPane.showMessageDialog(null, "System tray not supported on this platform.");
                System.exit(1);
                return;
            }
            trayIconHelper = new TrayIconHelper(this, !chronJobCheckBox.isSelected());
        });
    }

    public void toggleTrayIcon(boolean loadDefault) {
        SwingUtilities.invokeLater(() -> {
            this.trayIconHelper.toggleIcon(loadDefault);
        });
    }

    private GUIFromController buildFromGitlabController(JSONObject settings) {
        return new FromGitlabController(
                settings,
                fromGitlabPanel,
                fromGitlabUrlTextfield,
                fromGitlabUrlTokenTextfield,
                fromGitlabCloneStarredRepositoriesCheckBox,
                fromGitlabCloneForkedRepositoriesCheckBox,
                fromGitlabClonePrivateRepositoriesCheckBox,
                fromGitlabCloneArchivedRepositoriesCheckBox,
                fromGitlabClonePublicRepositoriesCheckBox,
                fromGitlabCloneOrganizationsRepositoriesCheckBox,
                fromGitlabExcludeOrganizationTextField,
                fromGitlabOptionsTabbedPane,
                fromGitlabExcludeRepoNamesListTextField,
                fromGitlabIncludeRepoNamesListFile,
                fromGitlabChooseRepoFileButton
        );
    }

    private void addFromTabbedPaneListener() {
        fromTabbedPane.addChangeListener(e -> disableGroupByRepositoryOwnerIfNecessary());
    }

    private void disableGroupByRepositoryOwnerIfNecessary() {
        if (fromTabbedPane.getSelectedIndex() == 2) {
            toLocalGroupByRepositoryOwnerCheckBox.setVisible(false);
            return;
        }
        toLocalGroupByRepositoryOwnerCheckBox.setVisible(true);
    }

    private void setEnabledAllComponents(boolean enabled) {
        SwingUtilities.invokeLater(() -> this.allComponentsList.forEach(component -> component.setEnabled(enabled)));
    }

    private ToolsController buildToolsController() {
        return new ToolsController(
                toolsDeleteALLGitHubRepositoriesButton,
                toolsDeleteALLGiteaRepositoriesButton,
                toolsDeleteALLGitlabRepositoriesButton,
                this::setEnabledAllComponents
        );
    }

    private AboutController buildAboutController() {
        return new AboutController(
                projectWebsiteButton,
                reportAnIssueButton,
                buyMeACoffeeButton,
                contactMe
        );
    }

    private AppController buildAppController(JSONObject settings, List<GUIFromController> fromControllerList, List<GUIToController> toControllerList) {
        return new AppController(
                settings,
                fromControllerList,
                toControllerList,
                appLogTextArea,
                appSleepTimeTextField,
                appSaveConfigurationButton,
                appProgressBar,
                appProgressMessageStatusLabel,
                appProgressStatusLabel,
                appStartButton,
                fromTabbedPane,
                toTabbedPane,
                appOpenLogFileButton,
                this::setEnabledAllComponents,
                appStopButton,
                statusContainerJPanel,
                multithreadingEnabled,
                clearLogFileButton,
                timeLable,
                chronJobCheckBox,
                chronExpressionTextField,
                this::toggleTrayIcon
        );
    }

    private ToGithubController buildToGithubController(JSONObject settings) {
        return new ToGithubController(
                settings,
                toGithubTokenTextField,
                toGithubPrivacyComboBox,
                toGithubOverrideIfExistsCheckBox
        );
    }

    private ToGiteaController buildToGiteaController(JSONObject settings) {
        return new ToGiteaController(
                settings,
                toGiteaUrlTextField,
                toGiteaTokenTextField,
                toGiteaPrivacyComboBox,
                toGiteaOverrideIfExistsCheckBox
        );
    }

    private ToLocalController buildToLocalController(JSONObject settings) {
        return new ToLocalController(
                settings,
                toLocalRootPathTextField,
                toLocalGroupByRepositoryOwnerCheckBox,
                toLocalChooseButton,
                toLocalOverrideIfExistsCheckBox
        );
    }

    private GUIToController buildToGitlabController(JSONObject settings) {
        return new ToGitlabController(
                settings,
                toGitlabUrlTextField,
                toGitlabTokenTextField,
                toGitlabPrivacyComboBox,
                toGitlabOverrideIfExistsCheckBox
        );
    }

    private FromLocalController buildFromLocalController(JSONObject settings) {
        return new FromLocalController(
                settings,
                fromLocalRootPathTextField,
                fromLocalChooseButton
        );
    }

    private FromGiteaController buildFromGiteaController(JSONObject settings) {
        return new FromGiteaController(
                settings,
                fromGiteaUrlTextField,
                fromGiteaTokenTextField,
                fromGiteaCloneStarredRepositoriesCheckBox,
                fromGiteaCloneForkedRepositoriesCheckBox,
                fromGiteaClonePrivateRepositoriesCheckBox,
                fromGiteaClonePublicRepositoriesCheckBox,
                fromGiteaCloneArchivedRepositoriesCheckBox,
                fromGiteaCloneOrganizationsRepositoriesCheckBox,
                fromGiteaExcludeOrganizationTextField,
                fromGiteaOptionsTabbedPane,
                fromGiteaExcludeRepoNamesListTextField,
                fromGiteaIncludeRepoNamesListFile,
                fromGiteaChooseRepoFileButton
        );
    }

    private FromGithubController buildFromGithubController(JSONObject settings) {
        return new FromGithubController(
                settings,
                fromGithubTokenTextField,
                fromGithubCloneStarredRepositoriesCheckBox,
                fromGithubCloneForkedRepositoriesCheckBox,
                fromGithubClonePrivateRepositoriesCheckBox,
                fromGithubClonePublicRepositoriesCheckBox,
                fromGithubCloneArchivedRepositoriesCheckBox,
                fromGithubCloneOrganizationSRepositoriesCheckBox,
                fromGithubExcludeOrganizationTextField,
                fromGithubOptionsTabbedPane,
                fromGithubExcludeRepoNamesListTextField,
                fromGithubIncludeRepoNamesListFile,
                fromGithubChooseRepoFileButton
        );
    }

    private List<? extends JComponent> buildListOfComponents() {
        return List.of(
                fromTabbedPane,
                toTabbedPane,
                mainPanel,

                fromGithubPanel,
                fromGiteaPanel,
                fromLocalPanel,

                appLogTextArea,
                appSleepTimeTextField,
                appSaveConfigurationButton,
                appProgressBar,
                appProgressStatusLabel,
                appProgressMessageStatusLabel,
                appStartButton,

                fromGithubTokenTextField,
                fromGithubCloneStarredRepositoriesCheckBox,
                fromGithubCloneForkedRepositoriesCheckBox,
                fromGithubClonePrivateRepositoriesCheckBox,
                fromGithubCloneArchivedRepositoriesCheckBox,
                fromGithubCloneOrganizationSRepositoriesCheckBox,
                fromGithubExcludeOrganizationTextField,

                fromGiteaUrlTextField,
                fromGiteaTokenTextField,
                fromGiteaCloneStarredRepositoriesCheckBox,
                fromGiteaCloneForkedRepositoriesCheckBox,
                fromGiteaClonePrivateRepositoriesCheckBox,
                fromGiteaCloneArchivedRepositoriesCheckBox,
                fromGiteaCloneOrganizationsRepositoriesCheckBox,
                fromGiteaExcludeOrganizationTextField,

                fromLocalRootPathTextField,

                toLocalRootPathTextField,
                toLocalGroupByRepositoryOwnerCheckBox,

                toGiteaUrlTextField,
                toGiteaTokenTextField,
                toGiteaPrivacyComboBox,

                toGithubTokenTextField,
                toGithubPrivacyComboBox,
                fromGithubClonePublicRepositoriesCheckBox,
                toLocalChooseButton,
                fromGiteaClonePublicRepositoriesCheckBox,
                fromLocalChooseButton,

                toolsDeleteALLGitHubRepositoriesButton,
                toolsDeleteALLGiteaRepositoriesButton,
                toolsDeleteALLGitlabRepositoriesButton,

                fromGitlabPanel,
                toGitlabPanel,

                fromGitlabUrlTextfield,
                fromGitlabUrlTokenTextfield,
                fromGitlabCloneStarredRepositoriesCheckBox,
                fromGitlabCloneForkedRepositoriesCheckBox,
                fromGitlabClonePrivateRepositoriesCheckBox,
                fromGitlabCloneArchivedRepositoriesCheckBox,
                fromGitlabClonePublicRepositoriesCheckBox,
                fromGitlabCloneOrganizationsRepositoriesCheckBox,
                fromGitlabExcludeOrganizationTextField,
                toGitlabUrlTextField,
                toGitlabTokenTextField,
                toGitlabPrivacyComboBox,
                mainTabbedPane,
                buyMeACoffeeButton,
                projectWebsiteButton,
                reportAnIssueButton,
                multithreadingEnabled,

                githubFilterAndClone,
                fromGithubOptionsTabbedPane,
                fromGithubExcludeRepoNamesListTextField,
                fromGithubIncludeRepoNamesListFile,
                fromGithubChooseRepoFileButton,
                fromGiteaOptionsTabbedPane,
                fromGiteaExcludeRepoNamesListTextField,
                fromGiteaIncludeRepoNamesListFile,
                fromGiteaChooseRepoFileButton,
                fromGitlabOptionsTabbedPane,
                fromGitlabExcludeRepoNamesListTextField,
                fromGitlabIncludeRepoNamesListFile,
                fromGitlabChooseRepoFileButton,

                chronJobCheckBox,
                chronExpressionTextField,
                toGithubOverrideIfExistsCheckBox,
                toGiteaOverrideIfExistsCheckBox,
                toGitlabOverrideIfExistsCheckBox,
                toLocalOverrideIfExistsCheckBox
        );
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /** Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainTabbedPane = new JTabbedPane();
        mainPanel.add(mainTabbedPane, new GridConstraints(0, 0, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainTabbedPane.addTab("FromGtoG", new ImageIcon(getClass().getResource("/images/sm/copy.png")), panel1);
        mainTabbedPane.setDisabledIconAt(0, new ImageIcon(getClass().getResource("/images/sm/copy-disabled.png")));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 1, new Insets(20, 0, 0, 0), -1, -1));
        scrollPane1.setViewportView(panel2);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fromTabbedPane = new JTabbedPane();
        fromTabbedPane.setTabPlacement(2);
        panel3.add(fromTabbedPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        fromGithubPanel = new JPanel();
        fromGithubPanel.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 10, 20), -1, -1));
        fromTabbedPane.addTab("From GitHub", new ImageIcon(getClass().getResource("/images/sm/github.png")), fromGithubPanel);
        fromTabbedPane.setDisabledIconAt(0, new ImageIcon(getClass().getResource("/images/sm/github-disabled.png")));
        fromGithubPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "From GitHub", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, 20, fromGithubPanel.getFont()), null));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        fromGithubPanel.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 20, 20), -1, -1));
        panel4.add(panel5, new GridConstraints(0, 0, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/images/xl/github.png")));
        label1.setText("");
        panel5.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel4.add(spacer1, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel6, new GridConstraints(0, 1, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("GitHub Token");
        panel7.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGithubTokenTextField = new JPasswordField();
        panel7.add(fromGithubTokenTextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fromGithubOptionsTabbedPane = new JTabbedPane();
        fromGithubOptionsTabbedPane.setTabPlacement(4);
        panel6.add(fromGithubOptionsTabbedPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        githubFilterAndClone = new JPanel();
        githubFilterAndClone.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        fromGithubOptionsTabbedPane.addTab("Filter", new ImageIcon(getClass().getResource("/images/sm/filter.png")), githubFilterAndClone, "Filter and Clone");
        fromGithubOptionsTabbedPane.setDisabledIconAt(0, new ImageIcon(getClass().getResource("/images/sm/filter-gray.png")));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(7, 2, new Insets(0, 0, 0, 0), -1, -1));
        githubFilterAndClone.add(panel8, new GridConstraints(0, 0, 5, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(3, 2, new Insets(10, 10, 10, 10), -1, -1));
        panel8.add(panel9, new GridConstraints(3, 0, 3, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel9.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Exclude", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label3 = new JLabel();
        label3.setText("Exclude organizations");
        panel9.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGithubExcludeOrganizationTextField = new JTextField();
        panel9.add(fromGithubExcludeOrganizationTextField, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Exclude repos");
        panel9.add(label4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGithubExcludeRepoNamesListTextField = new JTextField();
        panel9.add(fromGithubExcludeRepoNamesListTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("(usernames separated by comma)");
        panel9.add(label5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("(repo names separated by comma)");
        panel9.add(label6, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panel10, new GridConstraints(0, 0, 3, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel10.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Filter", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, null));
        fromGithubCloneStarredRepositoriesCheckBox = new JCheckBox();
        fromGithubCloneStarredRepositoriesCheckBox.setText("clone starred repositories");
        panel10.add(fromGithubCloneStarredRepositoriesCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGithubCloneForkedRepositoriesCheckBox = new JCheckBox();
        fromGithubCloneForkedRepositoriesCheckBox.setText("clone forked repositories");
        panel10.add(fromGithubCloneForkedRepositoriesCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGithubClonePrivateRepositoriesCheckBox = new JCheckBox();
        fromGithubClonePrivateRepositoriesCheckBox.setText("clone private repositories");
        panel10.add(fromGithubClonePrivateRepositoriesCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGithubCloneArchivedRepositoriesCheckBox = new JCheckBox();
        fromGithubCloneArchivedRepositoriesCheckBox.setText("clone archived repositories");
        panel10.add(fromGithubCloneArchivedRepositoriesCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGithubClonePublicRepositoriesCheckBox = new JCheckBox();
        fromGithubClonePublicRepositoriesCheckBox.setText("clone public repositories");
        panel10.add(fromGithubClonePublicRepositoriesCheckBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGithubCloneOrganizationSRepositoriesCheckBox = new JCheckBox();
        fromGithubCloneOrganizationSRepositoriesCheckBox.setText("clone organization's repositories");
        panel10.add(fromGithubCloneOrganizationSRepositoriesCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel8.add(spacer2, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(2, 1, new Insets(5, 5, 5, 5), -1, -1));
        fromGithubOptionsTabbedPane.addTab("File", new ImageIcon(getClass().getResource("/images/sm/list.png")), panel11, "Custom List");
        fromGithubOptionsTabbedPane.setDisabledIconAt(1, new ImageIcon(getClass().getResource("/images/sm/list-gray.png")));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel12, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("File containing the repo list names to clone");
        panel12.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel12.add(panel13, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fromGithubChooseRepoFileButton = new JButton();
        fromGithubChooseRepoFileButton.setText("Choose file");
        panel13.add(fromGithubChooseRepoFileButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel13.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        fromGithubIncludeRepoNamesListFile = new JTextField();
        panel12.add(fromGithubIncludeRepoNamesListFile, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("(file line pattern: repo-name + new line)");
        panel12.add(label8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel11.add(spacer4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        fromGiteaPanel = new JPanel();
        fromGiteaPanel.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 10, 20), -1, -1));
        fromTabbedPane.addTab("From Gitea", new ImageIcon(getClass().getResource("/images/sm/gitea.png")), fromGiteaPanel);
        fromTabbedPane.setDisabledIconAt(1, new ImageIcon(getClass().getResource("/images/sm/gitea-disabled.png")));
        fromGiteaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "From Gitea", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, 20, fromGiteaPanel.getFont()), null));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        fromGiteaPanel.add(panel14, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 20, 20), -1, -1));
        panel14.add(panel15, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel15.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label9 = new JLabel();
        label9.setIcon(new ImageIcon(getClass().getResource("/images/xl/gitea.png")));
        label9.setText("");
        panel15.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel14.add(spacer5, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel14.add(panel16, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel16.add(panel17, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel17.add(panel18, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel18.add(panel19, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fromGiteaUrlTextField = new JTextField();
        panel19.add(fromGiteaUrlTextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Gitea URL");
        panel19.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel18.add(panel20, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Gitea Token");
        panel20.add(label11, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGiteaTokenTextField = new JPasswordField();
        panel20.add(fromGiteaTokenTextField, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fromGiteaOptionsTabbedPane = new JTabbedPane();
        fromGiteaOptionsTabbedPane.setTabPlacement(4);
        panel17.add(fromGiteaOptionsTabbedPane, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        fromGiteaOptionsTabbedPane.addTab("Filter", new ImageIcon(getClass().getResource("/images/sm/filter.png")), panel21, "Filter & Clone");
        fromGiteaOptionsTabbedPane.setDisabledIconAt(0, new ImageIcon(getClass().getResource("/images/sm/filter-gray.png")));
        final JPanel panel22 = new JPanel();
        panel22.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel21.add(panel22, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel23 = new JPanel();
        panel23.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel22.add(panel23, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel24 = new JPanel();
        panel24.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel23.add(panel24, new GridConstraints(3, 0, 3, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel24.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Exclude", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label12 = new JLabel();
        label12.setText("Exclude organizations");
        panel24.add(label12, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGiteaExcludeOrganizationTextField = new JTextField();
        panel24.add(fromGiteaExcludeOrganizationTextField, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("(usernames separated by comma)");
        panel24.add(label13, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("Exclude repos");
        panel24.add(label14, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("(repos name separated by comma)");
        panel24.add(label15, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGiteaExcludeRepoNamesListTextField = new JTextField();
        panel24.add(fromGiteaExcludeRepoNamesListTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel25 = new JPanel();
        panel25.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel23.add(panel25, new GridConstraints(0, 0, 3, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel25.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Filter", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, null));
        fromGiteaCloneStarredRepositoriesCheckBox = new JCheckBox();
        fromGiteaCloneStarredRepositoriesCheckBox.setText("clone starred repositories");
        panel25.add(fromGiteaCloneStarredRepositoriesCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGiteaCloneForkedRepositoriesCheckBox = new JCheckBox();
        fromGiteaCloneForkedRepositoriesCheckBox.setText("clone forked repositories");
        panel25.add(fromGiteaCloneForkedRepositoriesCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGiteaClonePrivateRepositoriesCheckBox = new JCheckBox();
        fromGiteaClonePrivateRepositoriesCheckBox.setText("clone private repositories");
        panel25.add(fromGiteaClonePrivateRepositoriesCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGiteaCloneArchivedRepositoriesCheckBox = new JCheckBox();
        fromGiteaCloneArchivedRepositoriesCheckBox.setText("clone archived repositories");
        panel25.add(fromGiteaCloneArchivedRepositoriesCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGiteaClonePublicRepositoriesCheckBox = new JCheckBox();
        fromGiteaClonePublicRepositoriesCheckBox.setText("clone public repositories");
        panel25.add(fromGiteaClonePublicRepositoriesCheckBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGiteaCloneOrganizationsRepositoriesCheckBox = new JCheckBox();
        fromGiteaCloneOrganizationsRepositoriesCheckBox.setText("clone organization's repositories");
        panel25.add(fromGiteaCloneOrganizationsRepositoriesCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel22.add(spacer6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel26 = new JPanel();
        panel26.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        fromGiteaOptionsTabbedPane.addTab("File", new ImageIcon(getClass().getResource("/images/sm/list.png")), panel26, "Custom List");
        fromGiteaOptionsTabbedPane.setDisabledIconAt(1, new ImageIcon(getClass().getResource("/images/sm/list-gray.png")));
        final JPanel panel27 = new JPanel();
        panel27.setLayout(new GridLayoutManager(5, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel26.add(panel27, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label16 = new JLabel();
        label16.setText("File containing the repo list names to clone");
        panel27.add(label16, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGiteaIncludeRepoNamesListFile = new JTextField();
        panel27.add(fromGiteaIncludeRepoNamesListFile, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fromGiteaChooseRepoFileButton = new JButton();
        fromGiteaChooseRepoFileButton.setText("Choose file");
        panel27.add(fromGiteaChooseRepoFileButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel27.add(spacer7, new GridConstraints(3, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel27.add(spacer8, new GridConstraints(3, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        panel27.add(spacer9, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label17 = new JLabel();
        label17.setText("(file line pattern: repo-name + new line)");
        panel27.add(label17, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGitlabPanel = new JPanel();
        fromGitlabPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        fromTabbedPane.addTab("From Gitlab", new ImageIcon(getClass().getResource("/images/sm/gitlab.png")), fromGitlabPanel);
        fromGitlabPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "From Gitlab", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, 20, fromGitlabPanel.getFont()), new Color(-2104859)));
        final JPanel panel28 = new JPanel();
        panel28.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        fromGitlabPanel.add(panel28, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel29 = new JPanel();
        panel29.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 20, 20), -1, -1));
        panel28.add(panel29, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel29.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label18 = new JLabel();
        label18.setIcon(new ImageIcon(getClass().getResource("/images/xl/gitlab.png")));
        label18.setText("");
        panel29.add(label18, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        panel28.add(spacer10, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel30 = new JPanel();
        panel30.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 20, 20), -1, -1));
        panel28.add(panel30, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel31 = new JPanel();
        panel31.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel30.add(panel31, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel32 = new JPanel();
        panel32.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel31.add(panel32, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel33 = new JPanel();
        panel33.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel32.add(panel33, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fromGitlabUrlTextfield = new JTextField();
        panel33.add(fromGitlabUrlTextfield, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label19 = new JLabel();
        label19.setText("Gitlab URL");
        panel33.add(label19, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel34 = new JPanel();
        panel34.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel32.add(panel34, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label20 = new JLabel();
        label20.setText("Gitlab Token");
        panel34.add(label20, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGitlabUrlTokenTextfield = new JPasswordField();
        panel34.add(fromGitlabUrlTokenTextfield, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer11 = new Spacer();
        panel31.add(spacer11, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        fromGitlabOptionsTabbedPane = new JTabbedPane();
        fromGitlabOptionsTabbedPane.setTabPlacement(4);
        panel31.add(fromGitlabOptionsTabbedPane, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel35 = new JPanel();
        panel35.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        fromGitlabOptionsTabbedPane.addTab("Filter", new ImageIcon(getClass().getResource("/images/sm/filter.png")), panel35, "Filter & Clone");
        fromGitlabOptionsTabbedPane.setDisabledIconAt(0, new ImageIcon(getClass().getResource("/images/sm/filter-gray.png")));
        final JPanel panel36 = new JPanel();
        panel36.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel35.add(panel36, new GridConstraints(0, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel36.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-15461356)), "Filter", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, null));
        fromGitlabCloneStarredRepositoriesCheckBox = new JCheckBox();
        fromGitlabCloneStarredRepositoriesCheckBox.setText("clone starred repositories");
        panel36.add(fromGitlabCloneStarredRepositoriesCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGitlabCloneForkedRepositoriesCheckBox = new JCheckBox();
        fromGitlabCloneForkedRepositoriesCheckBox.setText("clone forked repositories");
        panel36.add(fromGitlabCloneForkedRepositoriesCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGitlabClonePrivateRepositoriesCheckBox = new JCheckBox();
        fromGitlabClonePrivateRepositoriesCheckBox.setText("clone private repositories");
        panel36.add(fromGitlabClonePrivateRepositoriesCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGitlabCloneArchivedRepositoriesCheckBox = new JCheckBox();
        fromGitlabCloneArchivedRepositoriesCheckBox.setText("clone archived repositories");
        panel36.add(fromGitlabCloneArchivedRepositoriesCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGitlabClonePublicRepositoriesCheckBox = new JCheckBox();
        fromGitlabClonePublicRepositoriesCheckBox.setText("clone public repositories");
        panel36.add(fromGitlabClonePublicRepositoriesCheckBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGitlabCloneOrganizationsRepositoriesCheckBox = new JCheckBox();
        fromGitlabCloneOrganizationsRepositoriesCheckBox.setText("clone organization's repositories");
        panel36.add(fromGitlabCloneOrganizationsRepositoriesCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel37 = new JPanel();
        panel37.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel35.add(panel37, new GridConstraints(3, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel37.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Exclude", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label21 = new JLabel();
        label21.setText("Exclude organizations");
        panel37.add(label21, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label22 = new JLabel();
        label22.setText("Exclude repos");
        panel37.add(label22, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGitlabExcludeRepoNamesListTextField = new JTextField();
        panel37.add(fromGitlabExcludeRepoNamesListTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fromGitlabExcludeOrganizationTextField = new JTextField();
        panel37.add(fromGitlabExcludeOrganizationTextField, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label23 = new JLabel();
        label23.setText("(repo names separated by comma)");
        panel37.add(label23, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label24 = new JLabel();
        label24.setText("(usernames separated by comma)");
        panel37.add(label24, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel38 = new JPanel();
        panel38.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        fromGitlabOptionsTabbedPane.addTab("File", new ImageIcon(getClass().getResource("/images/sm/list.png")), panel38, "Custom List");
        fromGitlabOptionsTabbedPane.setDisabledIconAt(1, new ImageIcon(getClass().getResource("/images/sm/list-gray.png")));
        final JPanel panel39 = new JPanel();
        panel39.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel38.add(panel39, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label25 = new JLabel();
        label25.setText("File containing the repo list names to clone");
        panel39.add(label25, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromGitlabIncludeRepoNamesListFile = new JTextField();
        panel39.add(fromGitlabIncludeRepoNamesListFile, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fromGitlabChooseRepoFileButton = new JButton();
        fromGitlabChooseRepoFileButton.setText("Choose file");
        panel39.add(fromGitlabChooseRepoFileButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer12 = new Spacer();
        panel39.add(spacer12, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer13 = new Spacer();
        panel39.add(spacer13, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label26 = new JLabel();
        label26.setText("(file line pattern: repo-name + new line)");
        panel39.add(label26, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromLocalPanel = new JPanel();
        fromLocalPanel.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 10, 20), -1, -1));
        fromTabbedPane.addTab("From Local", new ImageIcon(getClass().getResource("/images/sm/floppy.png")), fromLocalPanel);
        fromTabbedPane.setDisabledIconAt(3, new ImageIcon(getClass().getResource("/images/sm/floppy-disabled.png")));
        fromLocalPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "From Local", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, 20, fromLocalPanel.getFont()), null));
        final JPanel panel40 = new JPanel();
        panel40.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        fromLocalPanel.add(panel40, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel41 = new JPanel();
        panel41.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 20, 20), -1, -1));
        panel40.add(panel41, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel41.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label27 = new JLabel();
        label27.setIcon(new ImageIcon(getClass().getResource("/images/xl/floppy.png")));
        label27.setText("");
        panel41.add(label27, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer14 = new Spacer();
        panel40.add(spacer14, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel42 = new JPanel();
        panel42.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel40.add(panel42, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label28 = new JLabel();
        label28.setText("Root path");
        panel42.add(label28, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer15 = new Spacer();
        panel42.add(spacer15, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        fromLocalRootPathTextField = new JTextField();
        panel42.add(fromLocalRootPathTextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fromLocalChooseButton = new JButton();
        fromLocalChooseButton.setText("Choose");
        panel42.add(fromLocalChooseButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel43 = new JPanel();
        panel43.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel43, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        toTabbedPane = new JTabbedPane();
        toTabbedPane.setTabPlacement(2);
        panel43.add(toTabbedPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel44 = new JPanel();
        panel44.setLayout(new GridLayoutManager(2, 2, new Insets(20, 20, 0, 20), -1, -1));
        toTabbedPane.addTab("To GitHub", new ImageIcon(getClass().getResource("/images/sm/github.png")), panel44);
        toTabbedPane.setDisabledIconAt(0, new ImageIcon(getClass().getResource("/images/sm/github-disabled.png")));
        panel44.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "To GitHub", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, 20, panel44.getFont()), null));
        final JPanel panel45 = new JPanel();
        panel45.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel44.add(panel45, new GridConstraints(0, 0, 2, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel46 = new JPanel();
        panel46.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 20, 20), -1, -1));
        panel45.add(panel46, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel46.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label29 = new JLabel();
        label29.setIcon(new ImageIcon(getClass().getResource("/images/xl/github.png")));
        label29.setText("");
        panel46.add(label29, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer16 = new Spacer();
        panel45.add(spacer16, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel47 = new JPanel();
        panel47.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel45.add(panel47, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label30 = new JLabel();
        label30.setText("GitHub Token");
        panel47.add(label30, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toGithubTokenTextField = new JPasswordField();
        panel47.add(toGithubTokenTextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label31 = new JLabel();
        label31.setText("repository privacy");
        panel47.add(label31, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toGithubPrivacyComboBox = new JComboBox();
        panel47.add(toGithubPrivacyComboBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel48 = new JPanel();
        panel48.setLayout(new GridLayoutManager(1, 2, new Insets(10, 10, 10, 10), -1, -1));
        panel47.add(panel48, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel48.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-1769445)), "DANGER ZONE", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, new Color(-1769445)));
        toGithubOverrideIfExistsCheckBox = new JCheckBox();
        toGithubOverrideIfExistsCheckBox.setForeground(new Color(-1769445));
        toGithubOverrideIfExistsCheckBox.setText("Override destination if exists");
        panel48.add(toGithubOverrideIfExistsCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer17 = new Spacer();
        panel48.add(spacer17, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer18 = new Spacer();
        panel47.add(spacer18, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer19 = new Spacer();
        panel47.add(spacer19, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel49 = new JPanel();
        panel49.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 0, 20), -1, -1));
        toTabbedPane.addTab("To Gitea", new ImageIcon(getClass().getResource("/images/sm/gitea.png")), panel49);
        toTabbedPane.setDisabledIconAt(1, new ImageIcon(getClass().getResource("/images/sm/gitea-disabled.png")));
        panel49.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "To Gitea", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, 20, panel49.getFont()), null));
        final JPanel panel50 = new JPanel();
        panel50.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel49.add(panel50, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel51 = new JPanel();
        panel51.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 20, 20), -1, -1));
        panel50.add(panel51, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel51.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label32 = new JLabel();
        label32.setIcon(new ImageIcon(getClass().getResource("/images/xl/gitea.png")));
        label32.setText("");
        panel51.add(label32, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer20 = new Spacer();
        panel50.add(spacer20, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel52 = new JPanel();
        panel52.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel50.add(panel52, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel53 = new JPanel();
        panel53.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel52.add(panel53, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label33 = new JLabel();
        label33.setText("repository privacy");
        panel53.add(label33, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toGiteaPrivacyComboBox = new JComboBox();
        panel53.add(toGiteaPrivacyComboBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel54 = new JPanel();
        panel54.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel53.add(panel54, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel55 = new JPanel();
        panel55.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel54.add(panel55, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label34 = new JLabel();
        label34.setText("Gitea Token");
        panel55.add(label34, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toGiteaTokenTextField = new JPasswordField();
        panel55.add(toGiteaTokenTextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel56 = new JPanel();
        panel56.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel54.add(panel56, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label35 = new JLabel();
        label35.setText("Gitea URL");
        panel56.add(label35, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toGiteaUrlTextField = new JTextField();
        panel56.add(toGiteaUrlTextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel57 = new JPanel();
        panel57.setLayout(new GridLayoutManager(1, 2, new Insets(10, 10, 10, 10), -1, -1));
        panel53.add(panel57, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel57.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-1769445)), "DANGER ZONE", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, new Color(-1769445)));
        toGiteaOverrideIfExistsCheckBox = new JCheckBox();
        toGiteaOverrideIfExistsCheckBox.setForeground(new Color(-1769445));
        toGiteaOverrideIfExistsCheckBox.setText("Override destination if exists");
        panel57.add(toGiteaOverrideIfExistsCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer21 = new Spacer();
        panel57.add(spacer21, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer22 = new Spacer();
        panel53.add(spacer22, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer23 = new Spacer();
        panel53.add(spacer23, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        toGitlabPanel = new JPanel();
        toGitlabPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        toTabbedPane.addTab("To Gitlab", new ImageIcon(getClass().getResource("/images/sm/gitlab.png")), toGitlabPanel);
        final JPanel panel58 = new JPanel();
        panel58.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        toGitlabPanel.add(panel58, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel58.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "To Gitlab", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, 20, panel58.getFont()), new Color(-2104859)));
        final JPanel panel59 = new JPanel();
        panel59.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 20, 20), -1, -1));
        panel58.add(panel59, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel59.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label36 = new JLabel();
        label36.setIcon(new ImageIcon(getClass().getResource("/images/xl/gitlab.png")));
        label36.setText("");
        panel59.add(label36, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer24 = new Spacer();
        panel58.add(spacer24, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel60 = new JPanel();
        panel60.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 20, 20), -1, -1));
        panel58.add(panel60, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel61 = new JPanel();
        panel61.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel60.add(panel61, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label37 = new JLabel();
        label37.setText("repository privacy");
        panel61.add(label37, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toGitlabPrivacyComboBox = new JComboBox();
        panel61.add(toGitlabPrivacyComboBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel62 = new JPanel();
        panel62.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel61.add(panel62, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel63 = new JPanel();
        panel63.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel62.add(panel63, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label38 = new JLabel();
        label38.setText("Gitlab Token");
        panel63.add(label38, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toGitlabTokenTextField = new JPasswordField();
        panel63.add(toGitlabTokenTextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel64 = new JPanel();
        panel64.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel62.add(panel64, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label39 = new JLabel();
        label39.setText("Gitlab URL");
        panel64.add(label39, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toGitlabUrlTextField = new JTextField();
        panel64.add(toGitlabUrlTextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel65 = new JPanel();
        panel65.setLayout(new GridLayoutManager(1, 2, new Insets(10, 10, 10, 10), -1, -1));
        panel61.add(panel65, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel65.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-1769445)), "DANGER ZONE", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, new Color(-1769445)));
        toGitlabOverrideIfExistsCheckBox = new JCheckBox();
        toGitlabOverrideIfExistsCheckBox.setForeground(new Color(-1769445));
        toGitlabOverrideIfExistsCheckBox.setText("Override destination if exists");
        panel65.add(toGitlabOverrideIfExistsCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer25 = new Spacer();
        panel65.add(spacer25, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer26 = new Spacer();
        panel61.add(spacer26, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer27 = new Spacer();
        panel61.add(spacer27, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel66 = new JPanel();
        panel66.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 0, 20), -1, -1));
        toTabbedPane.addTab("To Local", new ImageIcon(getClass().getResource("/images/sm/floppy.png")), panel66);
        toTabbedPane.setDisabledIconAt(3, new ImageIcon(getClass().getResource("/images/sm/floppy-disabled.png")));
        panel66.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "To Local", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, 20, panel66.getFont()), null));
        final JPanel panel67 = new JPanel();
        panel67.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel66.add(panel67, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel68 = new JPanel();
        panel68.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 20, 20), -1, -1));
        panel67.add(panel68, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel68.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label40 = new JLabel();
        label40.setIcon(new ImageIcon(getClass().getResource("/images/xl/floppy.png")));
        label40.setText("");
        panel68.add(label40, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer28 = new Spacer();
        panel67.add(spacer28, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel69 = new JPanel();
        panel69.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel67.add(panel69, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label41 = new JLabel();
        label41.setText("Root path");
        panel69.add(label41, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toLocalRootPathTextField = new JTextField();
        panel69.add(toLocalRootPathTextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        toLocalGroupByRepositoryOwnerCheckBox = new JCheckBox();
        toLocalGroupByRepositoryOwnerCheckBox.setText("group by repository owner");
        panel69.add(toLocalGroupByRepositoryOwnerCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toLocalChooseButton = new JButton();
        toLocalChooseButton.setText("Choose");
        panel69.add(toLocalChooseButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel70 = new JPanel();
        panel70.setLayout(new GridLayoutManager(1, 2, new Insets(10, 10, 10, 10), -1, -1));
        panel69.add(panel70, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel70.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-1769445)), "DANGER ZONE", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, -1, panel70.getFont()), new Color(-1769445)));
        toLocalOverrideIfExistsCheckBox = new JCheckBox();
        toLocalOverrideIfExistsCheckBox.setForeground(new Color(-1769445));
        toLocalOverrideIfExistsCheckBox.setText("Override destination if exists");
        panel70.add(toLocalOverrideIfExistsCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer29 = new Spacer();
        panel70.add(spacer29, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer30 = new Spacer();
        panel69.add(spacer30, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer31 = new Spacer();
        panel69.add(spacer31, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel71 = new JPanel();
        panel71.setLayout(new GridLayoutManager(2, 2, new Insets(0, 20, 0, 20), -1, -1));
        panel43.add(panel71, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel71.add(scrollPane2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        appLogTextArea = new JTextArea();
        appLogTextArea.setMinimumSize(new Dimension(-1, -1));
        appLogTextArea.setRows(5);
        appLogTextArea.setTabSize(0);
        appLogTextArea.setText("");
        scrollPane2.setViewportView(appLogTextArea);
        final JLabel label42 = new JLabel();
        label42.setText("Log");
        panel71.add(label42, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel72 = new JPanel();
        panel72.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel71.add(panel72, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel73 = new JPanel();
        panel73.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel72.add(panel73, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        appSaveConfigurationButton = new JButton();
        appSaveConfigurationButton.setText("Save configuration");
        panel73.add(appSaveConfigurationButton, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        appSleepTimeTextField = new JTextField();
        panel73.add(appSleepTimeTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label43 = new JLabel();
        label43.setText("sleep time (s)");
        panel73.add(label43, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chronJobCheckBox = new JCheckBox();
        chronJobCheckBox.setText("Cron Job");
        panel73.add(chronJobCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label44 = new JLabel();
        label44.setText("Cron expression");
        panel73.add(label44, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chronExpressionTextField = new JTextField();
        panel73.add(chronExpressionTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        multithreadingEnabled = new JCheckBox();
        multithreadingEnabled.setText("Multi-threadead");
        panel72.add(multithreadingEnabled, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel74 = new JPanel();
        panel74.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel74, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        statusContainerJPanel = new JPanel();
        statusContainerJPanel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 20, 20, 20), -1, -1));
        panel74.add(statusContainerJPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        appStartButton = new JButton();
        appStartButton.setText("Start");
        statusContainerJPanel.add(appStartButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel75 = new JPanel();
        panel75.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        statusContainerJPanel.add(panel75, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        appProgressMessageStatusLabel = new JLabel();
        appProgressMessageStatusLabel.setText("waiting for user action");
        panel75.add(appProgressMessageStatusLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer32 = new Spacer();
        panel75.add(spacer32, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        appProgressStatusLabel = new JLabel();
        appProgressStatusLabel.setText("0/0");
        panel75.add(appProgressStatusLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer33 = new Spacer();
        panel75.add(spacer33, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        timeLable = new JLabel();
        timeLable.setText("00:00:00");
        panel75.add(timeLable, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        appProgressBar = new JProgressBar();
        statusContainerJPanel.add(appProgressBar, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer34 = new Spacer();
        statusContainerJPanel.add(spacer34, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        appStopButton = new JButton();
        appStopButton.setText("Stop");
        statusContainerJPanel.add(appStopButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel76 = new JPanel();
        panel76.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainTabbedPane.addTab("Tools", new ImageIcon(getClass().getResource("/images/sm/tools.png")), panel76);
        mainTabbedPane.setDisabledIconAt(1, new ImageIcon(getClass().getResource("/images/sm/tools-disabled.png")));
        final JScrollPane scrollPane3 = new JScrollPane();
        panel76.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel77 = new JPanel();
        panel77.setLayout(new GridLayoutManager(3, 1, new Insets(20, 20, 20, 20), -1, -1));
        scrollPane3.setViewportView(panel77);
        final Spacer spacer35 = new Spacer();
        panel77.add(spacer35, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel78 = new JPanel();
        panel78.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel77.add(panel78, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel78.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Log", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final Spacer spacer36 = new Spacer();
        panel78.add(spacer36, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label45 = new JLabel();
        label45.setText("Open log file");
        panel78.add(label45, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        appOpenLogFileButton = new JButton();
        appOpenLogFileButton.setText("open log file");
        panel78.add(appOpenLogFileButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearLogFileButton = new JButton();
        clearLogFileButton.setForeground(new Color(-2979020));
        clearLogFileButton.setText("clear log file");
        panel78.add(clearLogFileButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel79 = new JPanel();
        panel79.setLayout(new GridLayoutManager(1, 1, new Insets(30, 0, 0, 0), -1, -1));
        panel77.add(panel79, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel80 = new JPanel();
        panel80.setLayout(new GridLayoutManager(4, 3, new Insets(10, 10, 10, 10), -1, -1));
        panel79.add(panel80, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel80.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-5363968)), "DANGER ZONE", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD, 20, panel80.getFont()), new Color(-5363968)));
        final JLabel label46 = new JLabel();
        label46.setForeground(new Color(-5363968));
        label46.setText("Delete all repositories on GitHub");
        panel80.add(label46, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel81 = new JPanel();
        panel81.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel80.add(panel81, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        toolsDeleteALLGitHubRepositoriesButton = new JButton();
        toolsDeleteALLGitHubRepositoriesButton.setForeground(new Color(-5363968));
        toolsDeleteALLGitHubRepositoriesButton.setText("Delete ALL GitHub Repositories");
        panel81.add(toolsDeleteALLGitHubRepositoriesButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label47 = new JLabel();
        label47.setForeground(new Color(-5363968));
        label47.setText("Delete all repositories on Gitea");
        panel80.add(label47, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel82 = new JPanel();
        panel82.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel80.add(panel82, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        toolsDeleteALLGiteaRepositoriesButton = new JButton();
        toolsDeleteALLGiteaRepositoriesButton.setForeground(new Color(-5363968));
        toolsDeleteALLGiteaRepositoriesButton.setText("Delete ALL Gitea Repositories ");
        panel82.add(toolsDeleteALLGiteaRepositoriesButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toolsDeleteALLGitlabRepositoriesButton = new JButton();
        toolsDeleteALLGitlabRepositoriesButton.setForeground(new Color(-5363968));
        toolsDeleteALLGitlabRepositoriesButton.setText("Delete ALL Gitlab Repositories ");
        panel80.add(toolsDeleteALLGitlabRepositoriesButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer37 = new Spacer();
        panel80.add(spacer37, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer38 = new Spacer();
        panel80.add(spacer38, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer39 = new Spacer();
        panel80.add(spacer39, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label48 = new JLabel();
        label48.setForeground(new Color(-5363968));
        label48.setText("Delete all repositories on Gitlab");
        panel80.add(label48, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label49 = new JLabel();
        label49.setForeground(new Color(-5429760));
        label49.setText("<html>\nIt will be asked to insert the credentials of the repository that you want to delete.\n<br/>The application will show only a confirmation message\n<br/>after the deletion process was completed\n<br/>The Delete ALL procedure, deletes all public and private repositories.\n</html>");
        panel80.add(label49, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel83 = new JPanel();
        panel83.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainTabbedPane.addTab("About", new ImageIcon(getClass().getResource("/images/sm/about.png")), panel83);
        mainTabbedPane.setDisabledIconAt(2, new ImageIcon(getClass().getResource("/images/sm/about-disabled.png")));
        final JPanel panel84 = new JPanel();
        panel84.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel83.add(panel84, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane4 = new JScrollPane();
        panel84.add(scrollPane4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel85 = new JPanel();
        panel85.setLayout(new GridLayoutManager(2, 2, new Insets(20, 20, 20, 20), -1, -1));
        scrollPane4.setViewportView(panel85);
        final JLabel label50 = new JLabel();
        label50.setIcon(new ImageIcon(getClass().getResource("/images/xl/icon.png")));
        label50.setText("");
        panel85.add(label50, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel86 = new JPanel();
        panel86.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel85.add(panel86, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel86.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel87 = new JPanel();
        panel87.setLayout(new GridLayoutManager(8, 4, new Insets(10, 10, 10, 10), -1, -1));
        panel86.add(panel87, new GridConstraints(0, 0, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label51 = new JLabel();
        label51.setText("FromGtoG");
        panel87.add(label51, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer40 = new Spacer();
        panel87.add(spacer40, new GridConstraints(6, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label52 = new JLabel();
        label52.setText("Version: 9.1.2");
        panel87.add(label52, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label53 = new JLabel();
        label53.setText("Author: Andrei Dodu");
        panel87.add(label53, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer41 = new Spacer();
        panel87.add(spacer41, new GridConstraints(7, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel88 = new JPanel();
        panel88.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel87.add(panel88, new GridConstraints(4, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer42 = new Spacer();
        panel88.add(spacer42, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel89 = new JPanel();
        panel89.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel87.add(panel89, new GridConstraints(5, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel90 = new JPanel();
        panel90.setLayout(new GridLayoutManager(4, 2, new Insets(20, 0, 0, 0), -1, -1));
        panel89.add(panel90, new GridConstraints(0, 0, 3, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        projectWebsiteButton = new JButton();
        projectWebsiteButton.setText("Project website");
        panel90.add(projectWebsiteButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer43 = new Spacer();
        panel90.add(spacer43, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        reportAnIssueButton = new JButton();
        reportAnIssueButton.setText("Report an issue");
        panel90.add(reportAnIssueButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buyMeACoffeeButton = new JButton();
        buyMeACoffeeButton.setText("Support my work");
        panel90.add(buyMeACoffeeButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        contactMe = new JButton();
        contactMe.setText("Contact me");
        panel90.add(contactMe, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer44 = new Spacer();
        panel87.add(spacer44, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer45 = new Spacer();
        panel85.add(spacer45, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /** @noinspection ALL */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
