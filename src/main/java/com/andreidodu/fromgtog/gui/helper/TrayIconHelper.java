package com.andreidodu.fromgtog.gui.helper;

import com.andreidodu.fromgtog.util.OsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class TrayIconHelper {
    public static final String TRAY_ICON_IMAGE = "/images/xm/icon-64x64.png";
    private TrayIcon trayIcon;
    final static Logger log = LoggerFactory.getLogger(TrayIconHelper.class);

    public TrayIconHelper(JFrame mainWindow) {
        try {

            SystemTray tray = SystemTray.getSystemTray();
            Image image = loadImage(TRAY_ICON_IMAGE, getTrayIconSize(tray));
            if (image == null) {
                image = createFallbackImage();
            }

            BuildTrayIconMenu result = buildTrayIconMenu(mainWindow, tray);
            trayIcon = new TrayIcon(image, "FromGtoG", result.popup());

            completeTrayIcon(mainWindow, tray);

            addWindowListeners(mainWindow);

            mainWindow.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

            mainWindow.setVisible(true);
        } catch (AWTException ex) {
            log.error("Something went wrong", ex);
            mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }

    }

    private void addWindowListeners(JFrame mainWindow) {
        mainWindow.addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent e) {
                SwingUtilities.invokeLater(() -> hideWindow(mainWindow));
            }
        });
    }

    private static Dimension getTrayIconSize(SystemTray tray) {
        if (OsUtil.isLinux()) {
            return new Dimension(16, 16);
        }
        return tray.getTrayIconSize();
    }

    private void hideWindow(JFrame mainWindow) {
        if (trayIcon != null) {
            mainWindow.setVisible(false);
        } else {
            System.exit(0);
        }
    }

    private void completeTrayIcon(JFrame mainWindow, SystemTray tray) throws AWTException {
        trayIcon.setImageAutoSize(false);
        trayIcon.addActionListener((e) -> SwingUtilities.invokeLater(() -> showWindow(mainWindow)));
        tray.add(trayIcon);
    }

    private BuildTrayIconMenu buildTrayIconMenu(JFrame mainWindow, SystemTray tray) {
        PopupMenu popup = new PopupMenu();

        MenuItem restoreItem = new MenuItem("Open");
        restoreItem.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> showWindow(mainWindow));
        });
        popup.add(restoreItem);

        MenuItem showMsg = new MenuItem("About");
        showMsg.addActionListener(e -> displayInfo("FromGtoG", "FromGtoG 8.1.16"));
        popup.add(showMsg);

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                tray.remove(trayIcon);
                System.exit(0);
            });
        });
        popup.addSeparator();
        popup.add(exitItem);
        BuildTrayIconMenu result = new BuildTrayIconMenu(popup, restoreItem);
        return result;
    }

    private static void showWindow(JFrame mainWindow) {
        mainWindow.setVisible(true);
        mainWindow.setExtendedState(JFrame.NORMAL);
        mainWindow.toFront();
    }

    private record BuildTrayIconMenu(PopupMenu popup, MenuItem restoreItem) {
    }

    private void displayInfo(String caption, String text) {
        if (trayIcon != null) {
            trayIcon.displayMessage(caption, text, TrayIcon.MessageType.INFO);
        }
    }

    private static Image loadImage(String resourcePath, Dimension size) {
        try {
            log.info("Loading image from {}", resourcePath);
            log.info("size: {}x{}", size.width, size.height);
            Image image = ImageIO.read(Objects.requireNonNull(TrayIconHelper.class.getResourceAsStream(resourcePath)));
            return image.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            return null;
        }
    }

    private static Image createFallbackImage() {
        int size = 16;
        Image img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setColor(Color.GREEN);
        g.fillOval(0, 0, size, size);
        g.dispose();
        return img;
    }
}
