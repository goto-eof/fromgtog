package com.andreidodu.fromgtog.gui.controller.impl;


import com.andreidodu.fromgtog.dto.gitea.GiteaRepositoryDTO;
import com.andreidodu.fromgtog.service.GitHubService;
import com.andreidodu.fromgtog.service.GiteaService;
import com.andreidodu.fromgtog.service.impl.GitHubServiceImpl;
import com.andreidodu.fromgtog.service.impl.GiteaServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

@Getter
@Setter
public class ToolsController {

    Logger log = LoggerFactory.getLogger(ToolsController.class);
    private JButton toolsDeleteALLGitHubRepositoriesButton;
    private JButton toolsDeleteALLGiteaRepositoriesButton;

    public ToolsController(JButton toolsDeleteALLGitHubRepositoriesButton,
                           JButton toolsDeleteALLGiteaRepositoriesButton) {
        this.toolsDeleteALLGitHubRepositoriesButton = toolsDeleteALLGitHubRepositoriesButton;
        this.toolsDeleteALLGiteaRepositoriesButton = toolsDeleteALLGiteaRepositoriesButton;

        addDeleteGiteaRepositoriesButtonListener();
        addDeleteGithubRepositoriesButtonListener();
    }


    public void addDeleteGiteaRepositoriesButtonListener() {
        this.toolsDeleteALLGiteaRepositoriesButton.addActionListener(e -> {
            String giteaUrl = JOptionPane.showInputDialog(null, "Enter Gitea URL:", "Gitea URL", JOptionPane.QUESTION_MESSAGE);
            if (giteaUrl == null || giteaUrl.isEmpty()) {
                return;
            }

            String giteaToken = JOptionPane.showInputDialog(null, "Enter the Gitea Token:", "Gitea Token", JOptionPane.QUESTION_MESSAGE);
            if (giteaToken == null || giteaToken.isEmpty()) {
                return;
            }

            String areYouSure = JOptionPane.showInputDialog(null, "Are you sure that you want to delete all the repositories on Gitea (yes/no):", "Are you sure?", JOptionPane.QUESTION_MESSAGE);
            if (areYouSure == null || !areYouSure.equalsIgnoreCase("yes")) {
                return;
            }

            GiteaService giteaService = GiteaServiceImpl.getInstance();
            List<GiteaRepositoryDTO> giteaRepositoryDTOList = giteaService.tryToRetrieveUserRepositories(giteaUrl, giteaToken);
            giteaRepositoryDTOList.forEach(repositoryDTO -> {
                giteaService.deleteRepository(giteaUrl, giteaToken, repositoryDTO.getOwner().getLogin(), repositoryDTO.getName());
            });

            JOptionPane.showMessageDialog(null, "All gitea repositories were deleted!", "Info", JOptionPane.INFORMATION_MESSAGE);
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

            try {
                gitHubService.retrieveGitHubMyself(gitHubService.retrieveGitHubClient(gitHubToken))
                        .getAllRepositories()
                        .forEach((s, ghRepository) -> {
                            try {
                                ghRepository.delete();
                            } catch (IOException ee) {
                                throw new RuntimeException(ee);
                            }
                        });
            } catch (Exception eee) {
                JOptionPane.showMessageDialog(null, "Something went wrong!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(null, "All GitHub repositories were deleted!", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
