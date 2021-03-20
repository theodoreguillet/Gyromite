package game;

import scene.FPSViewer;
import scene.Scene;
import scene.Sprite;

import java.awt.Graphics2D;
import java.awt.Color;

public class Game extends Scene {
    private Window window;

    public Game() {
        super();
    }

    @Override
    protected void preload() {

    }

    @Override
    protected void init() {
        window = new Window(500, 500, "Test", this);

        FPSViewer fps = new FPSViewer(this);
        Sprite sprite = new Sprite(this);
        // sprite.position().x += 100;
        // sprite.position().y += 50;

        // camera().position().x = sprite.position().x;
        // camera().position().y = sprite.position().y;
        // camera().setZoom(new Vector2(20, 20));
    }

    @Override
    protected void preRender(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, viewport().getWidth(), viewport().getHeight());
        g.setColor(Color.WHITE);
        g.fillRect(5, 5, viewport().getWidth() - 10, viewport().getHeight() - 10);
    }

    @Override
    protected void postRender(Graphics2D g) {
        //
    }
}
