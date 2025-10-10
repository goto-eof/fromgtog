package com.andreidodu.fromgtog.service.factory.from;

import com.andreidodu.fromgtog.type.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public abstract class AbstractSourceEngine implements SourceEngine {
    private static final Logger log = LoggerFactory.getLogger(AbstractSourceEngine.class);

    @Override
    public boolean accept(EngineType sourceEngineType) {
        return getEngineType().equals(sourceEngineType);
    }


    protected List<String> fileContentToList(String filename) {
        Path filePath = Paths.get(filename);

        try {
            return Files.readAllLines(filePath, StandardCharsets.UTF_8)
                    .stream()
                    .map(str -> str.trim().toLowerCase())
                    .toList();
        } catch (IOException e) {
            log.error("An error occurred while reading the file: {}", e.getMessage());
            throw new RuntimeException("An error occurred while reading the file");
        }
    }

}
