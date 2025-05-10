package com.andreidodu.fromgtog.service.factory.from.engines;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.FromContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.exception.CloningSourceException;
import com.andreidodu.fromgtog.service.LocalService;
import com.andreidodu.fromgtog.service.factory.from.AbstractSourceEngine;
import com.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LocalSourceEngine extends AbstractSourceEngine {
    Logger log = LoggerFactory.getLogger(LocalSourceEngine.class);
    private final static EngineType SOURCE_ENGINE_TYPE = EngineType.LOCAL;

    @Override
    public EngineType getEngineType() {
        return SOURCE_ENGINE_TYPE;
    }

    @Override
    public List<RepositoryDTO> retrieveRepositoryList(EngineContext engineContext) {
        FromContext fromContext = engineContext.fromContext();
        LocalService localService = LocalServiceImpl.getInstance();

        Path path = Paths.get(fromContext.rootPath());
        log.debug("source path: {}", path.toString());
        if (Files.notExists(path) || !Files.isDirectory(path)) {
            throw new CloningSourceException("source directory \"" + fromContext.rootPath() + "\" does not exists");
        }

        return localService.listAllRepos(fromContext.rootPath())
                .stream()
                .map(repoPath -> RepositoryDTO.builder().path(repoPath).build())
                .toList();
    }
}
