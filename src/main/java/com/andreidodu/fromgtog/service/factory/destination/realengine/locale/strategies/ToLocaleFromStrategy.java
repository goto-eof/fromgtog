package com.andreidodu.fromgtog.service.factory.destination.realengine.locale.strategies;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.dto.RepositoryDTO;
import com.andreidodu.fromgtog.type.EngineType;

import java.util.List;

public interface ToLocaleFromStrategy {

    public boolean accept(EngineType engineType);

    boolean cloneAll(EngineContext engineContext, List<RepositoryDTO> repositoryDTOList);
}
