package scene;

import core.Vector2;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class Camera {
    private final Scene scene;
    private Vector2 position = new Vector2();
    private Vector2 offset = new Vector2();
    private Vector2 zoom = new Vector2(1, 1);
    private Node followed = null;

    public Camera(Scene scene) {
        this.scene = scene;
    }

    public Vector2 position() {
        return position;
    }
    public Vector2 offset() {
        return offset;
    }
    public Vector2 zoom() {
        return zoom;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
    public void setOffset(Vector2 offset) {
        this.offset = offset;
    }
    public void setZoom(Vector2 zoom) {
        this.zoom = zoom;
    }

    public void transform(Graphics2D g) {
        g.setTransform(getTransform());
    }

    public void follow(Node node) {
        followed = node;
    }

    public void update() {
        if(this.followed != null) {
            this.position.x = followed.position().x;
            this.position.y = followed.position().y;
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
        double width = this.scene.viewport().getWidth();
        double height = this.scene.viewport().getHeight();

        AffineTransform at = new AffineTransform();

        // Zoom
        at.translate((width - width * zoom.x) / 2, (height - height * zoom.y) / 2);
        at.scale(zoom.x, zoom.y);

        // Center camera
        at.translate(width / 2 + offset.x, height / 2 + offset.y);

        // Move camera
        at.translate(-position.x, -position.y);

        return at;
    }
}
