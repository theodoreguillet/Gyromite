package scene.physics;

import core.Vector2;

public class CircleShape extends Shape {
    private final double radius;

    public CircleShape(double radius) {
        this.radius = radius;
    }

    public double radius() {
        return radius;
    }
}
