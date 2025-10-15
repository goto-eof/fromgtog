package com.andreidodu.fromgtog.service;

public interface ScheduledService {
    void run(Runnable runnable);

    void shutdown();
}
