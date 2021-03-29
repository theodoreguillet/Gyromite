package scene;

import core.Rect2;
import core.Size;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Sprite extends SpriteBase {
    private BufferedImage image;
    private String imageId = null;

    private Rect2 region = new Rect2();
    private int hframes = 1;
    private int vframes = 1;
    private int frame = 0;

    public Sprite() {
        super();
        image = null;
    }

    public Sprite(BufferedImage image) {
        super();
        this.image = image;
    }

    public Sprite(String image) {
        super();
        this.imageId = image;
    }

    public Rect2 region() {
        return region;
    }
    public int hframes() {
        return hframes;
    }
    public int vframes() {
        return vframes;
    }
    public int frame() {
        return frame;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
    public void setImage(String id) {
        setImage(scene().resources().getImage(id));
    }
    public void setRegion(Rect2 region) {
        this.region = region;
    }
    public void setHframes(int hframes) {
        this.hframes = hframes;
    }
    public void setVframes(int vframes) {
        this.vframes = vframes;
    }
    public void setFrame(int frame) {
        this.frame = frame;
    }

    @Override
    protected void init() {
        if(imageId != null) {
            this.image = scene().resources().getImage(imageId);
        }
    }

    @Override
    protected void render(Graphics2D g) {
        super.render(g);

        if(image == null) {
            return;
        }

        BufferedImage subImage;
        if(region.isEmpty()) {
            if(vframes > 1 || hframes > 1 && frame < vframes * hframes) {
                int frameX = frame % hframes;
                int frameY = frame / hframes;
                int x = frameX * image.getWidth() / hframes;
                int y = frameY * image.getHeight() / vframes;
                int w = image.getWidth() / hframes;
                int h = image.getHeight() / vframes;
                subImage = image.getSubimage(x, y, w, h);
            } else {
                subImage = image;
            }
        } else {
            Size regionSize = region.size();
            subImage = image.getSubimage(
                    (int)region.min.x, (int)region.min.y,
                    (int)regionSize.width, (int)regionSize.height);
        }

        // Smooth render of the image
        g.translate(-size().width / 2.0, -size().height / 2.0);
        g.scale(size().width / (double)subImage.getWidth(), size().height / (double)subImage.getHeight());

        g.drawImage(subImage, 0, 0, null);
    }
}
