package game;

import scene.AnimatedSprite;
import scene.physics.Body;
import scene.physics.CircleShape;

public class Bomb extends AnimatedSprite {
    public void explode() {
        play("explode");
    }

    @Override
    protected void init() {
        super.init();

        setBody(new CircleShape(10), Body.Mode.TRANSPARENT);
        size().set(64, 64);
        this.addAnimation("idle")
                .addFrames("bomb", 4, 2, 0, 3)
                .setSpeed(5)
                .loop(true);
        this.addAnimation("explode")
                .addFrames("bomb", 4, 2, 4, 7)
                .setSpeed(5)
                .loop(false);
        play("idle");
    }

    @Override
    protected void update() {
        super.update();

        if(!isPlaying()) { // Exploded
            remove();
        }
    }
}
