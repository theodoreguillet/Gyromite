package game;

import scene.AnimatedSprite;
import scene.physics.Body;
import scene.physics.CircleShape;

public class Bomb extends AnimatedSprite {

    public void preload() {
        scene().resources().loadImage("/img/bomb.png", "bomb");
    }

    public void init() {

        setBody(new CircleShape(10), Body.Mode.RIGID);
        this.addAnimation("desactived")
                .addFrames("bomb", 2, 4, 0, 3)
                .setSpeed(50)
                .loop(true);
        this.addAnimation("explode")
                .addFrames("bomb", 2, 4, 4, 7)
                .setSpeed(50)
                .loop(false);
    }

    public void explode() {
        play("explode");
    }
}
