package game;

import core.MathUtils;
import core.Rect2;
import core.Vector2;
import scene.*;
import scene.physics.Body;
import scene.physics.PolygonShape;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SceneTestSprite extends Scene {
    private Window window;

    private AnimatedSprite ninja;

    private static final double GAME_HEIGHT = 500;

    public SceneTestSprite() {
        super();
    }

    @Override
    protected void preload() {
        resources().loadImage("/img/test.jpg", "test");
        resources().loadImage("/img/ninja.png", "ninja");
    }

    @Override
    protected void init() {
        window = new Window(800, 600, "Test", this);

        FPSViewer fps = root().addChild(new FPSViewer());

        Sprite background = root().addChild(new Sprite("test"));
        background.size().set(400.0 * 1920.0 / 1080.0, 400.0);

        ninja = root().addChild(new AnimatedSprite());
        ninja.size().set(200.0, 200.0);
        ninja.addAnimation("run")
                .addFrames("ninja", 6, 3, 6, 11)
                .setSpeed(50)
                .loop(true);
        ninja.addAnimation("idle")
                .addFrames("ninja", 6, 3, 0, 2)
                .setSpeed(2)
                .loop(true);
        ninja.addAnimation("attack")
                .addFrame("ninja", new Rect2(0, 750, 400, 1100))
                .addFrame("ninja", new Rect2(400, 750, 740, 1100))
                .addFrame("ninja", new Rect2(740, 750, 1210, 1100))
                .addFrame("ninja", new Rect2(1210, 750, 1760, 1100))
                .setSpeed(15)
                .loop(false);

        ninja.play("idle");

        camera().setZoom(new Vector2(1, 1));

        input().addListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == ' ') {
                    if(ninja.currentAnimation().equals("idle")) {
                        ninja.play("run");
                    } else if(ninja.currentAnimation().equals("run")) {
                        ninja.play("attack");
                    } else {
                        ninja.play("idle");
                    }
                }
            }
            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) { }
        });
    }

    @Override
    protected void preRender(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, viewport().getWidth(), viewport().getHeight());
        g.setColor(Color.WHITE);
        g.fillRect(5, 5, viewport().getWidth() - 10, viewport().getHeight() - 10);
    }

    @Override
    protected void preUpdate() {
        double zoom = viewport().getHeight() / GAME_HEIGHT;
        camera().setZoom(new Vector2(zoom, zoom));

        if(!ninja.isPlaying()) {
            ninja.play("idle");
        }
    }
}
