package scene.map;

import core.resources.tilemap.Layer;
import core.resources.tilemap.TileObject;
import scene.Node;

public interface TileObjectFactory {
    Node create(TileMapBuilder builder, TileObject object, Layer objectLayer);
}
