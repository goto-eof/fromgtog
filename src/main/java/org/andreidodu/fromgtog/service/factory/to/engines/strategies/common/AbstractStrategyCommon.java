package org.andreidodu.fromgtog.service.factory.to.engines.strategies.common;

import org.andreidodu.fromgtog.dto.CallbackContainer;

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

    protected void incrementIndexSuccess(CallbackContainer callbackContainer) {
        callbackContainer.updateApplicationProgressBarCurrent().accept(this.getIndex());
        this.incrementIndex();
    }

    protected String calculateStatus(final int total) {
        return getIndex() < total ? " - with errors (check log file)" : "!";
    }

    protected String correctRepositoryName(String repositoryName) {
        return repositoryName
                .replaceAll(" ", "-")
                .replaceAll("[(]", "-")
                .replaceAll("[)]", "-")
                .toLowerCase();
    }
}
