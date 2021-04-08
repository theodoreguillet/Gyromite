package game;

import game.scenes.Menu;
import game.scenes.Title;
import scene.Camera;
import scene.Scene;
import scene.SceneRoot;
import scene.Sprite;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameTestTitle extends Scene {
    @Override
    protected void preload() {
        resources().loadImage("/img/title.png", "title");
        resources().loadImage("/img/menu.png", "menu");
        resources().loadImage("/img/enemy.png", "smick");
        resources().loadFont("/fonts/pixel.ttf", "pixel");
    }

    @Override
    protected void init() {
        new Window(800, 600, "Test", this);
        showTitle();
    }

    @Override
    protected void preRender(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, viewport().getWidth(), viewport().getHeight());
    }

    public void showTitle() {
        camera().reset();
        input().removeAllListeners();
        setRoot(new Title(this));
    }

    public void showMenu() {
        camera().reset();
        input().removeAllListeners();
        setRoot(new Menu(this));
    }

    public void showGame() {
        camera().reset();
        input().removeAllListeners();
        setRoot(new SceneRoot(this));
    }
}
