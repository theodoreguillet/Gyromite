package game;

import core.MathUtils;
import core.Rect2;
import core.Size2;
import core.Vector2;
import scene.*;
import scene.map.TiledMap;
import scene.physics.Body;
import scene.physics.CircleShape;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SceneTestGameTilemap extends Scene {
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
        resources().loadTilemap("/tilemaps/phase_01.json", "phase_01");
    }

    @Override
    protected void init() {
        window = new Window(800, 600, "Test", this);

        FPSViewer fps = root().addChild(new FPSViewer());

        Player player = root().addChild(new Player());
        player.body().gravity.set(0, 0);

        var tiledmap = root().addChild(new TiledMap("phase_01"));
                /*.setObjectFactory(2, 3, (builder, object, layer) -> {
                    Sprite s = builder.scene().root().addChild(new Sprite("test"));
                    s.setPosition(builder.getObjectPosition(object));
                    s.size().set(object.width, object.height);
                    s.setOpacity(layer.opacity);
                    return s;
                })
                .enableCollisions(46, 40)
                .enableCollisions(25, 32)
                .enableCollisions("cactus")*/
        tiledmap.build();

        Size2 mapSize = tiledmap.size();
        camera().setSize(new Size2(mapSize.height * 4.0 / 3.0, mapSize.height));
        camera().setStretchMode(Camera.StretchMode.KEEP_ASPECT);
        camera().setBounds(new Rect2(
                tiledmap.position().x - mapSize.width / 2.0,
                tiledmap.position().y - mapSize.height / 2.0,
                tiledmap.position().x + mapSize.width / 2.0,
                tiledmap.position().y + mapSize.height / 2.0
        ));
        camera().follow(player);

        setRenderPhysics(true);
    }

    @Override
    protected void preRender(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, viewport().getWidth(), viewport().getHeight());
    }

    @Override
    protected void preUpdate() {
        // double zoom = viewport().getHeight() / GAME_HEIGHT;
        // camera().setZoom(new Vector2(zoom, zoom));
    }
}
