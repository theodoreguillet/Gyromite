package gyromite.core.resources.tilemap;

import java.util.ArrayList;

public class TileMapData extends TileElement {
    public static final String ORTHOGONAL = "orthogonal";
    public int width, height;
    public int tilewidth, tileheight;
    public boolean infinite;
    public String orientation;
    public String type;
    public ArrayList<TileSet> tilesets;
    public ArrayList<Layer> layers;
}
