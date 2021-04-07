package game;

import core.MathUtils;
import scene.map.Tile;
import scene.physics.Body;
import scene.physics.CircleShape;

/**
 * Class of the enemies in gyromite
 */
public class Enemy extends scene.AnimatedSprite {
    /**
     * Enum to precise the direction of mouvements
     */
    public enum Direction {
        RIGHT, LEFT, UP, DOWN
    }

    private boolean eating;
    private Direction direction;

    public Enemy() {
        super();
    }

    /**
     * preload the spriteSheet
     */
    public void preload() {
        scene().resources().loadImage("/img/enemy.png", "enemy");
    }

    /**
     * init values of attirbutes of this class
     */
    @Override
    public void init() {
        eating = false;
        direction = Math.random() < 0.5 ? Direction.LEFT : Direction.RIGHT;
        preload();
        setBody(new CircleShape(10), Body.Mode.CHARACTER);

        this.addAnimation("walk")
                .addFrames("enemy", 4, 5, 0, 2)
                .setSpeed(50)
                .loop(true);
        this.addAnimation("eatRadish")
                .addFrames("enemy", 4, 5, 8, 9)
                .setSpeed(50)
                .loop(true);
        this.addAnimation("eatHector")
                .addFrames("enemy", 4, 5, 10, 10)
                .setSpeed(50)
                .loop(true);
        this.addAnimation("jump")
                .addFrames("enemy", 4, 5, 11, 11)
                .setSpeed(50)
                .loop(false);
        this.addAnimation("climbing")
                .addFrames("enemy", 4, 5, 12, 13)
                .setSpeed(50)
                .loop(false);
        this.addAnimation("eatHectorOnRope")
                .addFrames("enemy", 4, 5, 14, 14)
                .setSpeed(50)
                .loop(false);
        this.addAnimation("dead")
                .addFrames("enemy", 4, 5, 16, 16)
                .setSpeed(50)
                .loop(false);
        play("walkRight");
    }

    public void update() {
        move();

        Tile ropeTile = null;
        for (var b : body().contacts()) {
            if (b.node().owner() instanceof Tile) {
                Tile tile = (Tile) b.node().owner();
                if (tile.type.equals("rope")) {
                    ropeTile = tile;
                }
            } else if (b.node() instanceof Column) {
                if (((Column) b.node()).isMoving()) {
                    // Remove column velocity inertia
                    body().velocity.y = 0.0;
                }
            }
        }
        if (ropeTile != null && body().gravity.y != 0.0) {
            position().x = ropeTile.position().x + 2;
            body().velocity.set(0, 0);
            body().gravity.set(0, 0);
            body().force.set(0, 0);
        } else if (ropeTile == null && body().gravity.y == 0.0) {
            body().resetGravity();
        }
    }

    public void move() {
        if (direction == Direction.RIGHT) {
            body().velocity.x = 100.0;
            setOrient(0.0);
            this.play("walkRight", true);
        } else if (direction == Direction.LEFT) {
            body().velocity.x = -100;
            setOrient(MathUtils.PI);
            this.play("walkLeft");
        } else if (direction == Direction.UP) {
            body().velocity.y = -100.0;
            setOrient(-MathUtils.PI / 2.0);
            this.play("climbing");
        } else {
            body().velocity.y = 100.0;
            setOrient(MathUtils.PI / 2.0);
            this.play("climbing", true);
        }
    }

    public void inverseDirection() {
        if (direction == Direction.RIGHT) {
            direction = Direction.LEFT;
        } else if (direction == Direction.LEFT) {
            direction = Direction.RIGHT;
        } else if (direction == Direction.UP) {
            direction = Direction.DOWN;
        } else if (direction == direction.DOWN) {
            direction = Direction.UP;
        }
    }

    public void feed() {
        eating = true;
        this.play("eatRadish");
    }

    public void dead() {
        this.play("dead");
    }

    public void eatHector() {
        this.play("eatHector");
    }

    public void eatHectorOnRope() {
        this.play("eatHectorOnRope");
    }

    public void jump() {
        this.play("jump");
    }

    public boolean isEating() {
        return eating;
    }
}
