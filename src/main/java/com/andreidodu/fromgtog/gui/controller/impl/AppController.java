package com.andreidodu.fromgtog.gui.controller.impl;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.gui.controller.DataProviderController;
import com.andreidodu.fromgtog.gui.controller.DataProviderFromController;
import com.andreidodu.fromgtog.gui.controller.DataProviderToController;
import com.andreidodu.fromgtog.util.JsonObjectUtil;
import com.andreidodu.fromgtog.service.RepositoryCloner;
import com.andreidodu.fromgtog.service.impl.RepositoryClonerImpl;
import com.andreidodu.fromgtog.translator.JsonObjectToAppContextTranslator;
import com.andreidodu.fromgtog.translator.JsonObjectToFromContextTranslator;
import com.andreidodu.fromgtog.translator.JsonObjectToToContextTranslator;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;
import java.util.List;

import static com.andreidodu.fromgtog.gui.GuiKeys.APP_SLEEP_TIME;
import static com.andreidodu.fromgtog.util.NumberUtil.toIntegerOrDefault;


/**
 * TODO error managing for retrieveJsonData -> show error alert
 * TODO validate user input -> show error alert
 */
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
    private JsonObjectToFromContextTranslator translatorFrom;
    private JsonObjectToToContextTranslator translatorTo;
    private JsonObjectToAppContextTranslator translatorApp;

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
        this.translatorFrom = new JsonObjectToFromContextTranslator();
        this.translatorTo = new JsonObjectToToContextTranslator();
        this.translatorApp = new JsonObjectToAppContextTranslator();

        defineAppStartButtonListener(fromControllerList, toControllerList, fromTabbedPane, toTabbedPane);
    }

    private void defineAppStartButtonListener(List<DataProviderFromController> fromControllerList, List<DataProviderToController> toControllerList, JTabbedPane fromTabbedPane, JTabbedPane toTabbedPane) {
        this.appStartButton.addActionListener(e -> {
            appLogTextArea.setText(String.format("%s\n(%s) -> (%s)",
                            appLogTextArea.getText(),
                            fromTabbedPane.getSelectedIndex(),
                            toTabbedPane.getSelectedIndex()
                    )
            );

            JSONObject jsonObjectFrom = retrieveJsonData(fromControllerList, fromTabbedPane.getSelectedIndex());
            JSONObject jsonObjectTo = retrieveJsonData(toControllerList, toTabbedPane.getSelectedIndex());
            JSONObject jsonObjectApp = getDataFromChildren();

            EngineContext engineContext = EngineContext.builder()
                    .settingsContext(translatorApp.translate(jsonObjectApp))
                    .fromContext(translatorFrom.translate(jsonObjectFrom))
                    .toContext(translatorTo.translate(jsonObjectTo))
                    .build();

            appLogTextArea.setText(String.format("%s\n%s(%s) -> %s(%s)",
                            appLogTextArea.getText(),
                            engineContext.fromContext().sourceEngineType(),
                            fromTabbedPane.getSelectedIndex(),
                            engineContext.toContext().engineType(),
                            toTabbedPane.getSelectedIndex()
                    )
            );

            RepositoryCloner repositoryCloner = new RepositoryClonerImpl();
            repositoryCloner.cloneAllRepositories(engineContext);
        });
    }

    private static <T extends DataProviderController> JSONObject retrieveJsonData(List<T> fromControllerList, int selectedIndex) {
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
