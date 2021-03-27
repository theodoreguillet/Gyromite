package core.resources.tilemap;

import java.util.ArrayList;

public class Layer extends TileElement {
    public final static String TILE = "tilelayer";
    public final static String OBJECT = "objectgroup";

    public int id;
    public String name = "";
    public String type = "";
    public int x, y;
    public int width, height;
    public int offsetx, offsety;
    public int parallaxx, parallaxy;
    public boolean visible = true;
    public double opacity = 1.0;
    public ArrayList<Long> data = null;
    public ArrayList<TileObject> objects = null;
}
