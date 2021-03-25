package game;

import core.MathUtils;
import core.Vector2;
import scene.Entity;
import scene.FPSViewer;
import scene.Scene;
import scene.Sprite;
import scene.physics.Body;
import scene.physics.PolygonShape;

import java.awt.*;

public class SceneTestSprite extends Scene {
    private Window window;

    private Sprite sprite;
    private long lastTime = 0;

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

        FPSViewer fps = new FPSViewer(this);

        sprite = new Sprite(this, "ninja");
        sprite.setVframes(3);
        sprite.setHframes(6);
        sprite.size().set(200, 200);
        sprite.flipH(true);
        // sprite.region().set(100, 100, 500, 500);

        camera().setZoom(new Vector2(1, 1));
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

        if(System.currentTimeMillis() - lastTime > 100) {
            lastTime = System.currentTimeMillis();
            sprite.setframe(6 + (sprite.frame() + 1) % 6);
        }
    }
}
