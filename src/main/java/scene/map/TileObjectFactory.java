package scene.map;

import core.resources.tilemap.Layer;
import core.resources.tilemap.TileObject;
import scene.Entity;

public interface TileObjectFactory {
    Entity create(TileMapBuilder builder, TileObject object, Layer objectLayer);
}
