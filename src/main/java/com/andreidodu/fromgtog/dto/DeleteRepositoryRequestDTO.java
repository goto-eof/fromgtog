package com.andreidodu.fromgtog.dto;

import java.io.File;
import java.util.Optional;

public record DeleteRepositoryRequestDTO(
        Optional<String> token,
        Optional<String> baseUrl,
        Optional<String> owner,
        Optional<String> repoName,
        Optional<File> localPath
) {
}
