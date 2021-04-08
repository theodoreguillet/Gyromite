package game;

import core.MainLoop;
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
        WALK, ROPE_CLIMB, JUMP, CRUSHING, CRUSHED, EATING_RADISH, EATING_HECTOR
    }

    private static final double WIDTH = 32;
    private static final double HEIGHT = 32;
    private static final double BODY_WIDTH2 = 10;
    private static final double BODY_HEIGHT2 = 19;

    private static final double SPRITE_BODY_HEIGHT2 = 28.0/2.0;
    private static final double SPRITE_BODY_WIDTH2 = 32.0/2.0;

    private Tile ropeTile = null;
    private boolean onFloor = false;
    private boolean inContactCeiling = false;
    private boolean inContactLeft = false;
    private boolean inContactRight = false;
    private double crushSpacing = -1;
    private double crushDelay;
    private Direction direction;
    private Direction lastHorizontalDirection;
    private Direction lastVerticalDirection;
    private State state = State.WALK;

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
        lastHorizontalDirection = direction = Math.random() < 0.5 ? Direction.LEFT : Direction.RIGHT;
        lastVerticalDirection = Direction.UP;
        setBody(new PolygonShape(BODY_WIDTH2, BODY_HEIGHT2), Body.Mode.CHARACTER);
        body().restitution = 0.0;
        size().set(WIDTH, HEIGHT);

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
        this.addAnimation("crushing")
                .addFrames("enemy", 4, 5, 16, 16)
                .setSpeed(10)
                .loop(false);
    }

    @Override
    public void update() {
        super.update();

        updateContacts();

        if (crushSpacing != -1) {
            if (state != State.CRUSHING && state != State.CRUSHED) {
                state = State.CRUSHING;
                crushDelay = 1;
            }
        }
        if (state == State.CRUSHING) {
            crushDelay -= MainLoop.DT;
            offset().y = -Math.max(0, BODY_HEIGHT2 - crushSpacing / 2.0);
            if(crushSpacing <= 2 || crushDelay <= 0) {
                state = State.CRUSHED;
            }
        }

        if (ropeTile != null && body().gravity.y != 0.0) {
            body().gravity.set(0, 0);
        } else if (ropeTile == null && body().gravity.y == 0.0) {
            body().resetGravity();
        }
        if(state == State.ROPE_CLIMB) {
            position().x = ropeTile.position().x + 1;
            body().velocity.x = 0;
        }
        if(state == State.JUMP && onFloor) {
            state = State.WALK;
        }
        updateDirection();
        move();
        updateAnimations();

        if(state == State.CRUSHED) {
            owner().addChild(new SmickParticles()).position().set(position());
            remove();
        }
    }


    public void move() {
        if (state == State.JUMP || !onFloor) {
            body().velocity.x = Math.signum(body().velocity.x) *
                    Math.max(0, Math.abs(body().velocity.x) - 25.0 / 32.0);
        } else if(state == State.WALK) {
            body().velocity.x = direction == Direction.LEFT ? -50.0 : 50.0;
        }
        if(state == State.ROPE_CLIMB) {
            body().velocity.y = direction == Direction.UP ? -50.0 : 50.0;
        }
    }

    public void inverseDirection() {
        if (direction == Direction.RIGHT) {
            direction = Direction.LEFT;
        } else if (direction == Direction.LEFT) {
            direction = Direction.RIGHT;
        } else if (direction == Direction.UP) {
            direction = Direction.DOWN;
        } else if (direction == Direction.DOWN) {
            direction = Direction.UP;
        }
    }

    public void feed(Radish radish) {
        // TODO : stop the enemy while the radish is still in the game and the he have to continue his way
        state = State.EATING_RADISH;
        radish.setGettingEated();
    }

    public boolean isEating() {
        return state == State.EATING_RADISH;
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
            case CRUSHED -> anim = null;
            case EATING_RADISH -> anim = "eatRadish";
            case EATING_HECTOR -> anim = onRope() ? "eatHector" : "eatHectorOnRope";
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
        if((state == State.WALK || state == State.JUMP) &&
                (inContactLeft && direction == Direction.LEFT) ||
                (inContactRight && direction == Direction.RIGHT)
        ) {
            if(onRope()) {
                lastHorizontalDirection = direction;
                if(state == State.JUMP) {
                    direction = lastVerticalDirection == Direction.UP
                            ? Direction.DOWN : Direction.UP;
                    lastHorizontalDirection = lastHorizontalDirection == Direction.RIGHT
                            ? Direction.LEFT : Direction.RIGHT;
                } else {
                    direction = Direction.UP;
                }
                state = State.ROPE_CLIMB;
            } else {
                inverseDirection();
            }
        } else if(state == State.ROPE_CLIMB && (
                (direction == Direction.UP && inContactCeiling) ||
                        (direction == Direction.DOWN && onFloor))
        ) {
            var tilemap = ((TiledMap)owner());
            int layerId = tilemap.getLayerId("background");
            var coords = tilemap.getTileCoordFromPosition(layerId, position());
            Tile leftTile = tilemap.getTile(layerId, coords[0] - 1, coords[1]);
            Tile rightTile = tilemap.getTile(layerId, coords[0] + 1, coords[1]);

            boolean obstacleLeft = leftTile != null &&
                    (leftTile.type.equals("floor") || leftTile.type.equals("wall"));
            boolean obstacleRight = rightTile != null &&
                    (rightTile.type.equals("floor") || rightTile.type.equals("wall"));

            boolean jump = (!obstacleLeft || !obstacleRight) && Math.random() < 0.8;
            if(jump) {
                if((lastHorizontalDirection == Direction.LEFT && !obstacleLeft) ||
                        (lastHorizontalDirection == Direction.RIGHT && !obstacleRight)
                ) {
                    lastVerticalDirection = direction;
                    direction = lastHorizontalDirection;
                    state = State.JUMP;
                    body().velocity.x = direction == Direction.LEFT ? -50 : 50;
                } else {
                    inverseDirection();
                }
            } else {
                inverseDirection();
            }
        }
    }

    private boolean onRope() {
        return ropeTile != null;
    }

    private void updateContacts() {
        ropeTile = null;

        Tile topTile = null;
        Tile bottomTile = null;
        Column topColumn = null;
        Column bottomColumn = null;

        inContactLeft = false;
        inContactRight = false;

        for (var b : body().contacts()) {
            if (b.node().owner() instanceof Tile) {
                Tile tile = (Tile) b.node().owner();
                if (tile.type.equals("rope")) {
                    ropeTile = tile;
                } else {
                    if(Math.abs(tile.position().x - position().x) <=
                            BODY_WIDTH2 + SPRITE_BODY_WIDTH2
                    ) {
                        boolean isTopOrBottom = false;
                        if(tile.type.equals("floor") || tile.type.equals("ceiling") || tile.type.equals("wall")) {
                            if(Math.abs(tile.position().y - position().y) <= BODY_HEIGHT2) {
                                if (tile.position().x < position().x) {
                                    inContactLeft = true;
                                } else {
                                    inContactRight = true;
                                }
                            } else {
                                if (tile.position().y < position().y) {
                                    topTile = tile;
                                } else {
                                    bottomTile = tile;
                                }
                            }
                        }
                    }
                }
            } else if (b.node() instanceof Column) {
                Column column = (Column) b.node();
                // Remove column velocity inertia
                body().velocity.y = 0.0;
                if (Math.abs(column.position().x - position().x) <=
                        BODY_WIDTH2 + SPRITE_BODY_WIDTH2
                ) {
                    if (column.position().y < position().y) {
                        topColumn = column;
                    } else {
                        bottomColumn = column;
                    }
                }
            } else if (b.node() instanceof Radish) {
                state = State.EATING_RADISH;
            } else if (b.node() instanceof Player) {
                if (state != State.EATING_RADISH) {
                    state = State.EATING_HECTOR;
                }
            }
        }

        inContactCeiling = topTile != null;

        if(bottomTile != null || bottomColumn != null) {
            double bottomTopY = bottomTile != null
                    ? bottomTile.position().y - SPRITE_BODY_HEIGHT2
                    : bottomColumn.position().y - bottomColumn.size().height / 2.0;
            double bodyBottomY = position().y + BODY_HEIGHT2;
            onFloor = Math.abs(bodyBottomY - bottomTopY) < BODY_HEIGHT2 / 10.0;
        } else {
            onFloor = false;
        }

        crushSpacing = -1;
        if(topTile != null && bottomColumn != null) {
            double dst1 = Math.abs(topTile.position().x - position().x);
            double dst2 = Math.abs(bottomColumn.position().x - position().x);
            if(dst1 < BODY_WIDTH2 + SPRITE_BODY_WIDTH2 - SPRITE_BODY_WIDTH2 / 5.0 &&
                    dst2 < BODY_WIDTH2 + bottomColumn.size().width / 2.0 - bottomColumn.size().width / 10.0
            ) {
                double topBottomY = topTile.position().y + SPRITE_BODY_HEIGHT2;
                double bottomTopY = bottomColumn.position().y - bottomColumn.size().height / 2.0;
                if(topBottomY <= bottomTopY) { // y axe is inverted
                    crushSpacing = bottomTopY - topBottomY;
                }
            }
        } else if(topColumn != null && bottomTile != null) {
            double dst1 = Math.abs(bottomTile.position().x - position().x);
            double dst2 = Math.abs(topColumn.position().x - position().x);
            if(dst1 < BODY_WIDTH2 + SPRITE_BODY_WIDTH2 - SPRITE_BODY_WIDTH2 / 5.0 &&
                    dst2 < BODY_WIDTH2 + topColumn.size().width / 2.0 - topColumn.size().width / 10.0
            ) {
                double topBottomY = topColumn.position().y + topColumn.size().height / 2.0;
                double bottomTopY = bottomTile.position().y - SPRITE_BODY_HEIGHT2;
                if(topBottomY <= bottomTopY) { // y axe is inverted
                    crushSpacing = bottomTopY - topBottomY;
                }
            }
        }
    }
}
