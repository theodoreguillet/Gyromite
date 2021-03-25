package scene.physics;

import core.MathUtils;

public class CircleShape extends Shape {
    double radius;

    public CircleShape(double r) {
        radius = r;
    }

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
