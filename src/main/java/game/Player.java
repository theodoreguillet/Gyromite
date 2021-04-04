package game;

import core.MathUtils;
import scene.AnimatedSprite;
import scene.Node;
import scene.map.Tile;
import scene.physics.Body;
import scene.physics.CircleShape;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends AnimatedSprite implements KeyListener {

    private Node radish;

    public void preload() {
        scene().resources().loadImage("/img/player.png", "player");
    }

    @Override
    public void init() {
        preload();
        radish = null;
        scene().input().addListener(this);
        setBody(new CircleShape(15), Body.Mode.CHARACTER);
        size().set(40.0, 40.0);

        // TDOD : ajouter une animation quand il fait rien
        this.addAnimation("idle")
                .addFrames("player", 6, 5, 0, 0)
                .setSpeed(50)
                .loop(false);
        this.addAnimation("walk")
                .addFrames("player", 6, 5, 0, 4)
                .setSpeed(50)
                .loop(true);
        this.addAnimation("walkWithRadish")
                .addFrames("player", 6, 5, 6, 10)
                .setSpeed(50)
                .loop(true);
        this.addAnimation("putRadish")
                .addFrames("player", 6, 5, 11, 11)
                .setSpeed(50)
                .loop(false);
        this.addAnimation("climb")
                .addFrames("player", 6, 5, 12, 13)
                .setSpeed(10)
                .loop(true);
        this.addAnimation("jump")
                .addFrames("player", 6, 5, 14, 14)
                .setSpeed(50)
                .loop(true);
        this.addAnimation("crushed")
                .addFrames("player", 6, 5, 15, 16)
                .setSpeed(50)
                .loop(false);
        this.addAnimation("dead")
                .addFrames("player", 6, 5, 18, 19)
                .setSpeed(50)
                .loop(false);
        play("idle");
    }

    @Override
    public void update() {
        Tile ropeTile = null;
        boolean isInContactFloor = false;
        for (var b : body().contacts()) {
            if (b.node().owner() instanceof Tile) {
                Tile tile = (Tile) b.node().owner();
                if (tile.type.equals("rope")) {
                    ropeTile = tile;
                } else if(tile.type.equals("floor") || tile.type.equals("ceiling")) {
                    isInContactFloor = true;
                }
            } else if (b.node() instanceof Column) {
                if (((Column) b.node()).isMoving()) {
                    // Remove column velocity inertia
                    body().velocity.y = 0.0;
                    if(isInContactFloor)
                        play("crushed");
                }
            } else if (b.node() instanceof Enemy) {
                if (!((Enemy) b.node()).isEating()) {
                    if (radish != null) {
                        ((Enemy) (b.node())).feed();
                        flipH(false);
                        this.play("putRadish");
                        removeChild(radish);
                        radish = null;
                    } else {
                        play("dead");
                    }
                }
            } else if (b.node() instanceof Bomb) {
                addChild(b.node());
            } else if (b.node() instanceof Radish) {
                if (radish == null) {
                    Radish tmpRadish = (Radish) b.node();
                    addChild(tmpRadish);
                    radish = tmpRadish;
                    play("putRadish");
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


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            body().velocity.x = 100.0;
            setOrient(0.0);
            playHorizontalAnim(true);
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            body().velocity.x = -100.0;
            setOrient(MathUtils.PI);
            playHorizontalAnim(false);
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            if (checkIfOnRope()) {
                body().velocity.y = -100.0;
                setOrient(0.0);
                playVerticalAnim();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (checkIfOnRope()) {
                body().velocity.y = 100.0;
                setOrient(0.0);
                playVerticalAnim();
            }
        }
    }

    public void playHorizontalAnim(boolean toRight) {
        flipH(true);
        flipV(!toRight);
        if (checkIfOnRope()) {
            play("jump", true);
        }
        else
            play("walk", true);
    }

    public void playVerticalAnim() {

        if (checkIfOnRope()) {
            play("climb");
        }
    }

    public boolean checkIfOnRope() {
        for (var b : body().contacts()) {
            if (b.node().owner() instanceof Tile) {
                Tile tile = (Tile) b.node().owner();
                if (tile.type.equals("rope")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
            body().velocity.x = 0;
            play("idle");
        }
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
            body().velocity.y = 0;
        }
    }
}
