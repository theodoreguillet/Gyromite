package scene;

import core.Rect2;
import core.Size2;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * A sprite node.
 * Displays an image. The image can be a region of a larger
 * atlas texture or a frame from a sprite sheet.
 */
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

    /**
     * @return The region of the atlas image to display.
     */
    public Rect2 region() {
        return region;
    }
    /**
     * @return The number of columns in the sprite sheet.
     */
    public int hframes() {
        return hframes;
    }
    /**
     * @return The number of rows in the sprite sheet
     */
    public int vframes() {
        return vframes;
    }
    /**
     * @return The position of the frame to display from the sprite sheet.
     */
    public int frame() {
        return frame;
    }

    /**
     * Set the image to display
     * @param image The image to display
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }
    /**
     * Set the image to display from a resource
     * @param id The id of the image in the resources
     */
    public void setImage(String id) {
        setImage(scene().resources().getImage(id));
    }
    /**
     * Set the region of the atlas image to display.
     * @param region The coordinates of the region in the atlas image.
     */
    public void setRegion(Rect2 region) {
        this.region = region;
    }
    /**
     * Set the number of columns in the sprite sheet.
     * @param hframes The number of columns.
     */
    public void setHframes(int hframes) {
        this.hframes = hframes;
    }
    /**
     * Set the number of rows in the sprite sheet.
     * @param vframes The number of rows.
     */
    public void setVframes(int vframes) {
        this.vframes = vframes;
    }
    /**
     * Set the position of the frame to display from the sprite sheet.
     * @param frame The position of the frame.
     */
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
            Size2 regionSize = region.size();
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
