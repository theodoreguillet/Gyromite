package gyromite.scene.map;

import gyromite.core.resources.tilemap.Layer;
import gyromite.core.resources.tilemap.TileObject;
import gyromite.scene.Node;

public interface TileObjectFactory {
    Node create(TiledMap tiledmap, TileObject object, Layer objectLayer);
}
