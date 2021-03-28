package game;

import core.MathUtils;
import core.Vector2;
import scene.Node;
import scene.FPSViewer;
import scene.Scene;
import scene.Sprite;
import scene.map.TileMapBuilder;
import scene.physics.Body;
import scene.physics.CircleShape;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SceneTestTilemap extends Scene {
    private static class Player extends Node implements KeyListener {
        public Player() {
            super();
        }

        @Override
        public void init() {
            scene().input().addListener(this);
            setBody(new CircleShape(10), Body.Mode.CHARACTER);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            if(e.getKeyChar() == ' ') {
                // Jump
                // body().velocity.y = -80.0;
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
            } else if(e.getKeyCode() == KeyEvent.VK_UP) {
                body().velocity.y = -100.0;
                setOrient(-MathUtils.PI / 2.0);
            } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                body().velocity.y = 100.0;
                setOrient(MathUtils.PI / 2.0);
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
                body().velocity.x = 0;
            }
            if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                body().velocity.y = 0;
            }
        }
    }

    private Window window;

    private static final double GAME_HEIGHT = 500;

    @Override
    protected void preload() {
        resources().loadImage("/img/test.jpg", "test");
        resources().loadTilemap("/tilemaps/test.json", "testmap");
    }

    @Override
    protected void init() {
        window = new Window(800, 600, "Test", this);

        FPSViewer fps = root().addChild(new FPSViewer());

        physics().gravity.set(0.0, 0.0);

        Player player = root().addChild(new Player());

        camera().setZoom(new Vector2(1, 1));

        new TileMapBuilder(this, "testmap")
                .setObjectFactory(2, 3, (builder, object, layer) -> {
                    Sprite s = builder.scene().root().addChild(new Sprite("test"));
                    s.setPosition(builder.getObjectPosition(object));
                    s.size().set(object.width, object.height);
                    s.setOpacity(layer.opacity);
                    return s;
                })
                .enableCollisions(46, 40)
                .enableCollisions(25, 32)
                .enableCollisions("cactus")
                .build();

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
}
