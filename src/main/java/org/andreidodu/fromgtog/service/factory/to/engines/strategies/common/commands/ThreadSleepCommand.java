package org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands;

import org.andreidodu.fromgtog.service.LocalService;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.Command;
import org.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadSleepCommand implements Command<Boolean> {
    final static Logger log = LoggerFactory.getLogger(ThreadSleepCommand.class);

    private final LocalService localService;
    private final int sleepTimeSeconds;

    public ThreadSleepCommand(int sleepTimeSeconds) {
        localService = LocalServiceImpl.getInstance();
        this.sleepTimeSeconds = sleepTimeSeconds;
    }

    @Override
    public Boolean execute() {
        try {
            log.debug("sleeping");
            Thread.sleep(sleepTimeSeconds * 1000L);
            return Boolean.TRUE;
        } catch (InterruptedException e) {
            return Boolean.FALSE;
        }
    }
}
