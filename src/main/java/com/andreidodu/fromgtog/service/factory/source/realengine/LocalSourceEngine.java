package com.andreidodu.fromgtog.service.factory.source.realengine;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.FromContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.service.factory.source.AbstractSourceEngine;
import com.andreidodu.fromgtog.service.LocalService;
import com.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;


import java.util.List;

public class LocalSourceEngine extends AbstractSourceEngine {

    private final static EngineType SOURCE_ENGINE_TYPE = EngineType.LOCAL;

    @Override
    public EngineType getEngineType() {
        return SOURCE_ENGINE_TYPE;
    }

    @Override
    public List<RepositoryDTO> retrieveRepositoryList(EngineContext engineContext) {
        FromContext contex = engineContext.fromContext();
        LocalService localService = LocalServiceImpl.getInstance();

        return localService.listAllRepos(contex.rootPath())
                .stream()
                .map(path -> RepositoryDTO.builder().path(path).build())
                .toList();
    }
}
