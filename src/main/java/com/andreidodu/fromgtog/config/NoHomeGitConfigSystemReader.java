package com.andreidodu.fromgtog.config;

import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.SystemReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NoHomeGitConfigSystemReader extends SystemReader {

    public static synchronized void install() {
        SystemReader.setInstance(new NoHomeGitConfigSystemReader());
    }

    private File dummyFile;

    public NoHomeGitConfigSystemReader() {
        try {
//            System.setProperty("http.maxConnections", "50");
//            System.setProperty("http.maxTotalConnections", "50");
            dummyFile = File.createTempFile("jgit-dummy", ".config");
//            String configContent = String.format(
//                    "[http]\n\tmaxRequests = %d\n", 5
//            );
//            try (FileOutputStream fos = new FileOutputStream(dummyFile)) {
//                fos.write(configContent.getBytes(StandardCharsets.UTF_8));
//            }
            dummyFile.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create dummy config file", e);
        }
    }


    @Override
    public String getHostname() {
        return "localhost";
    }

    @Override
    public String getenv(String key) {
        return null;
    }

    @Override
    public String getProperty(String key) {
        return System.getProperty(key);
    }

    @Override
    public FileBasedConfig openUserConfig(Config parent, FS fs) {
        return createDummyConfig(fs);
    }

    @Override
    public FileBasedConfig openSystemConfig(Config parent, FS fs) {
        return createDummyConfig(fs);
    }

    @Override
    public FileBasedConfig openJGitConfig(Config parent, FS fs) {
        return createDummyConfig(fs);
    }

    private FileBasedConfig createDummyConfig(FS fs) {
        return new FileBasedConfig(dummyFile, fs) {
            @Override
            public void load() {
            }

            @Override
            public boolean isOutdated() {
                return false;
            }
        };
    }

    @Override
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    @Override
    public int getTimezone(long when) {
        return 0;
    }
}
