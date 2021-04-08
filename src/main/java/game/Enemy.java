package game;

import scene.AnimatedSprite;
import scene.Scene;
import scene.map.Tile;
import scene.map.TiledMap;
import scene.physics.Body;
import scene.physics.PolygonShape;

/**
 * Class of the enemies in gyromite
 */
public class Enemy extends AnimatedSprite {
    private enum State {
        IDLE, WALK, ROPE_CLIMB, JUMP, CRUSHING, CRUSHED, EATINGRADISH, EATINGHECTOR, EATINGHECTORONROPE
    }

    private static double WIDTH = 30;
    private static double HEIGHT = 31;
    private static double BODY_WIDTH2 = 10;
    private static double BODY_HEIGHT2 = 19;

    private Tile ropeTile = null;
    private double crushDelay;
    private boolean inContactCeiling;
    private boolean inContactFloor;
    private boolean inContactColTop;
    private boolean inContactColBottom;
    private boolean inContactVerticalObstacle;
    private Direction direction;
    private Direction horizontalDirection;
    private Direction verticalDirection = null;
    private State state = State.IDLE;

    public Enemy() {
        super();
    }

    /**
     * preload the spriteSheet
     */

    public static void preload(Scene scene) {
        scene.resources().loadImage("/img/enemy.png", "enemy");
    }

    /**
     * init values of attirbutes of this class
     */
    @Override
    public void init() {
        direction = Math.random() < 0.5 ? Direction.LEFT : Direction.RIGHT;
        horizontalDirection = direction;
        setBody(new PolygonShape(BODY_WIDTH2, BODY_HEIGHT2), Body.Mode.CHARACTER);
        body().restitution = 0.0;
        size().set(WIDTH, HEIGHT);

        this.addAnimation("idle")
                .addFrames("enemy", 4, 5, 0, 0)
                .setSpeed(1)
                .loop(true);
        this.addAnimation("walk")
                .addFrames("enemy", 4, 5, 0, 2)
                .setSpeed(10)
                .loop(true);
        this.addAnimation("eatRadish")
                .addFrames("enemy", 4, 5, 8, 9)
                .setSpeed(10)
                .loop(true);
        this.addAnimation("eatHector")
                .addFrames("enemy", 4, 5, 10, 10)
                .setSpeed(10)
                .loop(true);
        this.addAnimation("jump")
                .addFrames("enemy", 4, 5, 11, 11)
                .setSpeed(10)
                .loop(false);
        this.addAnimation("climb")
                .addFrames("enemy", 4, 5, 12, 13)
                .setSpeed(10)
                .loop(false);
        this.addAnimation("eatHectorOnRope")
                .addFrames("enemy", 4, 5, 14, 14)
                .setSpeed(10)
                .loop(false);
        this.addAnimation("dead")
                .addFrames("enemy", 4, 5, 16, 16)
                .setSpeed(10)
                .loop(false);
        this.play("idle");
    }

    @Override
    public void update() {
        super.update();

        updateContacts();
        if (ropeTile != null && body().gravity.y != 0.0) {
            position().x = ropeTile.position().x + 1;
            body().velocity.set(0, 0);
            body().gravity.set(0, 0);
            body().force.set(0, 0);
        } else if (ropeTile == null && body().gravity.y == 0.0) {
            body().resetGravity();
        }
        updateDirection();

        move();
        updateAnimations();
    }


    public void move() {
        if (direction == Direction.RIGHT) {
            body().velocity.x = 50.0;
        } else if (direction == Direction.LEFT) {
            body().velocity.x = -50.0;
        } else if (direction == Direction.UP) {
            body().velocity.y = -50.0;
        } else {
            body().velocity.y = 50.0;
        }
    }

    public void inverseDirection() {
        if (direction == Direction.RIGHT) {
            direction = Direction.LEFT;
            horizontalDirection = direction;
        } else if (direction == Direction.LEFT) {
            direction = Direction.RIGHT;
            horizontalDirection = direction;
        } else if (direction == Direction.UP) {
            direction = Direction.DOWN;
            verticalDirection = direction;
        } else if (direction == Direction.DOWN) {
            direction = Direction.UP;
            verticalDirection = direction;
        }
    }

    public void feed(Radish radish) {
        // TODO : stop the enemy while the radish is still in the game and the he have to continue his way
        state = State.EATINGRADISH;
        radish.setGettingEated();
    }

    public boolean isEating() {
        return state == State.EATINGRADISH;
    }

    public void updateAnimations() {
        String anim = "idle";
        boolean backward = false;
        switch (state) {
            case WALK -> anim = "walk";
            case JUMP -> anim = "jump";
            case ROPE_CLIMB -> {
                anim = "climb";
                backward = direction == Direction.UP;
            }
            case CRUSHING -> anim = "crushing";
            case CRUSHED -> anim = "crushed";
            case EATINGHECTOR -> anim = "eatRadish";
            case EATINGHECTORONROPE -> anim = "eatHectorOnRope";
        }
        flipH(false);
        if (direction == Direction.RIGHT) {
            flipH(true);
        }
        if(anim == null) {
            reset();
        } else if (!currentAnimation().equals(anim) || !isPlaying() || isPlayingBackwards() != backward) {
            play(anim, backward);
        }
    }

    public void updateDirection() {
        var tilemap = ((TiledMap)owner());
        int layerId = tilemap.getLayerId("background");
        var coords = tilemap.getTileCoordFromPosition(layerId, position());
        Tile leftTile = tilemap.getTile(layerId, coords[0] - 1, coords[1]);
        Tile rightTile = tilemap.getTile(layerId, coords[0] + 1, coords[1]);

        boolean obstacleLeft = leftTile != null &&
                (leftTile.type.equals("floor") || leftTile.type.equals("wall"));
        boolean obstacleRight = rightTile != null &&
                (rightTile.type.equals("floor") || rightTile.type.equals("wall"));

        if(ropeTile != null) {
            if(verticalDirection == null) {

                direction = verticalDirection = Direction.UP;
                state = State.ROPE_CLIMB;
            } else {
                if (inContactCeiling || inContactFloor) {
                    if (Math.random() < 0.5) {
                        direction = horizontalDirection;
                       // verticalDirection = null;
                        state = State.JUMP;
                        System.out.println("ContactSeiling and jumping");
                        System.out.println(horizontalDirection);
                    } else {
                        inverseDirection();
                        verticalDirection = direction;
                        state = State.ROPE_CLIMB;
                    }
                }
            }

        }
    }

    public void updateContacts() {
        ropeTile = null;

        inContactCeiling = false;
        inContactFloor = false;
        inContactColTop = false;
        inContactColBottom = false;

        for (var b : body().contacts()) {
            if (b.node().owner() instanceof Tile) {
                Tile tile = (Tile) b.node().owner();
                if (tile.type.equals("rope")) {
                    ropeTile = tile;
                } else if ((tile.type.equals("floor") || tile.type.equals("ceiling")) &&
                        Math.abs(tile.position().x - position().x) <= BODY_WIDTH2 + tile.sprite.size().width / 2.0
                ) {
                    if (tile.position().y < position().y) {
                        inContactCeiling = true;
                    } else {
                        inContactFloor = true;
                    }

                }
            } else if (b.node() instanceof Column) {
                Column column = (Column) b.node();
                if (column.isMoving()) {
                    // Remove column velocity inertia
                    body().velocity.y = 0.0;
                    if (Math.abs(column.position().x - position().x) <= BODY_WIDTH2 + column.size().width / 2.0) {
                        if (column.position().y < position().y) {
                            inContactColTop = true;
                        } else {
                            inContactColBottom = true;
                        }
                    }
                }
            } else if (b.node() instanceof Radish) {
                state = State.EATINGRADISH;
            } else if (b.node() instanceof Player) {
                if (state != State.EATINGRADISH) {
                    state = ropeTile == null ? State.EATINGHECTOR : State.EATINGHECTORONROPE;
                }
            }
        }

    }
}
