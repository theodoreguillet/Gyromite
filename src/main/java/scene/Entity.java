package scene;

import core.Mat2;
import core.Vector2;
import scene.physics.Body;
import scene.physics.Shape;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Entity {
    private final Scene scene;
    private Body body = null;

    private Vector2 position = new Vector2();
    private double orient = 0.0; // Orientation in radians
    private final Mat2 orientMat = new Mat2(0.0);

    public Entity(Scene scene) {
        this.scene = scene;
        this.scene.addEntity(this);
    }

    public Scene scene() {
        return scene;
    }
    public Body body() {
        return body;
    }
    public Vector2 position() {
        return position;
    }
    public double orient() {
        return orient;
    }
    public Mat2 orientMat() {
        return orientMat.clone();
    }

    public Body setBody(Shape shape, Body.Mode mode) {
        this.body = scene().physics().add(this, shape, mode);
        return this.body;
    }
    public void removeBody() {
        if(this.body != null) {
            scene().physics().remove(this.body);
            this.body = null;
        }
    }
    public void setPosition(Vector2 position) {
        this.position = position;
    }
    public void setOrient(double orient) {
        this.orient = orient;
        this.orientMat.set(orient);
    }

    /**
     * Removes this entity from the scene.
     * Also removes the body of this entity.
     */
    public void remove() {
        removeBody();
        scene.removeEntity(this);
    }

    public void update() { }

    public void render(Graphics2D g) {
        AffineTransform at = new AffineTransform(g.getTransform());

        at.translate(position.x, position.y);

        g.setTransform(at);
    }
}
