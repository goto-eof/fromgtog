package org.andreidodu.fromgtog.service.impl;


import org.andreidodu.fromgtog.gui.ApplicationGUI;
import org.andreidodu.fromgtog.service.GuiStarterService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class GuiStarterServiceImpl implements GuiStarterService {

    public static final String THEME_METAL_JAVA_IMPROVED = "Metal";
    public static final String THEME_CDE_MOTIF_WINDOWS_95 = "CDE/Motif";
    public static final String THEME_GTK_PLUS = "GTK+";
    public static final String THEME_NIMBUS = "Nimbus";
    public static final List<String> knownThemesLowerCase = List.of(
            THEME_NIMBUS.toLowerCase(),
            THEME_GTK_PLUS.toLowerCase(),
            THEME_CDE_MOTIF_WINDOWS_95.toLowerCase(),
            THEME_METAL_JAVA_IMPROVED.toLowerCase()
    );
    private static final Logger log = LoggerFactory.getLogger(GuiStarterServiceImpl.class);

    public static void applyTheme(String themeName) {
        UIManager.LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
        System.out.println("Applying theme: " + themeName);
        Arrays.stream(installedLookAndFeels)
                .filter(theme -> theme.getName().equalsIgnoreCase(themeName))
                .findFirst()
                .ifPresent(theme -> {
                    try {
                        UIManager.setLookAndFeel(theme.getClassName());
                    } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException |
                             InstantiationException e) {
                        log.error("Failed to apply theme: {}", themeName);
                        throw new RuntimeException(e);
                    }
                });
        System.out.println("Applied theme: " + themeName);
    }

    public static void applyTheme(Optional<UIManager.LookAndFeelInfo> gtkPlusThemeOptional) {
        UIManager.LookAndFeelInfo lookAndFeelInfo = gtkPlusThemeOptional.orElseThrow();
        try {
            System.out.println("Applying theme: " + lookAndFeelInfo.getName());
            UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
            System.out.println("Applied theme: " + lookAndFeelInfo.getName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException |
                 InstantiationException e) {
            log.error("Failed to apply theme: {}", lookAndFeelInfo.getName());
            throw new RuntimeException(e);
        }
    }

    private static void applyDefaultTheme(UIManager.LookAndFeelInfo[] installedLookAndFeels, JSONObject settings) {

        Optional<UIManager.LookAndFeelInfo> themeOptional = null;

        themeOptional = findTheme(installedLookAndFeels, THEME_GTK_PLUS);
        if (themeOptional.isPresent()) {
            applyTheme(themeOptional);
            updateSettings(settings, THEME_GTK_PLUS);
            return;
        }

        themeOptional = findTheme(installedLookAndFeels, THEME_NIMBUS);
        if (themeOptional.isPresent()) {
            applyTheme(themeOptional);
            updateSettings(settings, THEME_NIMBUS);
            return;
        }

        themeOptional = findTheme(installedLookAndFeels, THEME_METAL_JAVA_IMPROVED);
        if (themeOptional.isPresent()) {
            applyTheme(themeOptional);
            updateSettings(settings, THEME_METAL_JAVA_IMPROVED);
            return;
        }

        themeOptional = findTheme(installedLookAndFeels, THEME_CDE_MOTIF_WINDOWS_95);
        if (themeOptional.isPresent()) {
            applyTheme(themeOptional);
            updateSettings(settings, THEME_CDE_MOTIF_WINDOWS_95);
            return;
        }

        themeOptional = Arrays.stream(installedLookAndFeels).findFirst();
        if (themeOptional.isPresent()) {
            applyTheme(themeOptional);
            updateSettings(settings, themeOptional.get().getName());
        }
    }

    private static void updateSettings(JSONObject jsonObject, String themeName) {
        jsonObject.put("theme", themeName);
        SettingsServiceImpl.getInstance().save(jsonObject);
    }

    private static Optional<UIManager.LookAndFeelInfo> findTheme(UIManager.LookAndFeelInfo[] installedLookAndFeels, String themeName) {
        return Arrays.stream(installedLookAndFeels).filter(info -> themeName.equalsIgnoreCase(info.getName())).findFirst();
    }

    @Override
    public void start() {
        com.formdev.flatlaf.FlatDarkLaf.setup();
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarculaLaf());
        } catch (Exception e) {
            log.error("Failed to apply theme: {}", "Dracula");
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        applyPreferredTheme();
                    } catch (Exception e) {
                        log.error("GUI error: {}", e.getMessage());
                        tryToApplyDefaultTheme();
                    }
                }
            });
        } finally {
            new ApplicationGUI();
        }
    }

    private void tryToApplyDefaultTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ex) {
            log.error("GUI error: {}", ex.getMessage());
        }
    }

    private List<String> getSystemThemeNamesLowerCase(UIManager.LookAndFeelInfo[] installedLookAndFeels) {
        return Arrays.stream(installedLookAndFeels)
                .map(UIManager.LookAndFeelInfo::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private void applyPreferredTheme() {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarculaLaf());
        } catch (Exception e) {
            log.error("Failed to apply theme: {}", e.getMessage());
            applyDefault();
        }


    }

    private void applyDefault() {
        UIManager.LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
        JSONObject settings = SettingsServiceImpl.getInstance().load();
        Optional.ofNullable(settings.optString("theme"))
                .filter(themeName -> !themeName.isEmpty())
                .filter(theme -> getSystemThemeNamesLowerCase(installedLookAndFeels).contains(theme.toLowerCase()))
                .ifPresentOrElse(GuiStarterServiceImpl::applyTheme, () -> applyDefaultTheme(installedLookAndFeels, settings));
    }

}
