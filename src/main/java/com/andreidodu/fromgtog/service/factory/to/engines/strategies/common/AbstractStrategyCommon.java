package com.andreidodu.fromgtog.service.factory.to.engines.strategies.common;

public class AbstractStrategyCommon {
    private int index = 0;

    protected synchronized void resetIndex() {
        this.index = 0;
    }

    protected int getIndex() {
        return index;
    }

    protected synchronized void incrementIndex() {
        index++;
    }

    protected String correctRepositoryName(String repositoryName) {
        return repositoryName
                .replaceAll(" ", "-")
                .replaceAll("[(]", "-")
                .replaceAll("[)]", "-")
                .toLowerCase();
    }
}
