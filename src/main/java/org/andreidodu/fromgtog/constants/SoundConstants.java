package org.andreidodu.fromgtog.constants;

import javax.sound.sampled.LineEvent;
import java.util.List;

public interface SoundConstants {

    List<LineEvent.Type> SOUND_STOP_EVENTS = List.of(LineEvent.Type.STOP, LineEvent.Type.CLOSE);
    String SUCCESS = "sounds/success.wav";
    String ERROR = "sounds/error.wav";

    String KEY_SUCCESS = "SUCCESS";
    String KEY_ERROR = "ERROR";
}
