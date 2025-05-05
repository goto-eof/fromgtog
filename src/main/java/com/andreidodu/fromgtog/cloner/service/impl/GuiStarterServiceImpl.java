package com.andreidodu.fromgtog.cloner.service.impl;


import com.andreidodu.fromgtog.cloner.gui.ApplicationGUI;
import com.andreidodu.fromgtog.cloner.service.GuiStarterService;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    @Override
    public void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    applyPreferredTheme();
                } catch (Exception e) {
                    System.err.println("GUI error: " + e.getMessage());
                    tryToApplyDefaultTheme();
                }
                new ApplicationGUI();
            }
        });
    }

    private void tryToApplyDefaultTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("GUI error: " + ex.getMessage());
        }
    }


    private List<String> getSystemThemeNamesLowerCase(UIManager.LookAndFeelInfo[] installedLookAndFeels) {
        return Arrays.stream(installedLookAndFeels)
                .map(UIManager.LookAndFeelInfo::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private void applyPreferredTheme() {
        UIManager.LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
        Map<String, String> settings = new SettingsServiceImpl().load();
        Optional.ofNullable(settings.get("theme"))
                .filter(themeName -> !themeName.isEmpty())
                .filter(theme -> getSystemThemeNamesLowerCase(installedLookAndFeels).contains(theme.toLowerCase()))
                .ifPresentOrElse(GuiStarterServiceImpl::applyTheme, () -> applyDefaultTheme(installedLookAndFeels, settings));
    }

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
            throw new RuntimeException(e);
        }
    }

    private static void applyDefaultTheme(UIManager.LookAndFeelInfo[] installedLookAndFeels, Map<String, String> settings) {

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

    private static void updateSettings(Map<String, String> settings, String themeName) {
        settings.put("theme", themeName);
        new SettingsServiceImpl().save(settings);
    }


    private static Optional<UIManager.LookAndFeelInfo> findTheme(UIManager.LookAndFeelInfo[] installedLookAndFeels, String themeName) {
        return Arrays.stream(installedLookAndFeels).filter(info -> themeName.equalsIgnoreCase(info.getName())).findFirst();
    }

}
