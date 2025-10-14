package com.andreidodu.fromgtog.service;

public interface JobService {
    void runTicTak(Runnable runnable);

    void shutdown();
}
