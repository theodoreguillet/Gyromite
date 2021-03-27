package scene.map;

import core.resources.tilemap.TileMap;
import core.resources.tilemap.TileObject;
import scene.Entity;
import scene.Scene;

public interface TileObjectFactory {
    Entity create(Scene scene, TileMap map, TileObject object);
}
