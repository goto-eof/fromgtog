package com.andreidodu.fromgtog.gui;

import com.andreidodu.fromgtog.service.JsonObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;
import java.util.List;

import static com.andreidodu.fromgtog.gui.GuiKeys.APP_SLEEP_TIME;
import static com.andreidodu.fromgtog.util.NumberUtil.toIntegerOrDefault;

@Getter
@Setter
public class AppController {

    private List<DataProviderFromController> fromControllerList;
    private List<DataProviderToController> toControllerList;
    private JTextArea appLogTextArea;
    private JTextField appSleepTimeTextField;
    private JButton appSaveConfigurationButton;
    private JProgressBar appProgressBar;
    private JLabel appProgressStatusLabel;
    private JLabel appStatusProgressBarLabel;
    private JButton appStartButton;
    private JsonObjectUtil jsonObjectUtil;
    private JTabbedPane fromTabbedPane;
    private JTabbedPane toTabbedPane;

    public AppController(List<DataProviderFromController> fromControllerList,
                         List<DataProviderToController> toControllerList,
                         JTextArea appLogTextArea,
                         JTextField appSleepTimeTextField,
                         JButton appSaveConfigurationButton,
                         JProgressBar appProgressBar,
                         JLabel appProgressStatusLabel,
                         JLabel appStatusProgressBarLabel,
                         JButton appStartButton,
                         JTabbedPane fromTabbedPane,
                         JTabbedPane toTabbedPane) {
        this.fromControllerList = fromControllerList;
        this.toControllerList = toControllerList;
        this.appLogTextArea = appLogTextArea;
        this.appSleepTimeTextField = appSleepTimeTextField;
        this.appSaveConfigurationButton = appSaveConfigurationButton;
        this.appProgressBar = appProgressBar;
        this.appProgressStatusLabel = appProgressStatusLabel;
        this.appStatusProgressBarLabel = appStatusProgressBarLabel;
        this.appStartButton = appStartButton;
        this.fromTabbedPane = fromTabbedPane;
        this.toTabbedPane = toTabbedPane;

        this.jsonObjectUtil = new JsonObjectUtil();

        defineAppStartButtonListener(fromControllerList, toControllerList, fromTabbedPane, toTabbedPane);
    }

    private void defineAppStartButtonListener(List<DataProviderFromController> fromControllerList, List<DataProviderToController> toControllerList, JTabbedPane fromTabbedPane, JTabbedPane toTabbedPane) {
        this.appStartButton.addActionListener(e -> {
            JSONObject jsonObjectFrom = getFromData(fromControllerList, fromTabbedPane.getSelectedIndex());
            JSONObject jsonObjectTo = getFromData(toControllerList, toTabbedPane.getSelectedIndex());
            JSONObject mergedJsonObject = this.jsonObjectUtil.merge(jsonObjectFrom, jsonObjectTo);
            mergedJsonObject = this.jsonObjectUtil.merge(mergedJsonObject, getDataFromChildren());
            // TODO call my service with mergedJsonObject
        });
    }

    private static <T extends DataProviderController> JSONObject getFromData(List<T> fromControllerList, int selectedIndex) {
        return fromControllerList.stream()
                .filter(controller -> controller.accept(selectedIndex))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid tab index"))
                .getDataFromChildren();
    }

    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();
        int sleepSeconds = toIntegerOrDefault(appSleepTimeTextField.getText());
        appSleepTimeTextField.setText(String.valueOf(sleepSeconds));

        jsonObject.put(APP_SLEEP_TIME, sleepSeconds);

        return jsonObject;
    }


}
