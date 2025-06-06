package com.andreidodu.fromgtog.service.factory.to.engines.strategies.common;

import com.andreidodu.fromgtog.dto.CallbackContainer;

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

    protected void completeTask(CallbackContainer callbackContainer) {
        callbackContainer.updateApplicationProgressBarCurrent().accept(this.getIndex());
        this.incrementIndex();
    }

    protected String correctRepositoryName(String repositoryName) {
        return repositoryName
                .replaceAll(" ", "-")
                .replaceAll("[(]", "-")
                .replaceAll("[)]", "-")
                .toLowerCase();
    }
}
