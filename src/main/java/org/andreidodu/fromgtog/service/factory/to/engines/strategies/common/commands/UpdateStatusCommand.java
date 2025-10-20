package org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands;

import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.Command;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.StatusCommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UpdateStatusCommand implements Command<Void> {
    final static Logger log = LoggerFactory.getLogger(UpdateStatusCommand.class);
    private final StatusCommandContext statusCommandContext;

    public UpdateStatusCommand(StatusCommandContext statusCommandContext) {
        this.statusCommandContext = statusCommandContext;
    }

    @Override
    public Void execute() {

        Optional.ofNullable(statusCommandContext.progressMaxValue())
                .ifPresent(maxIndex -> statusCommandContext.updateProgressMaxConsumer().accept(maxIndex));

        Optional.ofNullable(statusCommandContext.progressCurrentValue())
                .ifPresent(currentIndex -> statusCommandContext.updateProgressCurrentConsumer().accept(currentIndex));

        Optional.ofNullable(statusCommandContext.messageStatus())
                .ifPresent(message -> statusCommandContext.updateStatusMessageConsumer().accept(message));

        return null;
    }
}
