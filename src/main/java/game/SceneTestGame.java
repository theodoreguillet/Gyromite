package game;

import core.Vector2;
import scene.Camera;
import scene.Node;
import scene.FPSViewer;
import scene.Scene;
import scene.physics.Body;
import scene.physics.PolygonShape;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

class TestNode extends Node {
    public TestNode() {
        super();
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

class TestNode2 extends Node {
    public TestNode2() {
        super();
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

    private TestNode test;
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

        FPSViewer fps = root().addChild(new FPSViewer());
        test = root().addChild(new TestNode());

        test.position().x = 100;
        test.position().y = GAME_HEIGHT / 2;

        var test1 = root().addChild(new TestNode());
        test1.position().y = GAME_HEIGHT - 50;
        var test2 = root().addChild(new TestNode());
        test2.position().y = 50;

        var test2Child = test2.addChild(new TestNode2());
        test2Child.setPosition(100, 0);
        test2Child.setBody(new PolygonShape(20, 20), Body.Mode.RIGID);
        test2.setOrient(Math.PI / 4.0);

        var wall = root().addChild(new Node());
        wall.setPosition(200, test2Child.worldPosition().y);
        wall.setBody(new PolygonShape(10, 100), Body.Mode.STATIC);

        var floor = root().addChild(new Node());
        floor.setPosition(0, 400);
        floor.setBody(new PolygonShape(400, 10), Body.Mode.STATIC);

        testSpawn();

        camera().setZoom(new Vector2(2, 2));
        camera().setStretchMode(Camera.StretchMode.KEEP_ASPECT);
        camera().position().set(0, 250);

        camera().follow(test2Child);

        setRenderPhysics(true);
    }

    @Override
    protected void preRender(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fill(new Rectangle2D.Double(0, 0, viewport().getWidth(), viewport().getHeight()));
        /*
        g.setColor(Color.BLACK);

        g.fill(new Rectangle2D.Double(0, 0, viewport().getWidth(), viewport().getHeight()));
        camera().transform(g);

        g.setColor(Color.WHITE);
        g.fill(new Rectangle2D.Double(camera().position().x - camera().size().width / 2.0,
                camera().position().y - camera().size().height / 2.0,
                camera().size().width, camera().size().height));
         */
    }

    @Override
    protected void postRender(Graphics2D g) {
        double vw = viewport().getWidth();
        double vh = viewport().getHeight();
        g.setColor(Color.BLACK);
        g.draw(new Line2D.Double(0.0, vh / 2.0, vw, vh / 2.0));
        g.draw(new Line2D.Double(vw / 2.0, 0.0, vw / 2.0, vh));
    }

    @Override
    protected void preUpdate() {
        // double zoom = viewport().getHeight() / GAME_HEIGHT;
        // camera().setZoom(new Vector2(zoom, zoom));
    }

    @Override
    protected void postUpdate() {
        if(test.position().x - lastTestSpawnX > 200) {
            testSpawn();
        }
    }

    private void testSpawn() {
        lastTestSpawnX = test.position().x;
        root().addChild(new TestNode2())
                .setPosition(new Vector2(test.position().x + 200, test.position().y + 100));
        root().addChild(new TestNode2())
                .setPosition(new Vector2(test.position().x + 200, test.position().y - 100));
        root().addChild(new TestNode2())
                .setPosition(new Vector2(test.position().x + 400, test.position().y + 100));
        root().addChild(new TestNode2())
                .setPosition(new Vector2(test.position().x + 400, test.position().y - 100));
    }
}
