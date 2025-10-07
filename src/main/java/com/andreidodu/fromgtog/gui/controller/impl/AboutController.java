package com.andreidodu.fromgtog.gui.controller.impl;


import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;

@Getter
@Setter
public class AboutController {

    public static final String URL_BUY_ME_COFFEE = "https://github.com/sponsors/goto-eof";
    public static final String URL_PROJECT_WEBSITE = "https://github.com/goto-eof/fromgtog";
    public static final String URL_REPORT_ISSUE = "https://github.com/goto-eof/fromgtog/issues";
    public static final String URL_CONTACT_ME = "https://andre-i.eu/#contactme";

    Logger log = LoggerFactory.getLogger(AboutController.class);
    private JButton projectWebsiteButton;
    private JButton reportAnIssueButton;
    private JButton buyMeACoffeeButton;
    private JButton contactMe;

    public AboutController(JButton projectWebsiteButton,
                           JButton reportAnIssueButton,
                           JButton buyMeACoffeeButton,
                           JButton contactMe) {
        this.projectWebsiteButton = projectWebsiteButton;
        this.reportAnIssueButton = reportAnIssueButton;
        this.buyMeACoffeeButton = buyMeACoffeeButton;
        this.contactMe = contactMe;
        addProjectWebsiteButtonListener();
        addReportAnIssueButtonListener();
        addContactMeButtonListener();
        addBuyMeACoffeeButtonListener();
    }


    public void addProjectWebsiteButtonListener() {
        this.projectWebsiteButton.addActionListener(e -> {
            tryToOpenUrlCrossPlatform(URL_PROJECT_WEBSITE);
        });
    }

    private void tryToOpenUrlCrossPlatform(String url) {
        try {
            // Ubuntu
            openBrowser("xdg-open", url);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            try {
                // Windows
                openBrowser("explorer", url);
            } catch (Exception ex1) {
                log.error(ex1.getMessage());
                log.error(ex.getMessage());
                try {
                    // MacOS
                    openBrowser("open", url);
                } catch (Exception ex2) {
                    log.error(ex1.getMessage());
                }
            }
        }
    }

    public void addReportAnIssueButtonListener() {
        this.reportAnIssueButton.addActionListener(e -> {
            tryToOpenUrlCrossPlatform(URL_REPORT_ISSUE);
        });
    }

    public void addContactMeButtonListener() {
        this.contactMe.addActionListener(e -> {
            tryToOpenUrlCrossPlatform(URL_CONTACT_ME);
        });
    }


    public void addBuyMeACoffeeButtonListener() {
        this.buyMeACoffeeButton.addActionListener(e -> {
            tryToOpenUrlCrossPlatform(URL_BUY_ME_COFFEE);

        });
    }

    private void openBrowser(String command, String url) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command, url);
            pb.inheritIO();
            pb.start();
        } catch (IOException ee) {
            log.error("failed to open link: {}", ee.getMessage());
            throw new RuntimeException("failed to open link: " + ee.getMessage());
        }
    }

}
