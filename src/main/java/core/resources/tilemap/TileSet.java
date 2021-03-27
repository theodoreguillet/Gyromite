package core.resources.tilemap;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TileSet extends TileElement {
    public String name = "";
    public String image = ""; // Relative path to image
    public int firstgid;
    public int columns;
    public int imagewidth, imageheight;
    public int tilecount;
    public int tilewidth, tileheight;
    public int spacing;
    public int margin;
    public ArrayList<TileData> tiles = new ArrayList<>();

    @JsonIgnore
    public BufferedImage loadedImage = null;
}
