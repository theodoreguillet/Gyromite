package scene;

import core.Rect2;
import core.Size2;
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
    private final Size2 size = new Size2(800, 600);
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
     * @return The size of camera in the world.
     */
    public Size2 size() {
        return size;
    }
    /**
     * @return The offset of the camera in the viewport.
     *         Can be used to make a shake effect.
     */
    public Vector2 offset() {
        return offset;
    }
    /**
     * @return The zoom scale of the camera.
     */
    public Vector2 zoom() {
        return zoom;
    }
    /**
     * @return The bounds of the camera in the world.
     */
    public Rect2 bounds() {
        return bounds;
    }
    /**
     * @return The stretch mode. See {@link StretchMode}.
     *         By default {@link StretchMode#DISABLED}.
     */
    public StretchMode stretchMode() {
        return stretchMode;
    }

    /**
     * Set the position of the camera in the world.
     * The coordinates correspond to the center point of the camera.
     * @param position The coordinates
     */
    public void setPosition(Vector2 position) {
        this.position.set(position);
    }
    /**
     * Set the size of camera in the world.
     * @param size The size
     */
    public void setSize(Size2 size) {
        this.size.set(size);
    }
    /**
     * Set the offset of the camera in the viewport.
     * Can be used to make a shake effect.
     * @param offset The offset values.
     */
    public void setOffset(Vector2 offset) {
        this.offset.set(offset);
    }
    /**
     * Set the zoom scale of the camera.
     * @param zoom The zoom values.
     */
    public void setZoom(Vector2 zoom) {
        this.zoom.set(zoom);
    }
    /**
     * Set the zoom scale of the camera.
     * @param zoom The zoom value.
     */
    public void setZoom(double zoom) {
        this.zoom.set(zoom, zoom);
    }
    /**
     * Set the bounds of the camera in the world.
     * @param bounds The bounds coordinates in the world.
     */
    public void setBounds(Rect2 bounds) {
        this.bounds.set(bounds == null ? new Rect2() : bounds);
    }
    /**
     * Remove the bounds of the camera in the world.
     */
    public void removeBounds() {
        setBounds(null);
    }
    /**
     * Set the stretch mode. See {@link StretchMode}
     * @param stretchMode The stretch mode.
     */
    public void setStretchMode(StretchMode stretchMode) {
        this.stretchMode = stretchMode;
    }

    /**
     * Apply the camera transformation to the graphics context.
     * @param g The graphics context.
     */
    public void transform(Graphics2D g) {
        g.setTransform(getTransform());
    }

    /**
     * Follow the node.
     * @param node The node to follow.
     */
    public void follow(Node node) {
        follow(node, new Rect2());
    }
    /**
     * Follow the node withing a box.
     * @param node The node to follow.
     * @param box The box.
     */
    public void follow(Node node, Rect2 box) {
        followed = node;
        followBox.set(box);
    }

    /**
     * Transform screen coordinates to world coordinates.
     * @param screen The coordinates on the screen.
     * @return The coordinates in the world.
     */
    public Vector2 getWorldCoordinate(Vector2 screen) {
        return getWorldCoordinate(screen.x, screen.y);
    }
    /**
     * Transform screen coordinates to world coordinates.
     * @param screenX The x coordinate on the screen.
     * @param screenY The y coordinate on the screen.
     * @return The coordinates in the world.
     */
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

    /**
     * Transform world coordinates to screen coordinates.
     * @param world The coordinates in the world.
     * @return The coordinates on the screen.
     */
    public Vector2 getScreenCoordinate(Vector2 world) {
        return getScreenCoordinate(world.x, world.y);
    }
    /**
     * Transform world coordinates to screen coordinates.
     * @param worldX The x coordinate in the world.
     * @param worldY The y coordinate in the world.
     * @return The coordinates on the screen.
     */
    public Vector2 getScreenCoordinate(double worldX, double worldY) {
        AffineTransform at = getTransform();
        Point2D coordinates = at.transform(new Point2D.Double(worldX, worldY), new Point2D.Double());
        return new Vector2(coordinates.getX(), coordinates.getY());
    }

    /**
     * @return The camera transformation.
     */
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
     * Update the camera. Called by {@link Scene}
     */
    protected void update() {
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
