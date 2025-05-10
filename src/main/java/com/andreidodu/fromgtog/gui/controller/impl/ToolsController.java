package com.andreidodu.fromgtog.gui.controller.impl;


import com.andreidodu.fromgtog.service.DeletableDestinationContentService;
import com.andreidodu.fromgtog.service.GitHubService;
import com.andreidodu.fromgtog.service.impl.GitHubServiceImpl;
import com.andreidodu.fromgtog.service.impl.GiteaServiceImpl;
import com.andreidodu.fromgtog.service.impl.GitlabServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.util.ThreadUtil;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;

@Getter
@Setter
public class ToolsController {

    Logger log = LoggerFactory.getLogger(ToolsController.class);
    private JButton toolsDeleteALLGitHubRepositoriesButton;
    private JButton toolsDeleteALLGiteaRepositoriesButton;
    private JButton toolsDeleteALLGitlabRepositoriesButton;

    public ToolsController(JButton toolsDeleteALLGitHubRepositoriesButton,
                           JButton toolsDeleteALLGiteaRepositoriesButton,
                           JButton toolsDeleteALLGitlabRepositoriesButton) {
        this.toolsDeleteALLGitHubRepositoriesButton = toolsDeleteALLGitHubRepositoriesButton;
        this.toolsDeleteALLGiteaRepositoriesButton = toolsDeleteALLGiteaRepositoriesButton;
        this.toolsDeleteALLGitlabRepositoriesButton = toolsDeleteALLGitlabRepositoriesButton;
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


        ThreadUtil.executeOnSeparateThread(() -> {
            try {
                service.deleteAllRepositories(giteaUrl, giteaToken);
                showRepositoriesDeletedSuccessfullyMessage(engineType);
            } catch (Exception e) {
                showFailedDeleteRepositoriesMessage(engineType);
            }
        });

    }

    private static void showFailedDeleteRepositoriesMessage(EngineType engineType) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Something wen wrong. Unable to delete " + engineType + " repositories.", "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private static void showRepositoriesDeletedSuccessfullyMessage(EngineType engineType) {
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

            GitHubService gitHubService = GitHubServiceImpl.getInstance();
            ThreadUtil.executeOnSeparateThread(() -> {
                try {
                    gitHubService.deleteAllRepositories(gitHubToken);
                    showRepositoriesDeletedSuccessfullyMessage(EngineType.GITHUB);
                } catch (Exception ee) {
                    showFailedDeleteRepositoriesMessage(EngineType.GITHUB);
                }
            });

        });
    }
}
