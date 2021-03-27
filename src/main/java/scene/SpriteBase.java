package scene;

import core.MathUtils;
import core.Size;
import core.Vector2;

import java.awt.*;

public class SpriteBase extends Entity {
    private Size size = new Size();
    private Vector2 offset = new Vector2();
    private boolean flipH = false;
    private boolean flipV = false;
    private boolean flipD = false; // Anti diagonally flipping (for tile rotation)
    private double opacity = 1.0;

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
    public boolean isFlipD() {
        return flipD;
    }
    public double opacity() {
        return opacity;
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
    public void flipD(boolean flipD) {
        this.flipD = flipD;
    }
    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);

        if(size.width == 0 || size.height == 0 || opacity <= 0.0) {
            return;
        }

        if(opacity < 1.0) {
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)opacity);
            g.setComposite(ac);
        }

        g.translate(offset.x, offset.y);
        g.scale(flipH ? -1 : 1, flipV ? -1 : 1);
        g.rotate(flipD ? -MathUtils.PI / 2.0 : 0.0);
        g.scale(flipD ? -1 : 1, 1);
    }
}
