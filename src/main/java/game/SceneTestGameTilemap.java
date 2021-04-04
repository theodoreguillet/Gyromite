package game;

import core.MathUtils;
import core.Rect2;
import core.Size2;
import core.Vector2;
import core.resources.tilemap.TileObject;
import scene.*;
import scene.map.Tile;
import scene.map.TiledMap;
import scene.physics.Body;
import scene.physics.BodyListener;
import scene.physics.CircleShape;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

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
        public void update() {
            Tile ropeTile = null;
            for(var b : body().contacts()) {
                if(b.node().owner() instanceof Tile) {
                    Tile tile = (Tile) b.node().owner();
                    if(tile.type.equals("rope")) {
                        ropeTile = tile;
                    }
                } else if(b.node() instanceof Column) {
                    if(((Column)b.node()).isMoving()) {
                        // Remove column velocity inertia
                        body().velocity.y = 0.0;
                    }
                }
            }
            if(ropeTile != null && body().gravity.y != 0.0) {
                position().x = ropeTile.position().x + 2;
                body().velocity.set(0, 0);
                body().gravity.set(0, 0);
                body().force.set(0, 0);
            } else if(ropeTile == null && body().gravity.y == 0.0) {
                body().resetGravity();
            }
        }

        @Override
        public void keyTyped(KeyEvent e) { }
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

    private static class Counters extends Node {
        @Override
        public void init() {

        }

        @Override
        public void update() {
            var camera = scene().camera();
            position().x = camera.position().x - camera.size().width / 2.0;
            position().y = camera.position().y - camera.size().height / 2.0 + 26.0;
        }

        @Override
        public void render(Graphics2D g) {
            super.render(g);
            Size2 size = scene().camera().size();

            g.scale(size.width / 256.0, size.height / 224.0);

            double w1 = 32.0;
            double w2 = 80.0;

            g.setStroke(new BasicStroke(1));

            g.setColor(Color.RED);
            g.draw(new Line2D.Double(0, 0, w1, 0));
            g.setColor(Color.ORANGE);
            g.draw(new Rectangle2D.Double(-2, 4, w1 + 2, 6));
            g.draw(new Line2D.Double(0, 7, w1, 7));
            g.setColor(Color.RED);
            g.draw(new Line2D.Double(0, 14, w1, 14));

            g.translate(w1, 0);
            g.setColor(Color.BLUE);
            g.draw(new RoundRectangle2D.Double(5, 0, 70, 14, 1, 1));
            g.translate(w2, 0);

            g.setColor(Color.RED);
            g.draw(new Line2D.Double(0, 0, w1, 0));
            g.setColor(Color.ORANGE);
            g.draw(new Rectangle2D.Double(0, 4, w1, 6));
            g.draw(new Line2D.Double(0, 7, w1, 7));
            g.setColor(Color.RED);
            g.draw(new Line2D.Double(0, 14, w1, 14));

            g.translate(w1, 0);
            g.setColor(Color.BLUE);
            g.draw(new RoundRectangle2D.Double(5, 0, 70, 14, 1, 1));
            g.translate(w2, 0);

            g.setColor(Color.RED);
            g.draw(new Line2D.Double(0, 0, w1, 0));
            g.setColor(Color.ORANGE);
            g.draw(new Rectangle2D.Double(0, 4, w1 + 2, 6));
            g.draw(new Line2D.Double(0, 7, w1, 7));
            g.setColor(Color.RED);
            g.draw(new Line2D.Double(0, 14, w1, 14));
        }
    }

    private Window window;
    private FPSViewer fps;
    private TiledMap tiledmap;
    private final ArrayList<Column> columns = new ArrayList<>();

    private int score = 0;
    private int timeLeft = 999;

    @Override
    protected void preload() {
        resources().loadImage("/img/test.jpg", "test");
        resources().loadImage("/tilemaps/tileset.png", "tileset");
        resources().loadTilemap("/tilemaps/phase_01.json", "phase_01");
        for(var layer : resources().getTilemap("phase_01").layers) {
            if(layer.name.equals("columns_demo")) {
                layer.visible = false;
                break;
            }
        }
    }

    @Override
    protected void init() {
        window = new Window(800, 600, "Test", this);

        fps = new FPSViewer();
        fps.setColor(Color.WHITE);

        physics().gravity.set(0, 100);

        Player player = root().addChild(new Player());

        tiledmap = root().addChild(new TiledMap("phase_01"))
                .enableCollisions(1, 2, 3, 4, 5, 6, 14)
                .enableAreas("rope")
                .setObjectFactory("columns", "blue", (tm, object, objectLayer) ->
                        createColumn(tm, object, Column.Type.BLUE))
                .setObjectFactory("columns", "red", (tm, object, objectLayer) ->
                        createColumn(tm, object, Column.Type.RED));
        tiledmap.build();

        Size2 mapSize = tiledmap.size();
        double gameHeight = mapSize.height + 32.0;
        camera().setSize(new Size2(gameHeight * 4.0 / 3.0, gameHeight));
        camera().position().y = 32.0;
        camera().setStretchMode(Camera.StretchMode.KEEP_ASPECT);
        camera().setBounds(new Rect2(
                tiledmap.position().x - mapSize.width / 2.0,
                tiledmap.position().y - mapSize.height / 2.0,
                tiledmap.position().x + mapSize.width / 2.0,
                tiledmap.position().y + mapSize.height / 2.0
        ));
        camera().follow(player, new Rect2(-150, -150, 150, 150));

        root().addChild(new Counters());

        input().addListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == ' ') {
                    for (var col : columns) {
                        col.toggle();
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) { }
        });

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

    private Column createColumn(TiledMap tiledmap, TileObject object, Column.Type type) {
        Object isTop = object.getProperty("top");
        var col = new Column(tiledmap, object.x, object.y, object.width, object.height,
                type, !(isTop instanceof Boolean) || (Boolean)isTop);
        columns.add(col);
        return col;
    }
}
