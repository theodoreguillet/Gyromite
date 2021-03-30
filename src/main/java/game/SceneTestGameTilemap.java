package game;

import core.MathUtils;
import core.Rect2;
import core.Size2;
import core.Vector2;
import scene.*;
import scene.map.Tile;
import scene.map.TiledMap;
import scene.physics.Body;
import scene.physics.BodyListener;
import scene.physics.CircleShape;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SceneTestGameTilemap extends Scene {
    private static class Player extends Node implements KeyListener, BodyListener {
        public Player() {
            super();
        }

        @Override
        public void init() {
            scene().input().addListener(this);
            setBody(new CircleShape(10), Body.Mode.CHARACTER).addBodyListener(this);
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

        @Override
        public void bodyEntered(Body b) {
            if(b.node().owner() instanceof Tile) {
                Tile tile = (Tile) b.node().owner();
                if(tile.type.equals("rope")) {
                    position().x = tile.position().x + 2;
                }
            }
        }

        @Override
        public void bodyExited(Body b) {

        }
    }

    private Window window;
    private FPSViewer fps;

    @Override
    protected void preload() {
        resources().loadImage("/img/test.jpg", "test");
        resources().loadTilemap("/tilemaps/phase_01.json", "phase_01");
    }

    @Override
    protected void init() {
        window = new Window(800, 600, "Test", this);

        fps = new FPSViewer();
        fps.setColor(Color.WHITE);

        Player player = root().addChild(new Player());
        // player.body().gravity.set(0, 0);

        var tiledmap = root().addChild(new TiledMap("phase_01"))
                .enableCollisions(1, 2, 3, 4, 5, 6, 14)
                .enableAreas("rope");
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
        camera().follow(player, new Rect2(-150, -150, 150, 150));

        setRenderPhysics(true);
    }

    @Override
    protected void preRender(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, viewport().getWidth(), viewport().getHeight());
    }

    @Override
    protected void postRender(Graphics2D g) {
        fps.render(g);
    }

    @Override
    protected void postUpdate() {
        fps.update();
    }
}
