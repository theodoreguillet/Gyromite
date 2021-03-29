package scene.map;

import core.resources.tilemap.Layer;
import core.resources.tilemap.TileObject;
import scene.Node;

public interface TileObjectFactory {
    Node create(TiledMap tiledmap, TileObject object, Layer objectLayer);
}
