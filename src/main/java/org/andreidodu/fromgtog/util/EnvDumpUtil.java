package org.andreidodu.fromgtog.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EnvDumpUtil {
    private static final Logger log = LoggerFactory.getLogger(EnvDumpUtil.class);

    public static void logEnvironment() {
        log.debug("=== START Environment Variables (sorted) ===");
        System.getenv().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        log.debug("{}={}", entry.getKey(), entry.getValue())
                );
        log.debug("=== END Environment Variables (sorted) ===");
    }
}
