package com.andreidodu.fromgtog.service;

public interface JobService {
    void run(Runnable runnable);

    void shutdown();
}
