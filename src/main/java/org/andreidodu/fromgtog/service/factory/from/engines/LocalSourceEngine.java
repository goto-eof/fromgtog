package org.andreidodu.fromgtog.service.factory.from.engines;

import org.andreidodu.fromgtog.dto.EngineContext;
import org.andreidodu.fromgtog.dto.FromContext;
import org.andreidodu.fromgtog.dto.RepositoryDTO;
import org.andreidodu.fromgtog.exception.CloningSourceException;
import org.andreidodu.fromgtog.service.LocalService;
import org.andreidodu.fromgtog.service.factory.from.AbstractSourceEngine;
import org.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import org.andreidodu.fromgtog.type.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LocalSourceEngine extends AbstractSourceEngine {
    private final static EngineType SOURCE_ENGINE_TYPE = EngineType.LOCAL;
    Logger log = LoggerFactory.getLogger(LocalSourceEngine.class);

    @Override
    public EngineType getEngineType() {
        return SOURCE_ENGINE_TYPE;
    }

    @Override
    public List<RepositoryDTO> retrieveRepositoryList(EngineContext engineContext) {
        FromContext fromContext = engineContext.fromContext();
        LocalService localService = LocalServiceImpl.getInstance();

        Path path = Paths.get(fromContext.rootPath());
        log.debug("source path: {}", path);
        if (Files.notExists(path) || !Files.isDirectory(path)) {
            throw new CloningSourceException("source directory \"" + fromContext.rootPath() + "\" does not exists");
        }

        return localService.listAllRepos(fromContext.rootPath())
                .stream()
                .map(repoPath -> RepositoryDTO.builder().path(repoPath).build())
                .toList();
    }
}
