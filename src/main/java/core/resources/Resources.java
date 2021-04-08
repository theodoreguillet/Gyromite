package core.resources;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.resources.tilemap.TileMapData;
import core.resources.tilemap.TileSet;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The resources manager.
 */
public class Resources {
    private final Map<String, BufferedImage> images = new HashMap<>();
    private final Map<String, TileMapData> tilemaps = new HashMap<>();
    private final Map<String, Font> fonts = new HashMap<>();
    private final Map<String, Clip> audios = new HashMap<>();

    /**
     * Load an image.
     */
    public boolean loadImage(String name, String id) {
        var img = loadImage(name);
        if(img == null) {
            return false;
        }
        images.put(id, img);
        return true;
    }

    /**
     * Load a font.
     * Only ttf is supported.
     */
    public boolean loadFont(String name, String id) {
        var stream = getClass().getResourceAsStream(name);
        if(stream == null) {
            System.err.println("Font not found: " + name);
            return false;
        }
        try {
            var font = Font.createFont(Font.TRUETYPE_FONT, stream);
            fonts.put(id, font);
            return true;
        } catch (IOException | FontFormatException e) {
            e.printStackTrace(System.err);
        }
        return false;
    }

    /**
     * Load an audio.
     * Only wav is supported.
     */
    public boolean loadAudio(String name, String id) {
        var stream = getClass().getResourceAsStream(name);
        if(stream == null) {
            System.err.println("Audio not found: " + name);
            return false;
        }
        try {
            var audio = AudioSystem.getAudioInputStream(stream);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            audios.put(id, clip);
            return true;
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace(System.err);
        }
        return false;
    }

    /**
     * Load a json tilemap.
     */
    public boolean loadTilemap(String name, String id) {
        var url = getClass().getResource(name);
        if(url == null) {
            System.err.println("Tilemap not found: " + name);
            return false;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        try {
            var tilemap = mapper.readValue(url, TileMapData.class);
            loadTileSets(name, tilemap);
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
    public TileMapData getTilemap(String id) {
        return tilemaps.get(id);
    }
    public Font getFont(String id) {
        return fonts.get(id);
    }
    public Clip getAudio(String id) {
        return audios.get(id);
    }

    private BufferedImage loadImage(String name) {
        var url = getClass().getResource(name);
        if(url == null) {
            System.err.println("Image not found: " + name);
            return null;
        }
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    private void loadTileSets(String name, TileMapData tilemap) {
        String dir = getResourceDir(name);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

        var it = tilemap.tilesets.listIterator();
        while (it.hasNext()) {
            var tileset = it.next();
            if(tileset.source == null) {
                continue;
            }
            String path = dir.concat(tileset.source.replace('\\', '/'));
            var url = getClass().getResource(path);
            if(url == null) {
                it.remove();
                continue;
            }
            try {
                var loadedTileset = mapper.readValue(url, TileSet.class);
                loadedTileset.firstgid = tileset.firstgid;
                it.set(loadedTileset);
            } catch (IOException e) {
                e.printStackTrace(System.err);
                it.remove();
            }
        }
    }
    private void loadTileMapImages(String name, TileMapData tilemap) {
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
