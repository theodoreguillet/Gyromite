package scene.map;

import core.Rect2;
import core.Vector2;
import core.resources.tilemap.*;
import scene.Scene;
import scene.Sprite;
import scene.physics.Body;
import scene.physics.PolygonShape;
import util.Pair;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class TileMapBuilder {
    private final Scene scene;
    private final TileMap tilemap;

    private final HashSet<Integer> collidingTilesIds = new HashSet<>();
    private final HashSet<String> collidingTilesTypes = new HashSet<>();

    private final HashMap<Pair<Integer, Integer>, TileObjectFactory> objectFactoriesById = new HashMap<>();
    private final HashMap<Pair<Integer, String>, TileObjectFactory> objectFactoriesByName = new HashMap<>();

    private final Vector2 offset = new Vector2();

    public TileMapBuilder(Scene scene, String tilemap) {
        this.scene = scene;
        this.tilemap = scene.resources().getTilemap(tilemap);
    }

    public Scene scene() {
        return scene;
    }
    public TileMap tilemap() {
        return tilemap;
    }
    public Vector2 offset() {
        return offset;
    }

    public TileMapBuilder enableCollisions(int tileId) {
        collidingTilesIds.add(tileId);
        return this;
    }
    public TileMapBuilder enableCollisions(String tileType) {
        collidingTilesTypes.add(tileType);
        return this;
    }

    public TileMapBuilder setObjectFactory(String layer, int id, TileObjectFactory factory) {
        return setObjectFactory(getLayerId(layer), id, factory);
    }
    public TileMapBuilder setObjectFactory(int layer, int id, TileObjectFactory factory) {
        objectFactoriesById.put(new Pair<>(layer, id), factory);
        return this;
    }
    public TileMapBuilder setObjectFactory(String layer, String name, TileObjectFactory factory) {
        return setObjectFactory(getLayerId(layer), name, factory);
    }
    public TileMapBuilder setObjectFactory(int layer, String name, TileObjectFactory factory) {
        objectFactoriesByName.put(new Pair<>(layer, name), factory);
        return this;
    }

    public TileMapBuilder setOffset(double x, double y) {
        offset.set(x, y);
        return this;
    }
    public TileMapBuilder setOffset(Vector2 offset) {
        this.offset.set(offset);
        return this;
    }

    public int getLayerId(String layerName) {
        for(var layer : tilemap.layers) {
            if(layer.name.equals(layerName)) {
                return layer.id;
            }
        }
        throw new IllegalArgumentException(String.format("Layer '%s' does not exists in tilemap", layerName));
    }

    public void build() {
        if(tilemap.infinite) {
            throw new RuntimeException("Infinite tilemap not supported");
        }

        tilemap.tilesets.sort(Comparator.comparingInt(o -> o.firstgid));
        for(var layer : tilemap.layers) {
            switch (layer.type) {
                case Layer.TILE -> buildTileLayer(layer);
                case Layer.OBJECT -> buildObjectLayer(layer);
            }
        }
    }

    public Vector2 getObjectPosition(TileObject object) {
        return getMapPosition(new Vector2(object.x + object.width / 2.0, object.y + object.height / 2.0));
    }

    public Vector2 getMapPosition(Vector2 localPos) {
        double hw = (double)tilemap.width * (double)tilemap.tilewidth / 2.0;
        double hh = (double)tilemap.height * (double)tilemap.tileheight / 2.0;
        return localPos.add(this.offset).sub(new Vector2(hw, hh));
    }

    private void buildTileLayer(Layer layer) {
        if(layer.data == null) {
            return;
        }
        for(int tileIdx = 0; tileIdx < layer.data.size(); tileIdx++) {
            buildTile(layer, tileIdx, layer.data.get(tileIdx));
        }
    }

    private void buildTile(Layer layer, int tileIdx, int tileGid) {
        var tileset = getTileSet(tileGid);
        if (tileset == null) {
            return;
        }

        Rect2 tileRect = getTileRect(tileset, tileGid);
        if(tileRect == null) {
            return;
        }

        Rect2 mapRect = getTileMapRect(layer, tileIdx);
        if(mapRect == null) {
            return;
        }

        Sprite sprite = new Sprite(scene, tileset.loadedImage);

        Vector2 localPos = mapRect.center();
        sprite.position().set(getMapPosition(localPos));

        sprite.size().set(mapRect.size());
        sprite.setRegion(tileRect);
        sprite.setOpacity(layer.opacity);

        Tile tile = getTile(tileset, tileGid);
        if(collidingTilesIds.contains(tileGid) || (tile != null && collidingTilesTypes.contains(tile.type))) {
            sprite.setBody(new PolygonShape(sprite.size().width / 2, sprite.size().height / 2), Body.Mode.STATIC);
        }
    }

    private TileSet getTileSet(int tileGid) {
        for(var tileset : tilemap.tilesets) {
            if (tileset.loadedImage != null &&
                    tileset.firstgid <= tileGid && tileGid < tileset.firstgid + tileset.tilecount
            ) {
                return tileset;
            }
        }
        return null;
    }

    private Rect2 getTileRect(TileSet tileset, int tileGid) {
        int w = tileset.tilewidth;
        int h = tileset.tileheight;
        int nx = tileset.imagewidth / w;
        int ny = tileset.imageheight / h;
        int frame = tileGid - tileset.firstgid;
        int frameX = frame % nx;
        int frameY = frame / nx;
        if(frameY < ny) {
            int x1 = tileset.margin + frameX * (w + tileset.spacing);
            int y1 = tileset.margin + frameY * (h + tileset.spacing);
            int x2 = x1 + w;
            int y2 = y1 + h;

            if (x2 < tileset.loadedImage.getWidth() && y2 < tileset.loadedImage.getHeight()) {
                return new Rect2(x1, y1, x2, y2);
            }
        }
        return null;
    }

    private Tile getTile(TileSet tileset, int tileGid) {
        for(var tile : tileset.tiles) {
            if(tile.id == tileGid) {
                return tile;
            }
        }
        return null;
    }

    private Rect2 getTileMapRect(Layer layer, int tileIdx) {
        int w = tilemap.tilewidth;
        int h = tilemap.tileheight;
        int nx = layer.width;
        int ny = layer.height;
        int frameX = tileIdx % nx;
        int frameY = tileIdx / nx;
        if(frameY < ny) {
            int x1 = layer.offsetx + frameX * w;
            int y1 = layer.offsety + frameY * h;
            int x2 = x1 + w;
            int y2 = y1 + h;
            return new Rect2(x1, y1, x2, y2);
        }
        return null;
    }

    private void buildObjectLayer(Layer layer) {
        if(layer.objects == null) {
            return;
        }

        for(var object : layer.objects) {
            var factory = objectFactoriesById.get(new Pair<>(layer.id, object.id));
            if(factory == null) {
                factory = objectFactoriesByName.get(new Pair<>(layer.id, object.name));
            }
            if(factory != null) {
                factory.create(this, object, layer);
            }
        }
    }
}
