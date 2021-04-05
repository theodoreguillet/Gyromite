package game;

import core.MathUtils;
import scene.AnimatedSprite;
import scene.Node;
import scene.Scene;
import scene.map.Tile;
import scene.physics.Body;
import scene.physics.CircleShape;
import scene.physics.PolygonShape;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends AnimatedSprite implements KeyListener {
    private enum State {
        IDLE,
        WALK,
        ROPE_CLIMB,
        JUMP,
        PUT_RADISH,
        CRUSH,
        DIE
    }
    private enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
    private static double WIDTH = 32;
    private static double HEIGHT = 44;
    private static double BODY_WIDTH2 = 12;
    private static double BODY_HEIGHT2 = 22;
    private State state = State.IDLE;
    private Direction direction = Direction.LEFT;
    private Radish radish = null;
    private Tile ropeTile = null;

    private boolean inContactCeiling = false;
    private boolean inContactFloor = false;
    private boolean inContactColTop = false;
    private boolean inContactColBottom = false;

    public static void preload(Scene scene) {
        scene.resources().loadImage("/img/player.png", "player");
    }

    @Override
    public void init() {
        scene().input().addListener(this);
        // setBody(new CircleShape(BODY_WIDTH2), Body.Mode.CHARACTER);
        setBody(new PolygonShape(BODY_WIDTH2, BODY_HEIGHT2), Body.Mode.CHARACTER);
        body().restitution = 0.0;
        size().set(WIDTH, HEIGHT);

        this.addAnimation("idle")
                .addFrames("player", 6, 5, 0, 0)
                .setSpeed(1)
                .loop(false);
        this.addAnimation("walk")
                .addFrames("player", 6, 5, 0, 4)
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
        this.addAnimation("crushed")
                .addFrames("player", 6, 5, 15, 16)
                .setSpeed(1)
                .loop(false);
        this.addAnimation("dead")
                .addFrames("player", 6, 5, 18, 19)
                .setSpeed(1)
                .loop(false);
        play("idle");
    }

    @Override
    public void update() {
        super.update();

        updateContacts();

        if (ropeTile != null && body().gravity.y != 0.0) {
            position().x = ropeTile.position().x + 2;
            body().velocity.set(0, 0);
            body().gravity.set(0, 0);
            body().force.set(0, 0);
        } else if (ropeTile == null && body().gravity.y == 0.0) {
            body().resetGravity();
        }

        if((inContactCeiling && inContactColBottom) || (inContactFloor && inContactColTop)) {
            System.out.println("isCrushed");
            state = State.CRUSH;
        }

        if(state == State.JUMP && (inContactFloor || inContactColBottom)) {
            state = State.IDLE;
            body().velocity.x = 0;
        }

        if(state == State.WALK) {
            body().velocity.x = direction == Direction.LEFT ? -100 : 100;
        } else if(state == State.ROPE_CLIMB) {
            body().velocity.y = direction == Direction.UP ? -100 : 100;
        } else if(state == State.JUMP) {
            body().velocity.x = Math.signum(body().velocity.x) * Math.max(0, Math.abs(body().velocity.x) - 1);
        }

        updateAnimations();
    }


    @Override
    public void keyTyped(KeyEvent e) { }
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
            if((state == State.IDLE || state == State.JUMP) && (inContactFloor || inContactColBottom)) {
                state = State.WALK;
                direction = e.getKeyCode() == KeyEvent.VK_RIGHT ? Direction.RIGHT : Direction.LEFT;
            } else if(onRope()) {
                state = State.JUMP;
                direction = e.getKeyCode() == KeyEvent.VK_RIGHT ? Direction.RIGHT : Direction.LEFT;
                body().velocity.x = direction == Direction.LEFT ? -100 : 100;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (onRope()) {
                state = State.ROPE_CLIMB;
                direction = e.getKeyCode() == KeyEvent.VK_UP ? Direction.UP : Direction.DOWN;
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
            if(state != State.JUMP) {
                state = State.IDLE;
            }
            body().velocity.x = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
            if(onRope()) {
                state = State.IDLE;
                body().velocity.y = 0;
            }
        }
    }

    private boolean onRope() {
        return ropeTile != null;
    }

    private void updateContacts() {
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
                } else if((tile.type.equals("floor") || tile.type.equals("ceiling")) &&
                        Math.abs(tile.position().x - position().x) <= BODY_WIDTH2 + tile.sprite.size().width / 2.0
                ) {
                    if(tile.position().y < position().y) {
                        inContactCeiling = true;
                    } else {
                        inContactFloor = true;
                    }
                }
            } else if (b.node() instanceof Column) {
                Column column = (Column)b.node();
                if (column.isMoving()) {
                    // Remove column velocity inertia
                    body().velocity.y = 0.0;
                    if(Math.abs(column.position().x - position().x) <= BODY_WIDTH2 + column.size().width / 2.0) {
                        if(column.position().y < position().y) {
                            inContactColTop = true;
                        } else {
                            inContactColBottom = true;
                        }
                    }
                }
            } else if (b.node() instanceof Enemy) {
                var enemy = (Enemy) b.node();
                if (!enemy.isEating()) {
                    if (radish != null) {
                        enemy.feed();
                        radish.remove();
                        radish = null;
                        owner().addChild(new Radish()).position().set(position().x, position().y);
                        state = State.PUT_RADISH;
                    } else {
                        state = State.DIE;
                    }
                }
            } else if (b.node() instanceof Bomb) {
                b.node().remove();
                // Add score;
            } else if (b.node() instanceof Radish) {
                if (radish == null) {
                    b.node().remove();
                    radish = addChild(new Radish());
                    radish.position().set(0, 10);
                }
            }
        }
    }

    private void updateAnimations() {
        String anim = "idle";
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
            case CRUSH -> anim = "crushed";
            case PUT_RADISH -> anim = "putRadish";
            case DIE -> anim = "dead";
            case IDLE -> anim = onRope() ? "idleRope" : "idle";
        }
        flipH(false);
        if(direction == Direction.RIGHT) {
            flipH(true);
        }
        if(!currentAnimation().equals(anim) || !isPlaying() || isPlayingBackwards() != backward) {
            play(anim, backward);
        }
    }
}
