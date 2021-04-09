package game.nodes;

import scene.AnimatedSprite;
import scene.Scene;

public class SmickParticles extends AnimatedSprite {
    @Override
    protected void init() {
        super.init();

        size().set(108, 64);
        addAnimation("particles")
                .addFrames("particles", 3, 1, 0, 2)
                .setSpeed(5)
                .loop(false);
        play("particles");
    }

    @Override
    protected void update() {
        super.update();

        if(!isPlaying()) {
            remove();
        }
    }
}
