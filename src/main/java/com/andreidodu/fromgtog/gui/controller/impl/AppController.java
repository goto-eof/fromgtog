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
import com.andreidodu.fromgtog.gui.validator.composite.ValidationComposite;
import com.andreidodu.fromgtog.gui.validator.composite.factory.SetupFactory;
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
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.LOG_DIR_NAME;
import static com.andreidodu.fromgtog.constants.ApplicationConstants.LOG_FILENAME;
import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;
import static com.andreidodu.fromgtog.util.NumberUtil.toIntegerOrDefault;


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
    @Getter
    @Setter
    private volatile boolean isWorking = false;

    private JButton clearLogFileButton;
    private JLabel timeLabel;

    private JCheckBox chronJobCheckBox;
    private JTextField chronExpressionTextField;

    private Consumer<Boolean> toggleTrayIcon;

    public AppController(
            JSONObject settings,
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
            JLabel timeLabel,
            JCheckBox chronJobCheckBox,
            JTextField chronExpressionTextField,
            Consumer<Boolean> toggleTrayIcon
    ) {
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

        this.chronJobCheckBox = chronJobCheckBox;
        this.chronExpressionTextField = chronExpressionTextField;

        this.translatorTo = new JsonObjectToToContextTranslator();
        this.translatorApp = new JsonObjectToAppContextTranslator();
        this.translatorFrom = new JsonObjectToFromContextTranslator();

        this.toggleTrayIcon = toggleTrayIcon;

        defineAppStartButtonListener(fromControllerList, toControllerList, fromTabbedPane, toTabbedPane);
        defineAppStopButtonListener();
        defineSaveSettingsButtonListener();
        defineOpenLogFileButtonListener();
        defineClearLogFileButtonListener();
        addEnableChronJobCheckBoxListener();

        applySettings(settings);

        this.setShouldStop(true);

        if (chronJobCheckBox.isSelected()) {
            Thread.ofPlatform().start(() -> start(fromControllerList, toControllerList, fromTabbedPane, toTabbedPane));
        }

    }

    private void addEnableChronJobCheckBoxListener() {
        chronJobCheckBox.addActionListener(e -> {
            chronExpressionTextField.setEnabled(chronJobCheckBox.isSelected());
        });
    }

    private static File getLogFile() {
        File appDataDir = ApplicationUtil.getApplicationRootDirectory();
        File logDir = new File(appDataDir, LOG_DIR_NAME);
        File logFile = new File(logDir, LOG_FILENAME);
        return logFile;
    }

    private static <T extends StrategyGUIController> JSONObject retrieveJsonData(List<T> fromControllerList,
                                                                                 int selectedIndex) {
        return fromControllerList.stream()
                .filter(controller -> controller.accept(selectedIndex))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid tab index"))
                .getDataFromChildren();
    }

    private static String correctMessageLength(String message) {
        if (message.length() > 60) {
            return message.substring(0, 60) + "...";
        }
        return message;
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

        chronJobCheckBox.setSelected(settings.optBoolean(APP_CHRON_JOB_ENABLED, false));
        chronExpressionTextField.setText(settings.optString(APP_CHRON_JOB_EXPRESSION, ""));
        chronExpressionTextField.setEnabled(chronJobCheckBox.isSelected());

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
                setWorking(false);
                return;
            }
            saveSettings(allSettings);
        });
    }

    private synchronized void enableDisableUi(boolean enable) {
        setEnabledUI.accept(enable);
    }

    public synchronized void setShouldStop(boolean shouldStop) {
        this.shouldStop = shouldStop;
        log.debug("Setting should stop: " + shouldStop);
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
            this.enableDisableUi(true);
        });
    }

    private void defineAppStartButtonListener(List<GUIFromController> fromControllerList, List<GUIToController> toControllerList, JTabbedPane fromTabbedPane, JTabbedPane toTabbedPane) {
        this.appStartButton.addActionListener(e -> Thread.ofPlatform().start(() -> start(fromControllerList, toControllerList, fromTabbedPane, toTabbedPane)));
    }

    private void start(List<GUIFromController> fromControllerList, List<GUIToController> toControllerList, JTabbedPane fromTabbedPane, JTabbedPane toTabbedPane) {
        try {
            this.setShouldStop(false);
            SwingUtilities.invokeLater(() -> {
                this.appStartButton.setVisible(false);
                this.appStopButton.setVisible(true);
            });

            JSONObject jsonObjectFrom = retrieveJsonData(fromControllerList, fromTabbedPane.getSelectedIndex());
            JSONObject jsonObjectTo = retrieveJsonData(toControllerList, toTabbedPane.getSelectedIndex());
            JSONObject jsonObjectApp = getDataFromChildren();

            var allSettingsArr = new JSONObject[]{jsonObjectFrom, jsonObjectTo, jsonObjectApp};
            JSONObject allSettings = JsonObjectServiceImpl.getInstance().merge(allSettingsArr);

            List<String> errorList = validateSettings(allSettings);

            if (!errorList.isEmpty()) {
                this.showErrorMessage("Something went wrong. " + System.lineSeparator() + String.join(System.lineSeparator(), errorList));
                setEnabledUI.accept(true);
                setShouldStop(true);
                setWorking(false);
                return;
            }

            saveSettings(allSettingsArr);

            EngineContext engineContext = EngineContext.builder()
                    .settingsContext(translatorApp.translate(jsonObjectApp))
                    .fromContext(translatorFrom.translate(jsonObjectFrom))
                    .toContext(translatorTo.translate(jsonObjectTo))
                    .callbackContainer(CallbackContainer
                            .builder()
                            .updateApplicationProgressBarMax(this::updateApplicationProgressBarMax)
                            .updateApplicationProgressBarCurrent(this::updateApplicationProgressBarCurrent)
                            .updateLogAndApplicationStatusMessage(this::updateLogAndApplicationStatusMessage)
                            .updateApplicationStatusMessage(this::updateApplicationStatusMessage)
                            .setEnabledUI(setEnabledUI)
                            .isWorking(this::isWorking)
                            .setWorking(this::setWorking)
                            .showErrorMessage(this::invokeLaterShowErrorMessage)
                            .showSuccessMessage(this::invokeLaterSuccessMessage)
                            .isShouldStop(this::isShouldStop)
                            .setShouldStop(this::setShouldStop)
                            .updateTimeLabel(this::updateTimeLabel)
                            .jobTicker(this::ticTacJobStatusToggle)
                            .build())
                    .build();

            RepositoryCloner repositoryCloner = RepositoryClonerServiceImpl.getInstance();
            repositoryCloner.cloneAllRepositories(engineContext);
        } catch (Exception ee) {
            log.error(ee.getMessage(), ee);
            this.showErrorMessage("Something went wrong. " + ee.getMessage());
            setEnabledUI.accept(true);
            setShouldStop(true);
            setWorking(false);
        }
    }

    private List<String> validateSettings(JSONObject allSettings) {
        return ValidationComposite.getErrorMessageList(allSettings, new SetupFactory().build());
    }

    private void updateTimeLabel(String message) {
        SwingUtilities.invokeLater(() -> {
            timeLabel.setText(String.format("%s", message));
        });
    }

    private void ticTacJobStatusToggle(boolean loadDefault) {
        SwingUtilities.invokeLater(() -> {
            if (loadDefault) {
                chronJobCheckBox.setForeground(Color.yellow);
                this.toggleTrayIcon.accept(loadDefault);
            } else if (chronJobCheckBox.getForeground().equals(Color.red)) {
                chronJobCheckBox.setForeground(Color.yellow);
                this.toggleTrayIcon.accept(loadDefault);
            } else {
                chronJobCheckBox.setForeground(Color.red);
                this.toggleTrayIcon.accept(loadDefault);
            }
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

    @Override
    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();
        int sleepSeconds = toIntegerOrDefault(appSleepTimeTextField.getText());
        appSleepTimeTextField.setText(String.valueOf(sleepSeconds));

        jsonObject.put(APP_SLEEP_TIME, sleepSeconds);
        jsonObject.put(APP_MULTITHREADING_ENABLED, multithreadingEnabled.isSelected());
        jsonObject.put(FROM_TAB_INDEX, fromTabbedPane.getSelectedIndex());
        jsonObject.put(TO_TAB_INDEX, toTabbedPane.getSelectedIndex());

        jsonObject.put(APP_CHRON_JOB_ENABLED, chronJobCheckBox.isSelected());
        jsonObject.put(APP_CHRON_JOB_EXPRESSION, chronExpressionTextField.getText());

        return jsonObject;
    }

    private void updateLogAndApplicationStatusMessage(String message) {
        SwingUtilities.invokeLater(() -> messageStatus.setText(correctMessageLength(message)));
        SwingUtilities.invokeLater(() -> appLogTextArea.setText(String.format("%s\n%s", appLogTextArea.getText(), correctMessageLength(message))));
        log.info("{}", message);
    }

    private void updateApplicationStatusMessage(String message) {
        SwingUtilities.invokeLater(() -> messageStatus.setText(correctMessageLength(message)));
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
