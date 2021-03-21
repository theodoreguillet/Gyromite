package scene;

import core.Vector2;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Camera {
    private final Scene scene;
    private Vector2 position = new Vector2();
    private Vector2 offset = new Vector2();
    private Vector2 zoom = new Vector2(1, 1);
    private Entity followed = null;

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

        g.setTransform(at);
    }

    public void follow(Entity entity) {
        followed = entity;
    }

    public void update() {
        if(this.followed != null) {
            this.position.x = followed.position().x;
            this.position.y = followed.position().y;
        }
    }
}
