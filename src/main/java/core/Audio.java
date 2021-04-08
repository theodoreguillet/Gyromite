package core;

import scene.Scene;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Play audio resource.
 */
public class Audio implements LineListener {
    private final Scene scene;
    private Clip currentClip = null;
    public Audio(Scene scene) {
        this.scene = scene;
    }

    public void play(String id) {
        stop();
        var clip = scene.resources().getAudio(id);
        if(clip != null) {
            clip.addLineListener(this);
            clip.setMicrosecondPosition(0);
            clip.start();
            currentClip = clip;
        }
    }

    public void loop(String id) {
        stop();
        var clip = scene.resources().getAudio(id);
        if(clip != null) {
            clip.addLineListener(this);
            clip.setMicrosecondPosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            currentClip = clip;
        }
    }

    public void stop() {
        if(currentClip != null) {
            currentClip.stop();
            currentClip = null;
        }
    }

    public boolean isPlaying() {
        return currentClip != null;
    }

    @Override
    public void update(LineEvent event) {
        if(event.getType() == LineEvent.Type.STOP || event.getType() == LineEvent.Type.CLOSE) {
            currentClip = null;
            event.getLine().removeLineListener(this);
        }
    }
}
