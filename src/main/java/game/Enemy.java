package game;

import core.MainLoop;
import scene.AnimatedSprite;
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
    private static final double BODY_HEIGHT2 = 16;

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

    private Radish radish = null;

    public Enemy() {
        super();
    }

    /**
     * init values of attirbutes of this class
     */
    @Override
    protected void init() {
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
    protected void update() {
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
        if(state == State.ROPE_CLIMB && ropeTile != null) {
            position().x = ropeTile.position().x + 1;
            body().velocity.x = 0;
        }
        if((state == State.JUMP && onFloor) || (state == State.ROPE_CLIMB && ropeTile == null)) {
            state = State.WALK;
        }

        if(state == State.EATING_RADISH && radish != null) {
            if(radish.isEaten()) {
                radish.remove();
                radish = null;

                body().setMode(Body.Mode.CHARACTER);
                state = onRope() ? State.ROPE_CLIMB : State.WALK;
            }
        }

        updateDirection();
        move();
        updateAnimations();

        if(state == State.CRUSHED) {
            // Dead with particles
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

    public void invertDirection() {
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

    public void feedRadish() {
        if(state != State.EATING_RADISH) {
            state = State.EATING_RADISH;
            radish = addChild(new Radish(false));
            radish.position().set(direction == Direction.RIGHT ? 14 : -14, 0);
            radish.setGettingEaten();

            body().setMode(Body.Mode.TRANSPARENT);
            body().velocity.set(0, 0);
            body().force.set(0, 0);
        }
    }

    public void attack(Direction direction) {
        state = State.EATING_HECTOR;
        this.direction = direction;
        body().velocity.set(0, 0);
        body().force.set(0, 0);
    }

    public boolean isEating() {
        return state == State.EATING_RADISH;
    }

    private void updateAnimations() {
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
            case EATING_HECTOR -> anim = direction == Direction.RIGHT ||
                    direction == Direction.LEFT ? "eatHector" : "eatHectorOnRope";
        }
        flipH(false);
        flipV(false);
        if (direction == Direction.RIGHT) {
            flipH(true);
        }
        if(state == State.EATING_HECTOR && direction == Direction.DOWN) {
            flipV(true);
        }
        if(anim == null) {
            reset();
        } else if (!currentAnimation().equals(anim) || !isPlaying() || isPlayingBackwards() != backward) {
            play(anim, backward);
        }
    }

    private void updateDirection() {
        if((state == State.WALK || state == State.JUMP) &&
                (inContactLeft && direction == Direction.LEFT) ||
                (inContactRight && direction == Direction.RIGHT)
        ) {
            if(onRope()) {
                if(state == State.JUMP) {
                    invertDirection();
                    lastHorizontalDirection = direction;
                    direction = lastVerticalDirection;
                    invertDirection();
                } else {
                    lastHorizontalDirection = direction;
                    direction = Direction.UP;
                }
                state = State.ROPE_CLIMB;
            } else {
                invertDirection();
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

            boolean jump = (!obstacleLeft || !obstacleRight);// && Math.random() < 0.8;
            if(jump) {
                lastVerticalDirection = direction;
                direction = lastHorizontalDirection;
                if((direction == Direction.LEFT && obstacleLeft) ||
                        (direction == Direction.RIGHT && obstacleRight)
                ) {
                    invertDirection();
                }
                state = State.JUMP;
                body().velocity.x = direction == Direction.LEFT ? -50 : 50;
            } else {
                invertDirection();
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

        for (var contactEntry : body().contacts().entrySet()) {
            var b = contactEntry.getKey();
            var manifold = contactEntry.getValue();

            if (b.node().owner() instanceof Tile) {
                Tile tile = (Tile) b.node().owner();
                if (tile.type.equals("rope")) {
                    ropeTile = tile;
                } else {
                    if(manifold.normal.y == 0) {
                        if(Math.abs(tile.position().y - position().y) <= BODY_HEIGHT2) {
                            if (manifold.normal.x < 0) {
                                inContactRight = true;
                            } else {
                                inContactLeft = true;
                            }
                        }
                    } else if(manifold.normal.y < 0) {
                        bottomTile = tile;
                    } else {
                        topTile = tile;
                    }
                }
            } else if (b.node() instanceof Column) {
                Column column = (Column) b.node();
                // Remove column velocity inertia
                body().velocity.y = 0.0;

                if(manifold.normal.y == 0) {
                    if(Math.abs(column.position().y - position().y) <=
                            BODY_HEIGHT2 + column.size().height / 2.0 - SPRITE_BODY_HEIGHT2
                    ) {
                        if (manifold.normal.x < 0) {
                            inContactRight = true;
                        } else {
                            inContactLeft = true;
                        }
                    }
                } else if(manifold.normal.y < 0) {
                    bottomColumn = column;
                } else {
                    topColumn = column;
                }
            } else if (b.node() instanceof Radish) {
                feedRadish();
                b.node().remove();
            } else if(b.node() instanceof Enemy) {
                var other = (Enemy)b.node();
                if((body().velocity.x > 0 && b.velocity.x < 0 && position().x < other.position().x) ||
                        (b.velocity.x > 0 && body().velocity.x < 0 && other.position().x < position().x) ||
                        (body().velocity.y > 0 && b.velocity.y < 0 && position().y < other.position().y) ||
                        (b.velocity.y > 0 && body().velocity.y < 0 && other.position().y < position().y)
                ) {
                    invertDirection();
                }
            }
        }

        inContactCeiling = topTile != null;

        onFloor = (bottomTile != null || bottomColumn != null);

        crushSpacing = -1;
        if(topTile != null && bottomColumn != null) {
            double topBottomY = topTile.position().y + SPRITE_BODY_HEIGHT2;
            double bottomTopY = bottomColumn.position().y - bottomColumn.size().height / 2.0;
            if(topBottomY <= bottomTopY) { // y axe is inverted
                crushSpacing = bottomTopY - topBottomY;
            }
        } else if(topColumn != null && bottomTile != null) {
            double topBottomY = topColumn.position().y + topColumn.size().height / 2.0;
            double bottomTopY = bottomTile.position().y - SPRITE_BODY_HEIGHT2;
            if(topBottomY <= bottomTopY) { // y axe is inverted
                crushSpacing = bottomTopY - topBottomY;
            }
        }
    }
}
