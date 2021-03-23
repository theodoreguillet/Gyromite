package core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Resources {
    private Map<String, BufferedImage> images;

    public Resources() {
        images = new HashMap<>();
    }

    public void loadImage(String path, String id) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));
            images.put(id, img);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public BufferedImage getImage(String id) {
        return images.get(id);
    }
}
