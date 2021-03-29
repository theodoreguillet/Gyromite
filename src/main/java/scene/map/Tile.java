package scene.map;

import core.Vector2;
import scene.Node;
import scene.SpriteBase;
import scene.physics.Body;
import scene.physics.Shape;

/**
 * A tile of the {@link TiledMap}.
 */
public class Tile extends Node {
    public final SpriteBase sprite;
    public final int layerId, id, gid;
    public final String type;

    public Tile(SpriteBase sprite, int layerId, int id, int gid, String type) {
        super();
        this.sprite = addChild(sprite);
        this.layerId = layerId;
        this.id = id;
        this.gid = gid;
        this.type = type;
    }

    @Override
    public void init() {
        super.init();
    }

    public void addCollisionShape(Shape shape, Vector2 pos) {
        var collision = addChild(new Node());
        collision.setBody(shape, Body.Mode.STATIC);
        collision.setPosition(pos);
    }
}
