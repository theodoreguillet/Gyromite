package core.resources.tilemap;

import core.Vector2;

import java.util.ArrayList;

public class TileObject extends TileElement {
    public int id;
    public String name = "";
    public int gid = 0; // Tile id
    public double x, y;
    public double width, height;
    public String type = "";
    public double rotation = 0.0;
    public boolean visible = true;
    // Shape (rect shape by default)
    public boolean ellipse = false; // Ellipse shape
    public ArrayList<Vector2> polygon = null; // Polygon shape
}
