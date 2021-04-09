package game.nodes;

import core.MainLoop;
import core.Rect2;
import game.Game;
import scene.AnimatedSprite;
import scene.map.Tile;
import scene.map.TiledMap;
import scene.physics.Body;
import scene.physics.PolygonShape;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends AnimatedSprite implements KeyListener {
    private enum State {
        IDLE,
        WALK,
        ROPE_CLIMB,
        JUMP,
        PUT_RADISH,
        CRUSHING,
        HIT,
        DEAD
    }

    private static final double WIDTH = 32;
    private static final double HEIGHT = 44;
    private static final double BODY_WIDTH2 = 12;
    private static final double BODY_HEIGHT2 = 22;
    private State state = State.IDLE;
    private Direction direction = Direction.LEFT;
    private Radish radish = null;
    private Tile ropeTile = null;

    private boolean onFloor = false;
    private double crushSpacing = -1;
    private double crushDelay;

    @Override
    public void init() {
        scene().input().addListener(this);
        setBody(new PolygonShape(BODY_WIDTH2, BODY_HEIGHT2), Body.Mode.CHARACTER);
        body().restitution = 0.0;
        size().set(WIDTH, HEIGHT);

        scene().camera().follow(this, new Rect2(-150, -150, 150, 150));

        this.addAnimation("idle")
                .addFrames("player", 6, 5, 0, 0)
                .setSpeed(1)
                .loop(false);
        this.addAnimation("walk")
                .addFrames("player", 6, 5, 0, 4)
                .setSpeed(10)
                .loop(true);
        this.addAnimation("idleWithRadish")
                .addFrames("player", 6, 5, 6, 6)
                .setSpeed(10)
                .loop(true);
        this.addAnimation("walkWithRadish")
                .addFrames("player", 6, 5, 6, 10)
                .setSpeed(10)
                .loop(true);
        this.addAnimation("putRadish")
                .addFrames("player", 6, 5, 11, 11)
                .setSpeed(1)
                .loop(false);
        this.addAnimation("climb")
                .addFrames("player", 6, 5, 12, 13)
                .setSpeed(5)
                .loop(true);
        this.addAnimation("idleRope")
                .addFrames("player", 6, 5, 12, 12)
                .setSpeed(1)
                .loop(true);
        this.addAnimation("jump")
                .addFrames("player", 6, 5, 14, 14)
                .setSpeed(1)
                .loop(true);
        this.addAnimation("crushing")
                .addFrames("player", 6, 5, 15, 15);
        this.addAnimation("crushed")
                .addFrames("player", 6, 5, 16, 16);
        this.addAnimation("hit")
                .addFrames("player", 6, 5, 18, 19)
                .setSpeed(1)
                .loop(false);
        play("idle");
    }

    @Override
    public void update() {
        super.update();

        updateContacts();

        var tiledmap = ((TiledMap)owner());
        Tile tile = ((TiledMap)owner()).getTileFromPosition(tiledmap.getLayerId("background"), position());

        if (ropeTile != null) {
            if(body().gravity.y != 0.0) {
                body().velocity.set(0, 0);
                body().gravity.set(0, 0);
                body().force.set(0, 0);
            }
            if(state == State.JUMP && body().velocity.x == 0.0) {
                state = State.IDLE;
            }
            if(state == State.ROPE_CLIMB || state == State.IDLE) {
                position().x = ropeTile.position().x + 1;
            }
        } else if (body().gravity.y == 0.0) {
            body().resetGravity();
        }

        if (crushSpacing != -1) {
            if (state != State.CRUSHING && state != State.DEAD) {
                state = State.CRUSHING;
                crushDelay = 1;
            }
        }

        if (state == State.JUMP && onFloor && body().velocity.x == 0) {
            state = State.IDLE;
        }

        if (state == State.WALK) {
            body().velocity.x = direction == Direction.LEFT ? -100 : 100;
        } else if (state == State.ROPE_CLIMB) {
            body().velocity.y = direction == Direction.UP ? -100 : 100;
        } else if (state == State.JUMP) {
            body().velocity.x = Math.signum(body().velocity.x) *
                    Math.max(0, Math.abs(body().velocity.x) - 100.0 / 32.0);
        }

        if (state == State.CRUSHING) {
            crushDelay -= MainLoop.DT;
            offset().y = -Math.max(0, BODY_HEIGHT2 - crushSpacing / 2.0);
            if(crushSpacing <= 2 || crushDelay <= 0) {
                state = State.DEAD;
            }
        }

        if(state == State.PUT_RADISH && !isPlaying()) {
            state = State.IDLE;
        }
        if(state == State.HIT && !isPlaying()) {
            state = State.DEAD;
        }

        updateAnimations();

        if (state == State.DEAD) {
            ((Game)scene()).endPhase(false);
        }
    }

    @Override
    protected void destroy() {
        super.destroy();
        scene().input().removeListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(state == State.HIT || state == State.CRUSHING || state == State.DEAD) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (state == State.IDLE && onFloor) {
                state = State.WALK;
                direction = e.getKeyCode() == KeyEvent.VK_RIGHT ? Direction.RIGHT : Direction.LEFT;
            } else if (onRope() && state != State.JUMP && state != State.WALK) {
                state = State.JUMP;
                direction = e.getKeyCode() == KeyEvent.VK_RIGHT ? Direction.RIGHT : Direction.LEFT;
                body().velocity.x = direction == Direction.LEFT ? -100 : 100;
                body().velocity.y = 0;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (onRope() && (state == State.WALK || state == State.IDLE || state == State.ROPE_CLIMB)) {
                state = State.ROPE_CLIMB;
                direction = e.getKeyCode() == KeyEvent.VK_UP ? Direction.UP : Direction.DOWN;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(state == State.HIT || state == State.CRUSHING || state == State.DEAD) {
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (state != State.JUMP) {
                state = State.IDLE;
                body().velocity.x = 0;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (onRope()) {
                if(state == State.ROPE_CLIMB) {
                    state = State.IDLE;
                }
                body().velocity.y = 0;
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

        for (var contactEntry : body().contacts().entrySet()) {
            var b = contactEntry.getKey();
            var manifold = contactEntry.getValue();

            if (b.node().owner() instanceof Tile) {
                Tile tile = (Tile) b.node().owner();
                if (tile.type.equals("rope")) {
                    ropeTile = tile;
                } else if(manifold.normal.y != 0) {
                    if(manifold.normal.y < 0) {
                        bottomTile = tile;
                    } else {
                        topTile = tile;
                    }
                }
            } else if (b.node() instanceof Column) {
                Column column = (Column) b.node();
                // Remove column velocity inertia
                body().velocity.y = 0.0;

                if(manifold.normal.y != 0) {
                    if (manifold.normal.y < 0) {
                        bottomColumn = column;
                    } else {
                        topColumn = column;
                    }
                }
            } else if (b.node() instanceof Enemy) {
                var enemy = (Enemy) b.node();
                if (!enemy.isEating()) {
                    if (radish != null) {
                        enemy.feedRadish();
                        radish.remove();
                        radish = null;
                        state = State.PUT_RADISH;
                    } else {
                        Direction dir;
                        if(manifold.normal.y == 0) {
                            if(manifold.normal.x > 0) {
                                dir = Direction.LEFT;
                            } else {
                                dir = Direction.RIGHT;
                            }
                        } else if(manifold.normal.y > 0) {
                            dir = Direction.UP;
                        } else {
                            dir = Direction.DOWN;
                        }
                        enemy.attack(dir);
                        state = State.HIT;
                        body().velocity.set(0, 0);
                        body().force.set(0, 0);
                    }
                }
            } else if (b.node() instanceof Bomb) {
                b.node().remove();
                ((Game)scene()).bombRemoved();
            } else if (b.node() instanceof Radish) {
                if (radish == null) {
                    b.node().remove();
                    radish = addChild(new Radish(false));
                }
            }
        }

        onFloor = (bottomTile != null || bottomColumn != null);

        crushSpacing = -1;
        if(topTile != null && bottomColumn != null) {
            double topBottomY = topTile.position().y + topTile.sprite.size().height / 2.0;
            double bottomTopY = bottomColumn.position().y - bottomColumn.size().height / 2.0;
            if(topBottomY <= bottomTopY) { // y axe is inverted
                crushSpacing = bottomTopY - topBottomY;
            }
        } else if(topColumn != null && bottomTile != null) {
            double topBottomY = topColumn.position().y + topColumn.size().height / 2.0;
            double bottomTopY = bottomTile.position().y - bottomTile.sprite.size().height / 2.0;
            if(topBottomY <= bottomTopY) { // y axe is inverted
                crushSpacing = bottomTopY - topBottomY;
            }
        }
    }

    private void updateAnimations() {
        String anim = null;
        boolean backward = false;
        switch (state) {
            case WALK -> {
                anim = radish != null ? "walkWithRadish" : "walk";
                backward = true;
            }
            case JUMP -> anim = "jump";
            case ROPE_CLIMB -> {
                anim = "climb";
                backward = direction == Direction.UP;
            }
            case CRUSHING -> anim = crushSpacing > 25 ? "crushing" : "crushed";
            case PUT_RADISH -> anim = "putRadish";
            case HIT -> anim = "hit";
            case IDLE -> anim = onRope() ? "idleRope" : radish != null ? "idleWithRadish" : "idle";
        }
        flipH(false);
        if (direction == Direction.RIGHT) {
            flipH(true);
        }
        if(radish != null) {
            radish.position().set(direction == Direction.RIGHT ? 14 : -14, 0);
        }
        if(anim == null) {
            reset();
        } else if (!currentAnimation().equals(anim) || !isPlaying() || isPlayingBackwards() != backward) {
            play(anim, backward);
        }
    }
}
