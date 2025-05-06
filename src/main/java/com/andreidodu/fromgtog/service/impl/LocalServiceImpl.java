package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.service.LocalService;
import com.andreidodu.fromgtog.service.factory.destination.realengine.locale.strategies.ToLocaleFromRemoteStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class LocalServiceImpl implements LocalService {
    Logger log = LoggerFactory.getLogger(LocalServiceImpl.class);

    private static LocalService instance;

    public static LocalService getInstance() {
        if (instance == null) {
            instance = new LocalServiceImpl();
        }
        return instance;
    }

    @Override
    public List<String> listAllRepos(final String rootPath) {
        Objects.requireNonNull(rootPath);
        List<String> repos = new ArrayList<>();
        findGitRepositoriesExcursively(rootPath, repos);
        return repos;
    }

    private void findGitRepositoriesExcursively(String path, List<String> repositories) {
        File pathFile = getFile(path);
        log.debug("findGitRepositoriesExcursively {}", pathFile);
        List<String> foundRepositories = Arrays.stream(
                        Optional.ofNullable(pathFile.listFiles())
                                .orElseGet(() -> new File[0])
                )
                .peek(file -> {
                    log.debug(file.getAbsolutePath());
                })
                .filter(File::isDirectory)
                .map(File::getAbsolutePath)
                .toList();
        repositories.addAll(
                foundRepositories.stream()
                        .map(File::new)
                        .filter(this::isGitRepository)
                        .map(File::getAbsolutePath)
                        .toList()
        );
        foundRepositories.forEach(repoPath -> findGitRepositoriesExcursively(repoPath, repositories));
    }

    private boolean isGitRepository(File path) {
        File gitDir = new File(path, ".git");
        return gitDir.exists() && gitDir.isDirectory();
    }

    private static File getFile(String pathString) {
        return new File(pathString);
    }

}
