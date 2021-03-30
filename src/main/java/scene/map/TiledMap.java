package scene.map;

import core.Rect2;
import core.Size2;
import core.Vector2;
import core.resources.tilemap.*;
import scene.*;
import scene.physics.Body;
import scene.physics.CircleShape;
import scene.physics.PolygonShape;
import scene.physics.Shape;
import util.Pair;

import java.util.*;

/**
 * A tile map.
 */
public class TiledMap extends Node {
    private static final int FLIPPED_HORIZONTALLY_FLAG = 0x80000000;
    private static final int FLIPPED_VERTICALLY_FLAG   = 0x40000000;
    private static final int FLIPPED_DIAGONALLY_FLAG   = 0x20000000;

    private final String tilemapResourceId;
    private TileMapData tilemap = null;

    private final HashMap<Integer, Boolean> collidingTilesIds = new HashMap<>();
    private final HashMap<String, Boolean> collidingTilesTypes = new HashMap<>();

    private final HashMap<Pair<Integer, Integer>, TileObjectFactory> objectFactoriesById = new HashMap<>();
    private final HashMap<Pair<Integer, String>, TileObjectFactory> objectFactoriesByType = new HashMap<>();

    public TiledMap(String tilemap) {
        this.tilemapResourceId = tilemap;
    }

    @Override
    public void init() {
        this.tilemap = scene().resources().getTilemap(tilemapResourceId);
    }

    public TileMapData tilemap() {
        return tilemap;
    }
    public Size2 size() {
        return new Size2(tilemap.width * tilemap.tilewidth, tilemap.height * tilemap.tileheight);
    }

    public TiledMap enableCollisions(Integer... tileIds) {
        for(var tileId : tileIds) {
            collidingTilesIds.put(tileId, false);
        }
        return this;
    }
    public TiledMap enableCollisions(String... tileTypes) {
        for(var tileType : tileTypes) {
            collidingTilesTypes.put(tileType, false);
        }
        return this;
    }
    public TiledMap enableAreas(Integer... tileIds) {
        for(var tileId : tileIds) {
            collidingTilesIds.put(tileId, true);
        }
        return this;
    }
    public TiledMap enableAreas(String... tileTypes) {
        for(var tileType : tileTypes) {
            collidingTilesTypes.put(tileType, true);
        }
        return this;
    }

    public TiledMap setObjectFactory(String layer, int id, TileObjectFactory factory) {
        return setObjectFactory(getLayerId(layer), id, factory);
    }
    public TiledMap setObjectFactory(int layer, int id, TileObjectFactory factory) {
        objectFactoriesById.put(new Pair<>(layer, id), factory);
        return this;
    }
    public TiledMap setObjectFactory(String layer, String type, TileObjectFactory factory) {
        return setObjectFactory(getLayerId(layer), type, factory);
    }
    public TiledMap setObjectFactory(int layer, String type, TileObjectFactory factory) {
        objectFactoriesByType.put(new Pair<>(layer, type), factory);
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
        if(tilemap.infinite || !tilemap.orientation.equals(TileMapData.ORTHOGONAL)) {
            System.err.println("Unsupported tilemap format");
            return;
        }

        for(var layer : tilemap.layers) {
            if (layer.visible) {
                switch (layer.type) {
                    case Layer.TILE -> buildTileLayer(layer);
                    case Layer.OBJECT -> buildObjectLayer(layer);
                }
            }
        }
    }

    public Vector2 getObjectPosition(TileObject object) {
        return getMapPosition(new Vector2(object.x + object.width / 2.0, object.y + object.height / 2.0));
    }

    public Vector2 getMapPosition(Vector2 localPos) {
        double hw = (double)tilemap.width * (double)tilemap.tilewidth / 2.0;
        double hh = (double)tilemap.height * (double)tilemap.tileheight / 2.0;
        return localPos.sub(new Vector2(hw, hh));
    }

    private void buildTileLayer(Layer layer) {
        if(layer.data == null) {
            return;
        }
        for(int i = 0; i < layer.data.size(); i++) {
            buildTile(layer, i, layer.data.get(i));
        }
    }

    private void buildTile(Layer layer, int tileMapIdx, long tileGidRaw) {
        // Read out the flags
        boolean hflip = (tileGidRaw & FLIPPED_HORIZONTALLY_FLAG) != 0;
        boolean vflip = (tileGidRaw & FLIPPED_VERTICALLY_FLAG) != 0;
        boolean dflip = (tileGidRaw & FLIPPED_DIAGONALLY_FLAG) != 0;

        // Clear the flags
        int tileGid = (int) (
                tileGidRaw & ~(FLIPPED_HORIZONTALLY_FLAG | FLIPPED_VERTICALLY_FLAG | FLIPPED_DIAGONALLY_FLAG));

        // Resolve the tile
        var tileset = getTileSet(tileGid);
        if (tileset == null) {
            return;
        }

        int tileId = tileGid - tileset.firstgid;

        Rect2 mapRect = getTileMapRect(layer, tileMapIdx);
        if(mapRect == null) {
            return;
        }

        Tile tile;

        TileData tileData = getTile(tileset, tileId);
        if(tileData != null && tileData.animation != null && !tileData.animation.isEmpty()) {
            AnimatedSprite animatedSprite = new AnimatedSprite();
            tile = addChild(new Tile(animatedSprite, layer.id, tileId, tileGid, tileData.type));
            var anim = animatedSprite.addAnimation("tile");
            double totalDuration = 0.0;
            for(var frame : tileData.animation) {
                var frameTileRect = getTileRect(tileset, frame.tileid);
                if(frameTileRect == null) {
                    return;
                }

                anim.addFrame(tileset.loadedImage, frameTileRect).loop(true);
                totalDuration += frame.duration;
            }
            anim.setSpeed(1000.0 / totalDuration); // We don't support variable duration per frame
            animatedSprite.play("tile");
        } else {
            Rect2 tileRect = getTileRect(tileset, tileId);
            if(tileRect == null) {
                return;
            }

            Sprite staticSprite = new Sprite(tileset.loadedImage);
            tile = addChild(new Tile(staticSprite, layer.id, tileId, tileGid,
                    tileData != null ? tileData.type : ""));
            staticSprite.setRegion(tileRect);
        }

        Vector2 localPos = mapRect.center();
        tile.position().set(getMapPosition(localPos));

        SpriteBase sprite = tile.sprite;
        sprite.size().set(mapRect.size());
        sprite.setOpacity(layer.opacity);
        sprite.flipH(hflip);
        sprite.flipV(vflip);
        sprite.flipD(dflip);

        Boolean collidingById = collidingTilesIds.get(tileGid);
        Boolean collidingByType = tileData == null ? null : collidingTilesTypes.get(tileData.type);
        if(collidingById != null || collidingByType != null) {
            boolean transparentArea = (collidingById != null && collidingById) || collidingByType != null;
            if(tileData != null && tileData.objectgroup != null && !tileData.objectgroup.objects.isEmpty()) {
                for(var shapeObject : tileData.objectgroup.objects) {
                    Shape shape;
                    if (shapeObject.ellipse) {
                        shape = new CircleShape(shapeObject.width / 2.0);
                    } else if (shapeObject.polygon != null) {
                        shape = new PolygonShape(shapeObject.polygon.toArray(new Vector2[0]));
                    } else {
                        shape = new PolygonShape(shapeObject.width / 2.0, shapeObject.height / 2.0);
                    }
                    tile.addCollisionShape(shape, new Vector2(
                            shapeObject.x + (shapeObject.width - sprite.size().width) / 2.0,
                            shapeObject.y + (shapeObject.height - sprite.size().height) / 2.0
                    ), transparentArea);
                }
            } else {
                sprite.setBody(new PolygonShape(sprite.size().width / 2, sprite.size().height / 2),
                        transparentArea ? Body.Mode.TRANSPARENT : Body.Mode.STATIC);
            }
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

    private Rect2 getTileRect(TileSet tileset, int tileId) {
        int w = tileset.tilewidth;
        int h = tileset.tileheight;
        int nx = tileset.imagewidth / w;
        int ny = tileset.imageheight / h;
        int frame = tileId;
        int frameX = frame % nx;
        int frameY = frame / nx;
        if(frameY < ny) {
            int x1 = tileset.margin + frameX * (w + tileset.spacing);
            int y1 = tileset.margin + frameY * (h + tileset.spacing);
            int x2 = x1 + w;
            int y2 = y1 + h;

            if (x2 <= tileset.loadedImage.getWidth() && y2 <= tileset.loadedImage.getHeight()) {
                return new Rect2(x1, y1, x2, y2);
            }
        }
        return null;
    }

    private TileData getTile(TileSet tileset, int tileId) {
        for(var tile : tileset.tiles) {
            if(tile.id == tileId) {
                return tile;
            }
        }
        return null;
    }

    private Rect2 getTileMapRect(Layer layer, int tileMapIdx) {
        int w = tilemap.tilewidth;
        int h = tilemap.tileheight;
        int nx = layer.width;
        int ny = layer.height;
        int frameX = tileMapIdx % nx;
        int frameY = tileMapIdx / nx;
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
                factory = objectFactoriesByType.get(new Pair<>(layer.id, object.type));
            }
            if(factory != null) {
                factory.create(this, object, layer);
            }
        }
    }
}
