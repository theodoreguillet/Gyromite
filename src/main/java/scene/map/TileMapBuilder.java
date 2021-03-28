package scene.map;

import core.Rect2;
import core.Vector2;
import core.resources.tilemap.*;
import scene.*;
import scene.physics.Body;
import scene.physics.CircleShape;
import scene.physics.PolygonShape;
import scene.physics.Shape;
import util.Pair;

import java.util.*;

public class TileMapBuilder {
    private static final int FLIPPED_HORIZONTALLY_FLAG = 0x80000000;
    private static final int FLIPPED_VERTICALLY_FLAG   = 0x40000000;
    private static final int FLIPPED_DIAGONALLY_FLAG   = 0x20000000;

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

    public TileMapBuilder enableCollisions(Integer... tileIds) {
        collidingTilesIds.addAll(Arrays.asList(tileIds));
        return this;
    }
    public TileMapBuilder enableCollisions(String... tileTypes) {
        collidingTilesTypes.addAll(Arrays.asList(tileTypes));
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
        if(tilemap.infinite || !tilemap.orientation.equals(TileMap.ORTHOGONAL)) {
            System.err.println("Unsupported tilemap format");
            return;
        }

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

        SpriteBase sprite;

        TileData tile = getTile(tileset, tileId);
        if(tile != null && tile.animation != null && !tile.animation.isEmpty()) {
            AnimatedSprite animatedSprite = scene.root().addChild(new AnimatedSprite());
            var anim = animatedSprite.addAnimation("tile");
            double totalDuration = 0.0;
            for(var frame : tile.animation) {
                var frameTileRect = getTileRect(tileset, frame.tileid);
                if(frameTileRect == null) {
                    return;
                }

                anim.addFrame(tileset.loadedImage, frameTileRect).loop(true);
                totalDuration += frame.duration;
            }
            anim.setSpeed(1000.0 / totalDuration); // We don't support variable duration per frame
            animatedSprite.play("tile");
            sprite = animatedSprite;
        } else {
            Rect2 tileRect = getTileRect(tileset, tileId);
            if(tileRect == null) {
                return;
            }

            Sprite staticSprite = scene.root().addChild(new Sprite(tileset.loadedImage));
            staticSprite.setRegion(tileRect);
            sprite = staticSprite;
        }

        Vector2 localPos = mapRect.center();
        sprite.position().set(getMapPosition(localPos));

        sprite.size().set(mapRect.size());
        sprite.setOpacity(layer.opacity);
        sprite.flipH(hflip);
        sprite.flipV(vflip);
        sprite.flipD(dflip);

        if(collidingTilesIds.contains(tileGid) || (tile != null && collidingTilesTypes.contains(tile.type))) {
            if(tile != null && tile.objectgroup != null && !tile.objectgroup.objects.isEmpty()) {
                for(var shapeObject : tile.objectgroup.objects) {
                    Shape shape;
                    if (shapeObject.ellipse) {
                        shape = new CircleShape(shapeObject.width / 2.0);
                    } else if (shapeObject.polygon != null) {
                        shape = new PolygonShape(shapeObject.polygon.toArray(new Vector2[0]));
                    } else {
                        shape = new PolygonShape(shapeObject.width / 2.0, shapeObject.height / 2.0);
                    }
                    var collisionShape = scene.root().addChild(new Node());
                    collisionShape.setBody(shape, Body.Mode.STATIC);
                    collisionShape.position().x = sprite.position().x - sprite.size().width / 2.0
                            + shapeObject.x + shapeObject.width / 2.0;
                    collisionShape.position().y = sprite.position().y - sprite.size().height / 2.0
                            + shapeObject.y + shapeObject.height / 2.0;
                }
            } else {
                sprite.setBody(new PolygonShape(sprite.size().width / 2, sprite.size().height / 2), Body.Mode.STATIC);
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

            if (x2 < tileset.loadedImage.getWidth() && y2 < tileset.loadedImage.getHeight()) {
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
                factory = objectFactoriesByName.get(new Pair<>(layer.id, object.name));
            }
            if(factory != null) {
                factory.create(this, object, layer);
            }
        }
    }
}
