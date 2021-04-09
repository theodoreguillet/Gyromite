package gyromite.scene.map;

import gyromite.core.Vector2;
import gyromite.scene.Node;
import gyromite.scene.SpriteBase;
import gyromite.scene.physics.Body;
import gyromite.scene.physics.Shape;

/**
 * A tile of the {@link TiledMap}.
 */
public class Tile extends Node {
    public final SpriteBase sprite;
    public final int layerId, id, gid, mapIndex;
    public final String type;

    public Tile(SpriteBase sprite, int layerId, int id, int gid, int mapIndex, String type) {
        super();
        this.sprite = addChild(sprite);
        this.layerId = layerId;
        this.id = id;
        this.gid = gid;
        this.mapIndex = mapIndex;
        this.type = type;
    }

    public int getX() {
        if(!(owner() instanceof TiledMap)) {
            return 0;
        }
        return mapIndex % ((TiledMap)owner()).tilemap().layers.get(layerId).width;
    }
    public int getY() {
        if(!(owner() instanceof TiledMap)) {
            return 0;
        }
        return mapIndex / ((TiledMap)owner()).tilemap().layers.get(layerId).width;
    }


    public void addCollisionShape(Shape shape, Vector2 pos, boolean transparent) {
        var collision = addChild(new Node());
        collision.setBody(shape, transparent ? Body.Mode.TRANSPARENT : Body.Mode.STATIC);
        collision.setPosition(pos);
    }
}
