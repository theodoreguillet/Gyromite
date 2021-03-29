package scene;

import core.Rect2;
import core.Size;
import core.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Camera of the {@link Scene}, displays from a point of view inside the {@link Viewport}.
 */
public class Camera {
    /**
     * Stretch mode of the camera rendering inside the viewport
     * {@link StretchMode#DISABLED} by default.
     */
    public enum StretchMode {
        /**
         * No stretching.
         */
        DISABLED,
        /**
         * Ignore the aspect ratio when stretching the screen.
         */
        IGNORE_ASPECT,
        /**
         * Keep aspect ratio when stretching the screen.
         */
        KEEP_ASPECT,
        /**
         * Keep aspect ratio and expend height.
         */
        KEEP_WIDTH,
        /**
         * Keep aspect ratio and expend width.
         */
        KEEP_HEIGHT,
        /**
         * Keep aspect ratio and expend with and height.
         */
        EXPAND
    }

    private final Scene scene;
    private final Vector2 position = new Vector2();
    private final Size size = new Size(800, 600);
    private final Vector2 offset = new Vector2();
    private final Vector2 zoom = new Vector2(1, 1);
    private final Rect2 bounds = new Rect2();
    private Node followed = null;
    private final Rect2 followBox = new Rect2();
    private StretchMode stretchMode = StretchMode.DISABLED;

    public Camera(Scene scene) {
        this.scene = scene;
    }

    /**
     * The position of the camera in the world.
     * The coordinates correspond to the center point of the camera.
     * @return The position
     */
    public Vector2 position() {
        return position;
    }
    /**
     * @return The size of
     */
    public Size size() {
        return size;
    }
    public Vector2 offset() {
        return offset;
    }
    public Vector2 zoom() {
        return zoom;
    }
    public Rect2 bounds() {
        return bounds;
    }
    public StretchMode stretchMode() {
        return stretchMode;
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }
    public void setSize(Size size) {
        this.size.set(size);
    }
    public void setOffset(Vector2 offset) {
        this.offset.set(offset);
    }
    public void setZoom(Vector2 zoom) {
        this.zoom.set(zoom);
    }
    public void setZoom(double zoom) {
        this.zoom.set(zoom, zoom);
    }
    public void setBounds(Rect2 bounds) {
        this.bounds.set(bounds == null ? new Rect2() : bounds);
    }
    public void removeBounds() {
        setBounds(null);
    }
    public void setStretchMode(StretchMode stretchMode) {
        this.stretchMode = stretchMode;
    }

    public void transform(Graphics2D g) {
        g.setTransform(getTransform());
    }

    public void follow(Node node) {
        follow(node, new Rect2());
    }
    public void follow(Node node, Rect2 box) {
        followed = node;
        followBox.set(box);
    }

    public void update() {
        if(followed != null) {
            Vector2 pos = followed.worldPosition();
            Vector2 min = followBox.min.add(position);
            Vector2 max = followBox.max.add(position);
            if(pos.x < min.x) {
                position.x += pos.x - min.x;
            } else if(pos.x > max.x) {
                position.x += pos.x - max.x;
            }
            if(pos.y < min.y) {
                position.y += pos.y - min.y;
            } else if(pos.y > max.y) {
                position.y += pos.y - max.y;
            }
            if(!bounds.isEmpty()) {
                position.maxi(position, bounds.min);
                position.mini(position, bounds.max);
            }
        }
    }

    public Vector2 getWorldCoordinate(Vector2 screen) {
        return getWorldCoordinate(screen.x, screen.y);
    }

    public Vector2 getWorldCoordinate(double screenX, double screenY) {
        AffineTransform at = getTransform();
        try {
            Point2D coordinates = at.inverseTransform(new Point2D.Double(screenX, screenY), new Point2D.Double());
            return new Vector2(coordinates.getX(), coordinates.getY());
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace(System.err);
        }
        return new Vector2();
    }

    public Vector2 getScreenCoordinate(Vector2 world) {
        return getScreenCoordinate(world.x, world.y);
    }

    public Vector2 getScreenCoordinate(double worldX, double worldY) {
        AffineTransform at = getTransform();
        Point2D coordinates = at.transform(new Point2D.Double(worldX, worldY), new Point2D.Double());
        return new Vector2(coordinates.getX(), coordinates.getY());
    }

    public AffineTransform getTransform() {
        AffineTransform at = new AffineTransform();

        if(size.width == 0 || size.height == 0) {
            return at;
        }

        double viewportWidth = scene.viewport().getWidth();
        double viewportHeight = scene.viewport().getHeight();
        Vector2 scaleFactor = getScaleFactor();

        // Center camera
        at.translate(viewportWidth / 2 + offset.x, viewportHeight / 2 + offset.y);

        // Zoom
        at.scale(zoom.x * scaleFactor.x, zoom.y * scaleFactor.y);

        // Move camera
        at.translate(-position.x, -position.y);

        return at;
    }

    /**
     * Draw black bars following {@link StretchMode}
     */
    protected void drawBlackBars(Graphics2D g) {
        if(stretchMode == StretchMode.EXPAND || stretchMode == StretchMode.IGNORE_ASPECT) {
            return;
        }

        g.setTransform(new AffineTransform());
        Vector2 scaleFactor = getScaleFactor();
        double width = size.width * scaleFactor.x;
        double height = size.height * scaleFactor.y;
        double viewportWidth = scene.viewport().getWidth();
        double viewportHeight = scene.viewport().getHeight();
        double hdw = Math.max(0, viewportWidth - width) / 2.0;
        double hdh = Math.max(0, viewportHeight - height) / 2.0;

        g.setColor(Color.BLACK);

        if(stretchMode == StretchMode.KEEP_ASPECT || stretchMode == StretchMode.KEEP_WIDTH) {
            g.fill(new Rectangle2D.Double(0, 0, hdw, height));
            g.fill(new Rectangle2D.Double(hdw + width, 0, hdw, height));
        }
        if(stretchMode == StretchMode.KEEP_ASPECT || stretchMode == StretchMode.KEEP_HEIGHT) {
            g.fill(new Rectangle2D.Double(0, 0, width, hdh));
            g.fill(new Rectangle2D.Double(0, hdh + height, width, hdh));
        }
    }

    private Vector2 getScaleFactor() {
        if(stretchMode == StretchMode.DISABLED) {
            return new Vector2(1, 1);
        }
        double factorX = (double)scene.viewport().getWidth() / size.width;
        double factorY = (double)scene.viewport().getHeight() / size.height;
        if(stretchMode.ordinal() >= StretchMode.KEEP_ASPECT.ordinal()) {
            factorX = factorY = Math.min(factorX, factorY);
        }
        return new Vector2(factorX, factorY);
    }
}
