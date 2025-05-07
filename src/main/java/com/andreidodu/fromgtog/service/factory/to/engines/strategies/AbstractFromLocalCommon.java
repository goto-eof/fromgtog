package com.andreidodu.fromgtog.service.factory.to.engines.strategies;

public abstract class AbstractFromLocalCommon {

    protected String correctRepositoryName(String repositoryName) {
        return repositoryName
                .replaceAll(" ", "-")
                .replaceAll("[(]", "-")
                .replaceAll("[)]", "-")
                .toLowerCase();
    }

}
