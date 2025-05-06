package com.andreidodu.fromgtog.service.factory.source.realengine;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.FromContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.dto.gitea.GiteaRepositoryDTO;
import com.andreidodu.fromgtog.mapper.GiteaRepositoryMapper;
import com.andreidodu.fromgtog.service.GiteaService;
import com.andreidodu.fromgtog.service.factory.source.AbstractSourceEngine;
import com.andreidodu.fromgtog.service.impl.GiteaServiceImpl;
import com.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public class GiteaSourceEngine extends AbstractSourceEngine {

    private final static EngineType SOURCE_ENGINE_TYPE = EngineType.GITEA;

    @Override
    public EngineType getEngineType() {
        return SOURCE_ENGINE_TYPE;
    }

    @Override
    public List<RepositoryDTO> retrieveRepositoryList(EngineContext engineContext) {
        GiteaService giteaService = new GiteaServiceImpl();
        FromContext fromContext = engineContext.fromContext();
        List<GiteaRepositoryDTO> giteaRepositoryDTOList = giteaService.tryToRetrieveUserRepositories(fromContext.url(), fromContext.token());


        GiteaRepositoryMapper mapper = new GiteaRepositoryMapper();
        return giteaRepositoryDTOList.stream()
                .map(mapper::toDTO)
                .toList();
    }
}
