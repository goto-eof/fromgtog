package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.constants.SoundConstants;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.andreidodu.fromgtog.constants.SoundConstants.*;

public class SoundPlayer {
    private final static Logger logger = LoggerFactory.getLogger(SoundPlayer.class);
    private static SoundPlayer instance;
    private final Map<String, ClipWrapper> clipsMap;
    private boolean mute;

    public SoundPlayer() {
        if (instance != null) {
            throw new IllegalStateException("SoundPlayer already initialized");
        }
        clipsMap = Map.of(
                KEY_SUCCESS, new ClipWrapper(loadClip(SoundConstants.SUCCESS)),
                KEY_ERROR, new ClipWrapper(loadClip(SoundConstants.ERROR))
        );
    }

    public synchronized static SoundPlayer getInstance() {
        if (instance == null) {
            tryToInstantiateClass();
        }
        return instance;
    }

    private static void tryToInstantiateClass() {
        try {
            instance = new SoundPlayer();
        } catch (Exception e) {
            logger.error("SoundPlayer failed to initialize", e);
            throw new RuntimeException(e);
        }
    }

    public void play(final String soundName) {
        Objects.requireNonNull(soundName);
        if (this.isMute()) {
            return;
        }
        ClipWrapper clipWrapper = clipsMap.get(soundName);
        if (Objects.isNull(clipWrapper)) {
            return;
        }
        tryToRestartSound(clipWrapper);
    }

    private void tryToRestartSound(final ClipWrapper clipWrapper) {
        try {
            restartSound(clipWrapper);
        } catch (Exception e) {
            logger.error("SoundPlayer failed to play hover sound", e);
        }
    }

    private void restartSound(final ClipWrapper clipWrapper) {
        if (clipWrapper.isClipPlaying()) {
            Optional.ofNullable(clipWrapper.getClip()).ifPresent(DataLine::stop);
        }

        clipWrapper.setIsClipPlaying(true);
        Optional.ofNullable(clipWrapper.getClip())
                .ifPresent(clip -> {
                    clip.setFramePosition(0);
                    clip.start();

                });
    }

    private Clip loadClip(String soundFilename) {
        try {
            InputStream soundURL = SoundPlayer.class.getResourceAsStream(soundFilename);
            if (soundURL == null) {
                logger.error("File not found: {}", soundFilename);
                return null;
            }
            logger.info("Loading file: {}", soundFilename);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            audioStream.close();
            return clip;
        } catch (RuntimeException | UnsupportedAudioFileException | IOException |
                 LineUnavailableException e) {
            logger.error("SoundPlayer failed to load clip", e);
        }
        return null;
    }

    public synchronized void destroy() {
        clipsMap.keySet().forEach(s -> {
            Clip clip = clipsMap.get(s).getClip();
            clip.stop();
            clip.close();
        });
        SoundPlayer.instance = null;
    }


    public class ClipWrapper {
        private final Clip clip;
        @Getter
        private boolean isClipPlaying;


        public ClipWrapper(Clip clip) {
            this.clip = clip;
            Optional.ofNullable(this.clip)
                    .ifPresent(o -> {
                        this.clip.addLineListener(myLineEvent -> {
                            if (SOUND_STOP_EVENTS.contains(myLineEvent.getType())) {
                                this.isClipPlaying = false;
                            }
                        });
                    });
        }

        public synchronized Clip getClip() {
            return clip;
        }

        public synchronized void setIsClipPlaying(boolean value) {
            this.isClipPlaying = value;
        }

    }

    public synchronized boolean isMute() {
        return mute;
    }

    public synchronized void setMute(boolean mute) {
        this.mute = mute;
    }

}
