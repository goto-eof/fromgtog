package com.andreidodu.fromgtog.gui.controller.impl;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.gui.controller.GUIController;
import com.andreidodu.fromgtog.gui.controller.GUIFromController;
import com.andreidodu.fromgtog.gui.controller.GUIToController;
import com.andreidodu.fromgtog.gui.controller.StrategyGUIController;
import com.andreidodu.fromgtog.service.impl.SettingsServiceImpl;
import com.andreidodu.fromgtog.util.JsonObjectServiceImpl;
import com.andreidodu.fromgtog.service.RepositoryCloner;
import com.andreidodu.fromgtog.service.impl.RepositoryClonerImpl;
import com.andreidodu.fromgtog.translator.impl.JsonObjectToAppContextTranslator;
import com.andreidodu.fromgtog.translator.impl.JsonObjectToFromContextTranslator;
import com.andreidodu.fromgtog.translator.impl.JsonObjectToToContextTranslator;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;

import static com.andreidodu.fromgtog.gui.GuiKeys.*;
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
    private JLabel appProgressStatusLabel;
    private JLabel appStatusProgressBarLabel;
    private JButton appStartButton;

    private JsonObjectServiceImpl jsonObjectUtil;
    private JsonObjectToFromContextTranslator translatorFrom;
    private JsonObjectToToContextTranslator translatorTo;
    private JsonObjectToAppContextTranslator translatorApp;

    private JTabbedPane fromTabbedPane;
    private JTabbedPane toTabbedPane;

    public AppController(JSONObject settings,
                         List<GUIFromController> fromControllerList,
                         List<GUIToController> toControllerList,
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

        this.translatorTo = new JsonObjectToToContextTranslator();
        this.translatorApp = new JsonObjectToAppContextTranslator();
        this.translatorFrom = new JsonObjectToFromContextTranslator();

        defineAppStartButtonListener(fromControllerList, toControllerList, fromTabbedPane, toTabbedPane);
        applySettings(settings);
    }

    @Override
    public void applySettings(JSONObject settings) {
        appSleepTimeTextField.setText(settings.optString(APP_SLEEP_TIME, "1"));
        fromTabbedPane.setSelectedIndex(settings.optInt(FROM_TAB_INDEX, 0));
        toTabbedPane.setSelectedIndex(settings.optInt(TO_TAB_INDEX, 0));
    }

    private void defineAppStartButtonListener(List<GUIFromController> fromControllerList, List<GUIToController> toControllerList, JTabbedPane fromTabbedPane, JTabbedPane toTabbedPane) {
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
            log.debug("Saving settings...");
            JSONObject allSettings = JsonObjectServiceImpl.getInstance().merge(jsonObjectFrom, jsonObjectTo, jsonObjectApp);
            SettingsServiceImpl.getInstance().save(allSettings);
            log.debug("Done.");
            RepositoryCloner repositoryCloner = RepositoryClonerImpl.getInstance();
            repositoryCloner.cloneAllRepositories(engineContext);
        });
    }

    private static <T extends StrategyGUIController> JSONObject retrieveJsonData(List<T> fromControllerList, int selectedIndex) {
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
        jsonObject.put(FROM_TAB_INDEX, fromTabbedPane.getSelectedIndex());
        jsonObject.put(TO_TAB_INDEX, toTabbedPane.getSelectedIndex());

        return jsonObject;
    }


}
