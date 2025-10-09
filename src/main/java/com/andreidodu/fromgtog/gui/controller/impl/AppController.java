package com.andreidodu.fromgtog.gui.controller.impl;

import com.andreidodu.fromgtog.constants.SoundConstants;
import com.andreidodu.fromgtog.dto.CallbackContainer;
import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.gui.controller.GUIController;
import com.andreidodu.fromgtog.gui.controller.GUIFromController;
import com.andreidodu.fromgtog.gui.controller.GUIToController;
import com.andreidodu.fromgtog.gui.controller.StrategyGUIController;
import com.andreidodu.fromgtog.gui.controller.translator.impl.JsonObjectToAppContextTranslator;
import com.andreidodu.fromgtog.gui.controller.translator.impl.JsonObjectToFromContextTranslator;
import com.andreidodu.fromgtog.gui.controller.translator.impl.JsonObjectToToContextTranslator;
import com.andreidodu.fromgtog.gui.validator.AbstractRule;
import com.andreidodu.fromgtog.gui.validator.ValidSleepTimeRule;
import com.andreidodu.fromgtog.gui.validator.ValidOrganizationRule;
import com.andreidodu.fromgtog.service.RepositoryCloner;
import com.andreidodu.fromgtog.service.impl.RepositoryClonerServiceImpl;
import com.andreidodu.fromgtog.service.impl.SettingsServiceImpl;
import com.andreidodu.fromgtog.service.impl.SoundPlayer;
import com.andreidodu.fromgtog.util.ApplicationUtil;
import com.andreidodu.fromgtog.util.JsonObjectServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.LOG_DIR_NAME;
import static com.andreidodu.fromgtog.constants.ApplicationConstants.LOG_FILENAME;
import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;
import static com.andreidodu.fromgtog.util.NumberUtil.toIntegerOrDefault;


/**
 * TODO error managing for retrieveJsonData -> show error alert
 * TODO validate user input -> show error alert
 */
@Getter
@Setter
public class AppController implements GUIController {

    Logger log = LoggerFactory.getLogger(AppController.class);

    private List<GUIFromController> fromControllerList;
    private List<GUIToController> toControllerList;
    private JTextArea appLogTextArea;
    private JTextField appSleepTimeTextField;
    private JButton appSaveConfigurationButton;
    private JProgressBar appProgressBar;
    private JLabel messageStatus;
    private JLabel position;
    private JButton appStartButton;

    private JsonObjectServiceImpl jsonObjectUtil;
    private JsonObjectToFromContextTranslator translatorFrom;
    private JsonObjectToToContextTranslator translatorTo;
    private JsonObjectToAppContextTranslator translatorApp;

    private JTabbedPane fromTabbedPane;
    private JTabbedPane toTabbedPane;

    private JButton appOpenLogFileButton;
    private Consumer<Boolean> setEnabledUI;

    private JButton appStopButton;
    private JPanel statusContainerJPanel;
    private JCheckBox multithreadingEnabled;
    @Getter
    @Setter
    private volatile boolean shouldStop = false;

    private JButton clearLogFileButton;
    private JLabel timeLabel;


    public AppController(JSONObject settings,
                         List<GUIFromController> fromControllerList,
                         List<GUIToController> toControllerList,
                         JTextArea appLogTextArea,
                         JTextField appSleepTimeTextField,
                         JButton appSaveConfigurationButton,
                         JProgressBar appProgressBar,
                         JLabel messageStatus,
                         JLabel position,
                         JButton appStartButton,
                         JTabbedPane fromTabbedPane,
                         JTabbedPane toTabbedPane,
                         JButton appOpenLogFileButton,
                         Consumer<Boolean> setEnabledUI,
                         JButton appStopButton,
                         JPanel statusContainerJPanel,
                         JCheckBox multithreadingEnabled,
                         JButton clearLogFileButton,
                         JLabel timeLabel) {
        this.fromControllerList = fromControllerList;
        this.toControllerList = toControllerList;
        this.appLogTextArea = appLogTextArea;
        this.appSleepTimeTextField = appSleepTimeTextField;
        this.appSaveConfigurationButton = appSaveConfigurationButton;
        this.appProgressBar = appProgressBar;
        this.messageStatus = messageStatus;
        this.position = position;
        this.appStartButton = appStartButton;
        this.fromTabbedPane = fromTabbedPane;
        this.toTabbedPane = toTabbedPane;
        this.appOpenLogFileButton = appOpenLogFileButton;
        this.setEnabledUI = setEnabledUI;
        this.appStopButton = appStopButton;
        this.statusContainerJPanel = statusContainerJPanel;
        this.multithreadingEnabled = multithreadingEnabled;
        this.clearLogFileButton = clearLogFileButton;
        this.timeLabel = timeLabel;

        this.translatorTo = new JsonObjectToToContextTranslator();
        this.translatorApp = new JsonObjectToAppContextTranslator();
        this.translatorFrom = new JsonObjectToFromContextTranslator();

        defineAppStartButtonListener(fromControllerList, toControllerList, fromTabbedPane, toTabbedPane);
        defineAppStopButtonListener();
        defineSaveSettingsButtonListener();
        defineOpenLogFileButtonListener();
        defineClearLogFileButtonListener();

        applySettings(settings);

        this.setShouldStop(true);

    }

    private void defineClearLogFileButtonListener() {
        clearLogFileButton.addActionListener(e -> {
            File logFile = getLogFile();
            try (FileWriter writer = new FileWriter(logFile, false)) {
                // do nothing -> it is used just to override the file
            } catch (IOException ee) {
                log.error(ee.getMessage());
                showErrorMessage("Unable to clear the log file: " + ee.getMessage());
            }
        });
    }

    private void defineOpenLogFileButtonListener() {
        File logFile = getLogFile();

        appOpenLogFileButton.addActionListener(e -> {
            String filename = logFile.getAbsolutePath();
            try {
                openLogFileOnLinux(filename);
            } catch (Exception ex) {
                try {
                    openLogFileOnWindows(filename);
                } catch (Exception ex1) {
                    openLogFileOnMacOs(filename);
                }
            }
        });
    }

    private static File getLogFile() {
        File appDataDir = ApplicationUtil.getApplicationRootDirectory();
        File logDir = new File(appDataDir, LOG_DIR_NAME);
        File logFile = new File(logDir, LOG_FILENAME);
        return logFile;
    }

    private void openLogFileOnLinux(String logFilename) {
        try {
            ProcessBuilder pb = new ProcessBuilder("xdg-open", logFilename);
            pb.inheritIO();
            pb.start();
            log.debug("log file open request done");
        } catch (IOException ee) {
            log.error("failed to open log file: {} - {}", logFilename, ee.getMessage());
            throw new RuntimeException("failed to open log file: " + logFilename, ee);
        }
    }

    private void openLogFileOnWindows(String logFilename) {
        try {
            ProcessBuilder pb = new ProcessBuilder("notepad", logFilename);
            pb.inheritIO();
            pb.start();
            log.debug("log file open request done");
        } catch (IOException eee) {
            log.error("failed to open log file: {} - {}", logFilename, eee.getMessage());
            throw new RuntimeException("failed to open log file: " + logFilename, eee);
        }
    }

    private void openLogFileOnMacOs(String logFilename) {
        try {
            ProcessBuilder pb = new ProcessBuilder("open", logFilename);
            pb.inheritIO();
            pb.start();
            log.debug("log file open request done");
        } catch (IOException eee) {
            log.error("failed to open log file: {} - {}", logFilename, eee.getMessage());
        }
    }

    @Override
    public void applySettings(JSONObject settings) {
        appSleepTimeTextField.setText(settings.optString(APP_SLEEP_TIME, "1"));
        fromTabbedPane.setSelectedIndex(settings.optInt(FROM_TAB_INDEX, 0));
        toTabbedPane.setSelectedIndex(settings.optInt(TO_TAB_INDEX, 0));
        multithreadingEnabled.setSelected(settings.optBoolean(APP_MULTITHREADING_ENABLED, false));
    }

    private void defineSaveSettingsButtonListener() {
        appSaveConfigurationButton.addActionListener(e -> {
            JSONObject jsonObjectFrom = retrieveJsonData(fromControllerList, fromTabbedPane.getSelectedIndex());
            JSONObject jsonObjectTo = retrieveJsonData(toControllerList, toTabbedPane.getSelectedIndex());
            JSONObject jsonObjectApp = getDataFromChildren();

            var allSettingsArr = new JSONObject[]{jsonObjectFrom, jsonObjectTo, jsonObjectApp};
            JSONObject allSettings = JsonObjectServiceImpl.getInstance().merge(allSettingsArr);

            List<String> errorList = validateSettings(allSettings);
            if (!errorList.isEmpty()) {
                this.showErrorMessage("Something went wrong." + System.lineSeparator() + String.join(System.lineSeparator(), errorList));
                setEnabledUI.accept(true);
                setShouldStop(true);
                return;
            }
            saveSettings(allSettings);
        });
    }

    public synchronized void setShouldStop(boolean shouldStop) {
        this.shouldStop = shouldStop;
        SwingUtilities.invokeLater(() -> {
            this.appStartButton.setVisible(shouldStop);
            this.appStopButton.setVisible(!shouldStop);
        });
    }

    private void defineAppStopButtonListener() {
        this.appStopButton.addActionListener(e -> {
            this.setShouldStop(true);
            this.appStartButton.setVisible(true);
            this.appStopButton.setVisible(false);
        });
    }

    private void defineAppStartButtonListener(List<GUIFromController> fromControllerList, List<GUIToController> toControllerList, JTabbedPane fromTabbedPane, JTabbedPane toTabbedPane) {

        this.appStartButton.addActionListener(e -> {
            try {
                this.setShouldStop(false);
                this.appStartButton.setVisible(false);
                this.appStopButton.setVisible(true);

                JSONObject jsonObjectFrom = retrieveJsonData(fromControllerList, fromTabbedPane.getSelectedIndex());
                JSONObject jsonObjectTo = retrieveJsonData(toControllerList, toTabbedPane.getSelectedIndex());
                JSONObject jsonObjectApp = getDataFromChildren();


                EngineContext engineContext = EngineContext.builder()
                        .settingsContext(translatorApp.translate(jsonObjectApp))
                        .fromContext(translatorFrom.translate(jsonObjectFrom))
                        .toContext(translatorTo.translate(jsonObjectTo))
                        .callbackContainer(CallbackContainer
                                .builder()
                                .updateApplicationProgressBarMax(this::updateApplicationProgressBarMax)
                                .updateApplicationProgressBarCurrent(this::updateApplicationProgressBarCurrent)
                                .updateApplicationStatusMessage(this::updateApplicationStatusMessage)
                                .setEnabledUI(setEnabledUI)
                                .showErrorMessage(this::invokeLaterShowErrorMessage)
                                .showSuccessMessage(this::invokeLaterSuccessMessage)
                                .isShouldStop(this::isShouldStop)
                                .setShouldStop(this::setShouldStop)
                                .updateTimeLabel(this::updateTimeLabel)
                                .build())
                        .build();

                var allSettingsArr = new JSONObject[]{jsonObjectFrom, jsonObjectTo, jsonObjectApp};
                JSONObject allSettings = JsonObjectServiceImpl.getInstance().merge(allSettingsArr);

                List<String> errorList = validateSettings(allSettings);
                if (!errorList.isEmpty()) {
                    this.showErrorMessage("Something went wrong. " + System.lineSeparator() + String.join(System.lineSeparator(), errorList));
                    setEnabledUI.accept(true);
                    setShouldStop(true);
                    return;
                }
                saveSettings(allSettingsArr);

                RepositoryCloner repositoryCloner = RepositoryClonerServiceImpl.getInstance();
                repositoryCloner.cloneAllRepositories(engineContext);
            } catch (Exception ee) {
                this.showErrorMessage("Something went wrong. " + ee.getMessage());
                setEnabledUI.accept(true);
                setShouldStop(true);
            }
        });
    }

    private List<String> validateSettings(JSONObject allSettings) {
        List<AbstractRule> ruleList = List.of(
                new ValidSleepTimeRule(allSettings),
                new ValidOrganizationRule(allSettings)
        );

        return ruleList.stream()
                .filter(rule -> !rule.pass(allSettings))
                .map(AbstractRule::getInvalidMessage)
                .toList();
    }

    private boolean validate(String value, String regex, String errorMessage) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }


    private void updateTimeLabel(String message) {
        SwingUtilities.invokeLater(() -> {
            timeLabel.setText(String.format("%s", message));
        });
    }


    private void saveSettings(JSONObject... jsonObjectArr) {
        log.debug("Saving settings...");
        JSONObject allSettings = JsonObjectServiceImpl.getInstance().merge(jsonObjectArr);
        SettingsServiceImpl.getInstance().save(allSettings);
        log.debug("Done.");
    }

    private void invokeLaterSuccessMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            showSuccessMessage(message);
        });
    }

    private void showSuccessMessage(String message) {
        SoundPlayer.getInstance().play(SoundConstants.KEY_SUCCESS);
        JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void invokeLaterShowErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            showErrorMessage(message);
        });
    }

    private void showErrorMessage(String message) {
        SoundPlayer.getInstance().play(SoundConstants.KEY_ERROR);
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static <T extends StrategyGUIController> JSONObject retrieveJsonData(List<T> fromControllerList,
                                                                                 int selectedIndex) {
        return fromControllerList.stream()
                .filter(controller -> controller.accept(selectedIndex))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid tab index"))
                .getDataFromChildren();
    }

    @Override
    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();
        int sleepSeconds = toIntegerOrDefault(appSleepTimeTextField.getText());
        appSleepTimeTextField.setText(String.valueOf(sleepSeconds));

        jsonObject.put(APP_SLEEP_TIME, sleepSeconds);
        jsonObject.put(APP_MULTITHREADING_ENABLED, multithreadingEnabled.isSelected());
        jsonObject.put(FROM_TAB_INDEX, fromTabbedPane.getSelectedIndex());
        jsonObject.put(TO_TAB_INDEX, toTabbedPane.getSelectedIndex());

        return jsonObject;
    }

    private void updateApplicationStatusMessage(String message) {
        SwingUtilities.invokeLater(() -> messageStatus.setText(correctMessageLength(message)));
        SwingUtilities.invokeLater(() -> appLogTextArea.setText(String.format("%s\n%s", appLogTextArea.getText(), correctMessageLength(message))));
        log.info("{}", message);
    }

    private static String correctMessageLength(String message) {
        if (message.length() > 60) {
            return message.substring(0, 60) + "...";
        }
        return message;
    }

    private void updateApplicationProgressBarCurrent(int i) {
        SwingUtilities.invokeLater(() -> position.setText(i + "/" + appProgressBar.getMaximum()));
        SwingUtilities.invokeLater(() -> appProgressBar.setValue(i));
        log.info("cloning: {}/{}", i, appProgressBar.getMaximum());

    }

    private void updateApplicationProgressBarMax(int value) {
        SwingUtilities.invokeLater(() -> appProgressBar.setMaximum(value));
    }

}
