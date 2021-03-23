package game;

import core.Vector2;
import scene.Entity;
import scene.FPSViewer;
import scene.Scene;

import java.awt.*;

class TestEntity extends Entity {
    public TestEntity(Scene scene) {
        super(scene);
    }

    @Override
    public void update() {
        super.update();

        position().x += 1;
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);

        g.setColor(Color.RED);
        g.fillRect(-50, -50, 100, 100);
        g.setColor(Color.BLACK);
        g.fillRect(-45, -45, 90, 90);

        g.setColor(Color.BLUE);
        g.fillRect(-2, -2, 4, 4);
    }
}

class TestEntity2 extends Entity {
    public TestEntity2(Scene scene) {
        super(scene);
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);

        g.setColor(Color.GRAY);
        g.fillRect(-20, -20, 40, 40);

        g.setColor(Color.RED);
        g.fillRect(-2, -2, 4, 4);
    }
}

public class SceneTestGame extends Scene {
    private Window window;

    private TestEntity test;
    private double lastTestSpawnX = 0;

    private static final double GAME_HEIGHT = 500;

    public SceneTestGame() {
        super();
    }

    @Override
    protected void preload() {

    }

    @Override
    protected void init() {
        window = new Window(800, 600, "Test", this);

        FPSViewer fps = new FPSViewer(this);
        test = new TestEntity(this);

        test.position().x = 100;
        test.position().y = GAME_HEIGHT / 2;

        var test1 = new TestEntity(this);
        test1.position().y = GAME_HEIGHT - 50;
        var test2 = new TestEntity(this);
        test2.position().y = 50;

        testSpawn();

        camera().setZoom(new Vector2(1, 1));

        camera().follow(test);
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
    }

    @Override
    protected void postUpdate() {
        if(test.position().x - lastTestSpawnX > 200) {
            testSpawn();
        }
    }

    private void testSpawn() {
        lastTestSpawnX = test.position().x;
        new TestEntity2(this).setPosition(new Vector2(test.position().x + 200, test.position().y + 100));
        new TestEntity2(this).setPosition(new Vector2(test.position().x + 200, test.position().y - 100));
        new TestEntity2(this).setPosition(new Vector2(test.position().x + 400, test.position().y + 100));
        new TestEntity2(this).setPosition(new Vector2(test.position().x + 400, test.position().y - 100));
    }
}