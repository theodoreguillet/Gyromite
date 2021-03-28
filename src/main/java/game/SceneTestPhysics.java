package game;

import core.MathUtils;
import core.Vector2;
import scene.Node;
import scene.FPSViewer;
import scene.Scene;
import scene.physics.Body;
import scene.physics.BodyListener;
import scene.physics.CircleShape;
import scene.physics.PolygonShape;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SceneTestPhysics extends Scene {
    private static class ARNode extends Node {
        public ARNode() {
            super();
        }

        @Override
        public void update() {
            super.update();
            if(position().y > GAME_HEIGHT) {
                remove();
            }
        }
    }
    private static class Player extends ARNode implements KeyListener {
        public Player() {
            super();
        }

        @Override
        protected void init() {
            scene().input().addListener(this);
            setBody(new CircleShape(20), Body.Mode.CHARACTER);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            if(e.getKeyChar() == ' ') {
                // Jump
                body().velocity.y = -80.0;
            }
        }
        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                body().velocity.x = 100.0;
                setOrient(0.0);
            } else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                body().velocity.x = -100.0;
                setOrient(MathUtils.PI);
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
                body().velocity.x = 0;
            }
        }
    }
    private Window window;

    private static final double GAME_HEIGHT = 500;

    @Override
    protected void preload() {

    }

    @Override
    protected void init() {
        window = new Window(800, 600, "Test", this);

        FPSViewer fps = root().addChild(new FPSViewer());

        ARNode floor = root().addChild(new ARNode());
        floor.position().set(0, 200);
        floor.setBody(new PolygonShape(200, 10), Body.Mode.STATIC);

        Player player = root().addChild(new Player());

        ARNode area = root().addChild(new ARNode());
        area.setBody(new PolygonShape(50, 50), Body.Mode.TRANSPARENT)
                .addBodyListener(new BodyListener() {
                    @Override
                    public void bodyEntered(Body b) {
                        if(b.shape instanceof CircleShape && !(b.node() instanceof Player)) {
                            b.node().remove();
                        }
                        System.out.println("Body entered");
                    }

                    @Override
                    public void bodyExited(Body b) {
                        System.out.println("Body exited");
                    }
                });

        camera().setPosition(new Vector2(0, 0));
        camera().setZoom(new Vector2(1, 1));

        input().addListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ARNode entity = root().addChild(new ARNode());
                entity.position().set(camera().getWorldCoordinate(e.getX(), e.getY()));
                if(e.getButton() == MouseEvent.BUTTON1) {
                    entity.setBody(new PolygonShape(10, 10), Body.Mode.CHARACTER);
                } else if(e.getButton() == MouseEvent.BUTTON2) {
                    double r = MathUtils.random(10.0, 50.0);
                    int vertCount = MathUtils.random(3, PolygonShape.MAX_POLY_VERTEX_COUNT);

                    Vector2[] verts = Vector2.arrayOf( vertCount );
                    for (int i = 0; i < vertCount; i++)
                    {
                        verts[i].set( MathUtils.random( -r, r ), MathUtils.random( -r, r ) );
                    }

                    Body b = entity.setBody(new PolygonShape( verts ), Body.Mode.RIGID);
                    entity.setOrient(MathUtils.random( -MathUtils.PI, MathUtils.PI ));
                    b.restitution = 0.2f;
                    b.dynamicFriction = 0.2f;
                    b.staticFriction = 0.4f;
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
