package org.andreidodu.fromgtog.service;

public interface ScheduledService {
    void run(Runnable runnable);

    void shutdown();
}
