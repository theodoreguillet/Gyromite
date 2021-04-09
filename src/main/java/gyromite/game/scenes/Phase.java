package gyromite.game.scenes;

import gyromite.core.Rect2;
import gyromite.core.Size2;
import gyromite.core.resources.tilemap.TileObject;
import gyromite.game.Game;
import gyromite.game.nodes.*;
import gyromite.scene.Camera;
import gyromite.scene.Scene;
import gyromite.scene.SceneRoot;
import gyromite.scene.map.TiledMap;

public class Phase extends SceneRoot {
    private final int phase;
    private Counters counters = null;
    private int bombCount = 0;

    private static final int BOMB_REWARD = 100;
    private static final int SMICK_REWARD = 500;

    public Phase(Scene scene, int phase) {
        super(scene);
        this.phase = phase;
    }

    public void bombRemoved() {
        incrementScore(BOMB_REWARD);
        bombCount--;
        if(bombCount == 0) {
            end(true);
        }
    }
    public void smickDead() {
        incrementScore(SMICK_REWARD);
    }

    public void end(boolean success) {
        if(success) {
            incrementScore(10 * counters.timeLeft());
        }
        // Pause and delay
        ((Game)scene()).phaseEnded(success);
    }

    @Override
    protected void init() {
        super.init();

        scene().physics().gravity.set(0, 100);

        bombCount = 0;

        var tiledmap = addChild(new TiledMap(String.format("phase_%02d", phase)))
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
                    bombCount++;
                    return bomb;
                });
        tiledmap.build();

        Size2 mapSize = tiledmap.size();
        double gameHeight = mapSize.height + 32.0; // Space for top counters offset

        var camera = scene().camera();
        camera.setSize(new Size2(gameHeight * 4.0 / 3.0, gameHeight));
        camera.setStretchMode(Camera.StretchMode.KEEP_ASPECT);
        camera.setBounds(new Rect2(
                tiledmap.position().x - mapSize.width / 2.0,
                tiledmap.position().y - mapSize.height / 2.0,
                tiledmap.position().x + mapSize.width / 2.0,
                tiledmap.position().y + mapSize.height / 2.0
        ));

        counters = addChild(new Counters());

        scene().audio().play("game_a");
    }

    private Column createColumn(TiledMap tiledmap, TileObject object, Column.Type type) {
        Object isTop = object.getProperty("top");
        var col = new Column(tiledmap, object.x, object.y, object.width, object.height,
                type, !(isTop instanceof Boolean) || (Boolean)isTop);
        return col;
    }

    private void incrementScore(int score) {
        if(counters != null) {
            counters.incrementScore(score);
        }
    }
}
