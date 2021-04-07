package game;

import core.Vector2;
import scene.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SceneTestSpritePlayer extends Scene {
    private Window window;

    private AnimatedSprite player;

    private static final double GAME_HEIGHT = 500;

    public SceneTestSpritePlayer() {
        super();
    }

    @Override
    protected void preload() {
        resources().loadImage("/img/test.jpg", "test");
        resources().loadImage("/img/player.png", "player");
    }

    @Override
    protected void init() {
        window = new Window(800, 600, "Test", this);

        FPSViewer fps = root().addChild(new FPSViewer());

        Sprite background = root().addChild(new Sprite("test"));
        background.size().set(400.0 * 1920.0 / 1080.0, 400.0);

        player = root().addChild(new AnimatedSprite());
        player.size().set(200.0, 200.0);
        player.addAnimation("climb")
                .addFrames("player", 6, 5, 12, 13)
                .setSpeed(10)
                .loop(true);
        player.addAnimation("idle")
                .addFrames("player", 6, 5, 0, 0)
                .setSpeed(50)
                .loop(false);
        player.addAnimation("jump")
                .addFrames("player", 6, 5, 14, 14)
                .setSpeed(50)
                .loop(true);
        player.play("idle");

        camera().setZoom(new Vector2(1, 1));

        input().addListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == ' ') {
                    if(player.currentAnimation().equals("idle")) {
                        player.play("jump");
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

        if(!player.isPlaying()) {
            player.play("idle");
        }
    }
}
