package gyromite.scene.physics;

import gyromite.core.MathUtils;

/**
 * Physics body circle shape
 */
public class CircleShape extends Shape {
    double radius;

    public CircleShape(double r) {
        radius = r;
    }

    /**
     * @return The radius of the circle shape.
     */
    public double radius() {
        return radius;
    }

    @Override
    public Shape clone() {
        return new CircleShape(radius);
    }

    @Override
    protected void initialize() {
        computeMass();
    }

    @Override
    protected void computeMass() {
        body.mass = MathUtils.PI * radius * radius * density();
        body.invMass = (body.mass != 0.0) ? 1.0 / body.mass : 0.0;
        body.inertia = body.mass * radius * radius;
        body.invInertia = (body.inertia != 0.0) ? 1.0 / body.inertia : 0.0;
    }
}
