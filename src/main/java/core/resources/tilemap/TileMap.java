package core.resources.tilemap;

import java.util.ArrayList;

public class TileMap extends TileElement {
    public int width, height;
    public int tilewidth, tileheight;
    public boolean infinite;
    public String orientation;
    public String type;
    public ArrayList<TileSet> tilesets;
    public ArrayList<Layer> layers;
}
