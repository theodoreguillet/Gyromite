package scene;

import core.MathUtils;
import core.Size2;
import core.Vector2;

import java.awt.*;

/**
 * Base class of sprite nodes
 */
public class SpriteBase extends Node {
    private Size2 size = new Size2();
    private Vector2 offset = new Vector2();
    private boolean flipH = false;
    private boolean flipV = false;
    private boolean flipD = false; // Anti diagonally flipping (for tile rotation)
    private double opacity = 1.0;

    public SpriteBase() {
        super();
    }

    /**
     * @return The size of the sprite. The image is stretched to this size.
     */
    public Size2 size() {
        return size;
    }
    /**
     * @return The offset of the image with respect to the sprite node position.
     */
    public Vector2 offset() {
        return offset;
    }
    /**
     * @return Return <code>true</code> if the image is flipped horizontally.
     */
    public boolean isFlipH() {
        return flipH;
    }
    /**
     * @return Return <code>true</code> if the image is flipped vertically.
     */
    public boolean isFlipV() {
        return flipV;
    }
    /**
     * @return Return <code>true</code> if the image is flipped anti diagonally.
     */
    public boolean isFlipD() {
        return flipD;
    }
    /**
     * @return The opacity of the image when rendered.
     */
    public double opacity() {
        return opacity;
    }

    /**
     * Set the size of the sprite. The image is stretched to this size.
     * @param size The size of the sprite.
     */
    public void setSize(Size2 size) {
        this.size = size;
    }
    /**
     * Set the offset of the image with respect to the sprite node position.
     * @param offset The offset values.
     */
    public void setOffset(Vector2 offset) {
        this.offset = offset;
    }
    /**
     * Set whether the image is horizontally flipped or not.
     */
    public void flipH(boolean flipH) {
        this.flipH = flipH;
    }
    /**
     * Set whether the image is vertically flipped or not.
     */
    public void flipV(boolean flipV) {
        this.flipV = flipV;
    }
    /**
     * Set whether the image is anti diagonally flipped or not.
     */
    public void flipD(boolean flipD) {
        this.flipD = flipD;
    }
    /**
     * Set the opacity of the image when rendered.
     */
    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    @Override
    protected void render(Graphics2D g) {
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
