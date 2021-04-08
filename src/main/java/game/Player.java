package game;

import core.MainLoop;
import core.Rect2;
import core.Vector2;
import scene.AnimatedSprite;
import scene.Scene;
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
        CRUSHED,
        DIE
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

    public static void preload(Scene scene) {
        scene.resources().loadImage("/img/player.png", "player");
    }

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
            if (state != State.CRUSHING && state != State.CRUSHED) {
                state = State.CRUSHING;
                crushDelay = 1;
            }
        }

        if (state == State.JUMP && onFloor && body().velocity.x == 0) {
            state = State.IDLE;
            body().velocity.x = 0;
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
                state = State.CRUSHED;
            }
        }

        updateAnimations();

        if (state == State.CRUSHED) {
            scene().setPaused(true);
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyChar() == 'p') {
            scene().setPaused(true);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (state == State.IDLE && onFloor) {
                state = State.WALK;
                direction = e.getKeyCode() == KeyEvent.VK_RIGHT ? Direction.RIGHT : Direction.LEFT;
            } else if (onRope() && state != State.JUMP) {
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
        if (e.getKeyChar() == 'a') {
            scene().setPaused(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
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

        for (var b : body().contacts()) {
            if (b.node().owner() instanceof Tile) {
                Tile tile = (Tile) b.node().owner();
                if (tile.type.equals("rope")) {
                    ropeTile = tile;
                } else if ((tile.type.equals("floor") || tile.type.equals("ceiling")) &&
                        Math.abs(tile.position().x - position().x) <= BODY_WIDTH2 + tile.sprite.size().width / 2.0) {
                    if (tile.position().y < position().y) {
                        topTile = tile;
                    } else {
                        bottomTile = tile;
                    }
                }
            } else if (b.node() instanceof Column) {
                Column column = (Column) b.node();
                // Remove column velocity inertia
                body().velocity.y = 0.0;
                if (Math.abs(column.position().x - position().x) <= BODY_WIDTH2 + column.size().width / 2.0) {
                    if (column.position().y < position().y) {
                        topColumn = column;
                    } else {
                        bottomColumn = column;
                    }
                }
            } else if (b.node() instanceof Enemy) {
                var enemy = (Enemy) b.node();
                if (!enemy.isEating()) {
                    if (radish != null) {
                        enemy.feed(radish);
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

        if(bottomTile != null || bottomColumn != null) {
            double bottomTopY = bottomTile != null
                    ? bottomTile.position().y - bottomTile.sprite.size().height / 2.0
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
            if(dst1 < BODY_WIDTH2 + topTile.sprite.size().width / 2.0 - topTile.sprite.size().width / 10.0 &&
                    dst2 < BODY_WIDTH2 + bottomColumn.size().width / 2.0 - bottomColumn.size().width / 10.0
            ) {
                double topBottomY = topTile.position().y + topTile.sprite.size().height / 2.0;
                double bottomTopY = bottomColumn.position().y - bottomColumn.size().height / 2.0;
                if(topBottomY <= bottomTopY) { // y axe is inverted
                    crushSpacing = bottomTopY - topBottomY;
                }
            }
        } else if(topColumn != null && bottomTile != null) {
            double dst1 = Math.abs(bottomTile.position().x - position().x);
            double dst2 = Math.abs(topColumn.position().x - position().x);
            if(dst1 < BODY_WIDTH2 + bottomTile.sprite.size().width / 2.0 - bottomTile.sprite.size().width / 10.0 &&
                    dst2 < BODY_WIDTH2 + topColumn.size().width / 2.0 - topColumn.size().width / 10.0
            ) {
                double topBottomY = topColumn.position().y + topColumn.size().height / 2.0;
                double bottomTopY = bottomTile.position().y - bottomTile.sprite.size().height / 2.0;
                if(topBottomY <= bottomTopY) { // y axe is inverted
                    crushSpacing = bottomTopY - topBottomY;
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
            case CRUSHING -> anim = crushSpacing > 25 ? "crushing" : "crushed";
            case CRUSHED -> anim = null;
            case PUT_RADISH -> anim = "putRadish";
            case DIE -> anim = "dead";
            case IDLE -> anim = onRope() ? "idleRope" : "idle";
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
}
