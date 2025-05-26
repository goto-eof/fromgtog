package com.andreidodu.fromgtog.gui.controller.impl;


import com.andreidodu.fromgtog.constants.SoundConstants;
import com.andreidodu.fromgtog.service.DeletableDestinationContentService;
import com.andreidodu.fromgtog.service.GitHubService;
import com.andreidodu.fromgtog.service.impl.GitHubServiceImpl;
import com.andreidodu.fromgtog.service.impl.GiteaServiceImpl;
import com.andreidodu.fromgtog.service.impl.GitlabServiceImpl;
import com.andreidodu.fromgtog.service.impl.SoundPlayer;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.util.ThreadUtil;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.util.function.Consumer;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.TERMINATOR_THREAD_NAME_PREFIX;

@Getter
@Setter
public class ToolsController {

    Logger log = LoggerFactory.getLogger(ToolsController.class);
    private JButton toolsDeleteALLGitHubRepositoriesButton;
    private JButton toolsDeleteALLGiteaRepositoriesButton;
    private JButton toolsDeleteALLGitlabRepositoriesButton;
    private Consumer<Boolean> setEnabledUI;

    public ToolsController(JButton toolsDeleteALLGitHubRepositoriesButton,
                           JButton toolsDeleteALLGiteaRepositoriesButton,
                           JButton toolsDeleteALLGitlabRepositoriesButton,
                           Consumer<Boolean> setEnabledUI) {
        this.toolsDeleteALLGitHubRepositoriesButton = toolsDeleteALLGitHubRepositoriesButton;
        this.toolsDeleteALLGiteaRepositoriesButton = toolsDeleteALLGiteaRepositoriesButton;
        this.toolsDeleteALLGitlabRepositoriesButton = toolsDeleteALLGitlabRepositoriesButton;
        this.setEnabledUI = setEnabledUI;
        addDeleteGiteaRepositoriesButtonListener();
        addDeleteGithubRepositoriesButtonListener();
        addDeleteGitlabRepositoriesButtonListener();
    }


    public void addDeleteGiteaRepositoriesButtonListener() {
        this.toolsDeleteALLGiteaRepositoriesButton.addActionListener(e -> {
            startDeletionProcedure(GiteaServiceImpl.getInstance(), EngineType.GITEA);
        });
    }

    public void addDeleteGitlabRepositoriesButtonListener() {
        this.toolsDeleteALLGitlabRepositoriesButton.addActionListener(e -> {
            startDeletionProcedure(GitlabServiceImpl.getInstance(), EngineType.GITLAB);
        });
    }


    public <ServiceType extends DeletableDestinationContentService> void startDeletionProcedure(ServiceType service, EngineType engineType) {
        String giteaUrl = JOptionPane.showInputDialog(null, "Enter " + engineType + " URL:", engineType + " URL", JOptionPane.QUESTION_MESSAGE);
        if (giteaUrl == null || giteaUrl.isEmpty()) {
            return;
        }

        String giteaToken = JOptionPane.showInputDialog(null, "Enter the " + engineType + " Token:", engineType + " Token", JOptionPane.QUESTION_MESSAGE);
        if (giteaToken == null || giteaToken.isEmpty()) {
            return;
        }

        String areYouSure = JOptionPane.showInputDialog(null, "Are you sure that you want to delete all the repositories on " + engineType + " (yes/no):", "Are you sure?", JOptionPane.QUESTION_MESSAGE);
        if (areYouSure == null || !areYouSure.equalsIgnoreCase("yes")) {
            return;
        }


        ThreadUtil.getInstance().executeOnSeparateThread(TERMINATOR_THREAD_NAME_PREFIX + "-" + engineType, () -> {
            try {
                this.setEnabledUI.accept(false);
                service.deleteAllRepositories(giteaUrl, giteaToken);
                showRepositoriesDeletedSuccessfullyMessage(engineType);
                this.setEnabledUI.accept(true);
            } catch (Exception e) {
                showFailedDeleteRepositoriesMessage(engineType);
                this.setEnabledUI.accept(true);
            }
        });

    }

    private static void showFailedDeleteRepositoriesMessage(EngineType engineType) {
        SoundPlayer.getInstance().play(SoundConstants.KEY_ERROR);
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Something wen wrong. Unable to delete " + engineType + " repositories.", "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private static void showRepositoriesDeletedSuccessfullyMessage(EngineType engineType) {
        SoundPlayer.getInstance().play(SoundConstants.KEY_SUCCESS);
        showInfoMessage("All " + engineType + " repositories were deleted!", "Info");
    }


    private static void showInfoMessage(String message, String title) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void addDeleteGithubRepositoriesButtonListener() {
        this.toolsDeleteALLGitHubRepositoriesButton.addActionListener(e -> {
            String gitHubToken = JOptionPane.showInputDialog(null, "Enter the GitHub Token:", "Gitea Token", JOptionPane.QUESTION_MESSAGE);
            if (gitHubToken == null || gitHubToken.isEmpty()) {
                return;
            }

            String areYouSure = JOptionPane.showInputDialog(null, "Are you sure that you want to delete all the repositories on GitHub (yes/no):", "Are you sure?", JOptionPane.QUESTION_MESSAGE);
            if (areYouSure == null || !areYouSure.equalsIgnoreCase("yes")) {
                return;
            }

            this.setEnabledUI.accept(false);
            GitHubService gitHubService = GitHubServiceImpl.getInstance();
            ThreadUtil.getInstance().executeOnSeparateThread(TERMINATOR_THREAD_NAME_PREFIX + "-GITHUB", () -> {
                try {
                    gitHubService.deleteAllRepositories(gitHubToken);
                    showRepositoriesDeletedSuccessfullyMessage(EngineType.GITHUB);
                    this.setEnabledUI.accept(true);
                } catch (Exception ee) {
                    this.setEnabledUI.accept(true);
                    showFailedDeleteRepositoriesMessage(EngineType.GITHUB);
                }
            });

        });
    }
}
