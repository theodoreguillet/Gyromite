package core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Resources {
    private final Map<String, BufferedImage> images = new HashMap<>();

    public boolean loadImage(String name, String id) {
        if(images.containsKey(id)) {
            return false;
        }
        var url = getClass().getResource(name);
        if(url == null) {
            return false;
        }
        try {
            BufferedImage img = ImageIO.read(url);
            images.put(id, img);
            return true;
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return false;
    }

    public BufferedImage getImage(String id) {
        return images.get(id);
    }
}
