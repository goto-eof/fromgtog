package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.service.LocalService;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LocalServiceImpl implements LocalService {

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
        File pathFile = getFile(rootPath);
        return Arrays.stream(Optional.ofNullable(pathFile.listFiles())
                        .orElseGet(() -> new File[0]))
                .filter(File::isDirectory)
                .filter(this::isGitRepository)
                .map(File::getAbsolutePath)
                .toList();
    }

    private boolean isGitRepository(File path) {
        File gitDir = new File(path, ".git");
        return gitDir.exists() && gitDir.isDirectory();
    }

    private static File getFile(String pathString) {
        return new File(pathString);
    }

}
