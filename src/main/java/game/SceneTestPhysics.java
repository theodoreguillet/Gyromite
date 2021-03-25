package game;

import core.Vector2;
import scene.Entity;
import scene.FPSViewer;
import scene.Scene;
import scene.physics.Body;
import scene.physics.CircleShape;
import scene.physics.PolygonShape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SceneTestPhysics extends Scene {
    private Window window;

    private static final double GAME_HEIGHT = 500;

    @Override
    protected void preload() {

    }

    @Override
    protected void init() {
        window = new Window(800, 600, "Test", this);

        FPSViewer fps = new FPSViewer(this);

        Entity floor = new Entity(this);
        floor.position().set(0, 0);
        floor.setBody(new PolygonShape(200, 10), Body.Mode.STATIC);

        Entity circle = new Entity(this);
        circle.position().set(0, -200);
        circle.setBody(new CircleShape(10), Body.Mode.RIGID);

        for(int i = 0; i < 10; i++) {
            Entity circle1 = new Entity(this);
            circle1.position().set(100 -i * 10, -200 - 20 * i);
            circle1.setBody(new CircleShape(10), Body.Mode.RIGID);
        }

        camera().setPosition(new Vector2(0, 0));
        camera().setZoom(new Vector2(1, 1));

        input().addListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Entity entity = new Entity(SceneTestPhysics.this);
                entity.position().set(camera().getWorldCoordinate(e.getX(), e.getY()));
                if(e.getButton() == 1) {
                    entity.setBody(new PolygonShape(10, 10), Body.Mode.CHARACTER);
                } else {
                    entity.setBody(new CircleShape(10), Body.Mode.RIGID);
                }
            }
            @Override
            public void mousePressed(MouseEvent e) { }
            @Override
            public void mouseReleased(MouseEvent e) { }
            @Override
            public void mouseEntered(MouseEvent e) { }
            @Override
            public void mouseExited(MouseEvent e) { }
        });

        setRenderPhysics(true);
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

    }
}
