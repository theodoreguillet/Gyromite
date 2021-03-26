package scene;

import core.Size;
import core.Vector2;

import java.awt.Graphics2D;

public class SpriteBase extends Entity {
    private Size size = new Size();
    private Vector2 offset = new Vector2();
    private boolean flipH = false;
    private boolean flipV = false;

    public SpriteBase(Scene scene) {
        super(scene);
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

    @Override
    public void render(Graphics2D g) {
        super.render(g);

        if(size.width == 0 || size.height == 0) {
            return;
        }

        g.translate(offset.x, offset.y);
        g.scale(flipH ? -1 : 1, flipV ? -1 : 1);
    }
}
