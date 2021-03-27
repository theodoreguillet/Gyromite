package core.resources;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.resources.tilemap.TileMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Resources {
    private final Map<String, BufferedImage> images = new HashMap<>();
    private final Map<String, TileMap> tilemaps = new HashMap<>();

    public boolean loadImage(String name, String id) {
        var img = loadImage(name);
        if(img == null) {
            return false;
        }
        images.put(id, img);
        return true;
    }

    public boolean loadTilemap(String name, String id) {
        var url = getClass().getResource(name);
        if(url == null) {
            return false;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        try {
            var tilemap = mapper.readValue(url, TileMap.class);
            loadTileMapImages(name, tilemap);
            tilemaps.put(id, tilemap);
            return true;
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return false;
    }

    public BufferedImage getImage(String id) {
        return images.get(id);
    }
    public TileMap getTilemap(String id) {
        return tilemaps.get(id);
    }

    private BufferedImage loadImage(String name) {
        var url = getClass().getResource(name);
        if(url == null) {
            return null;
        }
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    private void loadTileMapImages(String name, TileMap tilemap) {
        String dir = getResourceDir(name);
        for(var tileset : tilemap.tilesets) {
            String imageName = tileset.image.replace('\\', '/');
            tileset.loadedImage = loadImage(dir.concat(imageName));
        }
    }

    private String getResourceDir(String name) {
        int dirIdx = name.lastIndexOf('/');
        return dirIdx != -1
                ? name.substring(0, dirIdx).concat("/")
                : name.charAt(0) == '/' ? "/" : "";
    }
}
