package com.andreidodu.fromgtog.gui.helper;

import com.andreidodu.fromgtog.util.OsUtil;
import dorkbox.systemTray.SystemTray;
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
    public static final String TRAY_ICON_IMAGE_SECONDARY = "/images/xm/icon-red-64x64.png";

    private SystemTray systemTray;
    final static Logger log = LoggerFactory.getLogger(TrayIconHelper.class);
    private volatile String currentTrayIconImageFile = TRAY_ICON_IMAGE;

    public TrayIconHelper(JFrame mainWindow, final boolean isWindowVisible) {
        try {
            this.systemTray = SystemTray.get();

            SystemTray tray = SystemTray.get();
            Image image = loadImage(TRAY_ICON_IMAGE, getTrayIconSize(tray));
            if (image == null) {
                image = createFallbackImage();
            }

            BuildTrayIconMenu result = buildTrayIconMenu(mainWindow);
            completeTrayIcon(image, result.popup());

            addWindowListeners(mainWindow);

            mainWindow.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            mainWindow.setVisible(isWindowVisible);
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

    private static int getTrayIconSize(SystemTray tray) {
        if (OsUtil.isLinux()) {
            return 16;
        }
        return tray.getTrayImageSize();
    }

    private void hideWindow(JFrame mainWindow) {
        mainWindow.setVisible(false);
    }

    private void completeTrayIcon(Image image, JMenu popupMenu) throws AWTException {
        systemTray.setImage(image);
        systemTray.setMenu(popupMenu);
    }

    private BuildTrayIconMenu buildTrayIconMenu(JFrame mainWindow) {
        JMenu popup = new JMenu("FromGtoG");

        JMenuItem restoreItem = new JMenuItem("Open");
        restoreItem.addActionListener(e -> SwingUtilities.invokeLater(() -> showWindow(mainWindow)));
        popup.add(restoreItem);

        JMenuItem showMsg = new JMenuItem("About");
        showMsg.addActionListener(e -> displayInfo("FromGtoG", "FromGtoG 9.1.0 by Andrei Dodu"));
        popup.add(showMsg);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> System.exit(0));
        });
        popup.addSeparator();
        popup.add(exitItem);
        return new BuildTrayIconMenu(popup, restoreItem);
    }

    private static void showWindow(JFrame mainWindow) {
        mainWindow.setVisible(true);
        mainWindow.setExtendedState(JFrame.NORMAL);
        mainWindow.toFront();
    }

    private record BuildTrayIconMenu(JMenu popup, JMenuItem restoreItem) {
    }

    private void displayInfo(String caption, String text) {
        JOptionPane.showMessageDialog(
                null,
                text,
                caption,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static Image loadImage(String resourcePath, int size) {
        try {
            log.debug("Loading image from {}", resourcePath);
            log.debug("size: {}x{}", size, size);
            Image image = ImageIO.read(Objects.requireNonNull(TrayIconHelper.class.getResourceAsStream(resourcePath)));
            return image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            return null;
        }
    }

    public synchronized void toggleIcon(boolean loadDefault) {
        if (loadDefault) {
            currentTrayIconImageFile = TRAY_ICON_IMAGE;
            loadAndSetImage();
            return;
        }

        if (!TRAY_ICON_IMAGE_SECONDARY.equals(currentTrayIconImageFile)) {
            currentTrayIconImageFile = TRAY_ICON_IMAGE_SECONDARY;
        } else if (!TRAY_ICON_IMAGE.equals(currentTrayIconImageFile)) {
            currentTrayIconImageFile = TRAY_ICON_IMAGE;
        }

        loadAndSetImage();
    }

    private void loadAndSetImage() {
        Image image = loadImage(currentTrayIconImageFile);
        systemTray.setImage(image);
    }

    private static Image loadImage(final String imgFile) {
        SystemTray tray = SystemTray.get();
        Image image = loadImage(imgFile, getTrayIconSize(tray));
        if (image == null) {
            image = createFallbackImage();
        }
        return image;
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
