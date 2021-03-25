package scene;

import core.Rect2;
import core.Size;
import core.Vector2;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Sprite extends Entity {
    private BufferedImage image;

    private Size size = new Size();
    private Vector2 offset = new Vector2();
    private boolean flipH = false;
    private boolean flipV = false;
    private Rect2 region = new Rect2();
    private int hframes = 1;
    private int vframes = 1;
    private int frame = 0;

    public Sprite(Scene scene) {
        super(scene);
        image = null;
    }

    public Sprite(Scene scene, BufferedImage image) {
        super(scene);
        setImage(image);
    }

    public Sprite(Scene scene, String image) {
        super(scene);
        setImage(image);
    }

    public Size size() {
        return size;
    }
    public Vector2 offset() {
        return offset;
    }
    public boolean isFlipH() {
        return flipH;
    }
    public boolean isFlipV() {
        return flipV;
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
    public void setSize(Size size) {
        this.size = size;
    }
    public void setOffset(Vector2 offset) {
        this.offset = offset;
    }
    public void flipH(boolean flipH) {
        this.flipH = flipH;
    }
    public void flipV(boolean flipV) {
        this.flipV = flipV;
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
    public void setframe(int frame) {
        this.frame = frame;
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);

        if(image == null) {
            return;
        }

        int hw = (int)size.width / 2;
        int hh = (int)size.height / 2;

        int sx1, sy1, sx2, sy2;
        if(region.isEmpty()) {
            if(vframes > 1 || hframes > 1 && frame < vframes * hframes) {
                int frameX = frame % hframes;
                int frameY = frame / hframes;
                sx1 = frameX * image.getWidth() / hframes;
                sy1 = frameY * image.getHeight() / vframes;
                sx2 = (frameX + 1) * image.getWidth() / hframes;
                sy2 = (frameY + 1) * image.getHeight() / vframes;
            } else {
                sx1 = 0;
                sy1 = 0;
                sx2 = image.getWidth();
                sy2 = image.getHeight();
            }
        } else {
            sx1 = (int)region.min.x;
            sy1 = (int)region.min.y;
            sx2 = (int)region.max.x;
            sy2 = (int)region.max.y;
        }

        g.translate(offset.x, offset.y);

        g.scale(flipH ? -1 : 1, flipV ? -1 : 1);
        g.drawImage(image, -hw, -hh, hw, hh, sx1, sy1, sx2, sy2, null);
        //
    }
}
