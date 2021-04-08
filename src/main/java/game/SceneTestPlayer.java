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
import java.util.ArrayList;

public class SceneTestPlayer extends Scene {

    private Window window;
    private FPSViewer fps;
    private final ArrayList<Column> columns = new ArrayList<>();

    @Override
    protected void preload() {
        resources().loadImage("/img/test.jpg", "test");
        resources().loadImage("/tilemaps/tileset.png", "tileset");
        resources().loadTilemap("/tilemaps/phase_01.json", "phase_01");
        resources().loadImage("/img/player.png", "player");
        resources().loadImage("/img/enemy.png", "enemy");
        resources().loadImage("/img/particles.png", "particles");
        resources().loadImage("/img/bomb.png", "bomb");
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

       // Player player = root().addChild(new Player());

        // Enemy enemy = root().addChild(new Enemy());

        var tiledmap = root().addChild(new TiledMap("phase_01"))
                .enableCollisions(1, 2, 3, 4, 5, 6, 14)
                .enableAreas("rope")
                .setObjectFactory("columns", "blue", (tm, object, objectLayer) ->
                        createColumn(tm, object, Column.Type.BLUE))
                .setObjectFactory("columns", "red", (tm, object, objectLayer) ->
                        createColumn(tm, object, Column.Type.RED))
                .setObjectFactory("characters", "smick", (tm, object, objectLayer) -> {
                    var enemy = new Enemy();
                    enemy.position().set(object.x + object.width / 2.0, object.y + object.height / 2.0);
                    return enemy;
                })
                .setObjectFactory("characters", "player", (tm, object, objectLayer) -> {
                    var player = new Player();
                    player.position().set(object.x + object.width / 2.0, object.y + object.height / 2.0);
                    return player;
                }).setObjectFactory("consumables", "radish", (tm, object, objectLayer) -> {
                    var radish = new Radish(true);
                    radish.position().set(object.x + object.width / 2.0, object.y + object.height / 2.0);
                    return radish;
                }).setObjectFactory("consumables", "bomb", (tm, object, objectLayer) -> {
                    var bomb = new Bomb();
                    bomb.position().set(object.x + object.width / 2.0, object.y + object.height / 2.0);
                    return bomb;
                });
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
      //  camera().follow(player, new Rect2(-150, -150, 150, 150));

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
