package scene;

import core.Vector2;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Entity {
    private final Scene scene;
    private Vector2 position = new Vector2();

    public Entity(Scene scene) {
        this.scene = scene;
        this.scene.addEntity(this);
    }

    public Vector2 position() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void update() { }

    public void render(Graphics2D g) {
        AffineTransform at = new AffineTransform(g.getTransform());

        at.translate(position.x, position.y);

        g.setTransform(at);
    }
}
