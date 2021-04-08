package game;

import scene.AnimatedSprite;
import scene.Scene;

public class SmickParticles extends AnimatedSprite {
    public static void preload(Scene scene) {
        scene.resources().loadImage("/img/particles.png", "particles");
    }

    @Override
    protected void init() {
        addAnimation("particles")
                .addFrames("particles", 3, 1, 0, 2)
                .setSpeed(1)
                .loop(false);
        size().set(108, 64);
    }

    @Override
    protected void update() {
        if(!isPlaying()) {
            remove();
        }
    }
}
