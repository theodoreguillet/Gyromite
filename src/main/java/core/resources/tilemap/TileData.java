package core.resources.tilemap;

import java.util.ArrayList;

public class TileData extends TileElement {
    public int id;
    public String type = "";
    public Layer objectgroup = null; // Collision shapes
    public ArrayList<TileFrame> animation = null;
}
