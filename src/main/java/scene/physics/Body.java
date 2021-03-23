package scene.physics;

import core.Vector2;
import scene.Entity;

public class Body {
    private final Entity entity;
    private Shape shape;

    private Vector2 velocity = new Vector2();
    private double restitution = 0.0;
    private double mass = 0.0;
    private double invMass;

    public Body(Entity entity, Shape shape) {
        this.entity = entity;
        this.shape = shape;
        entity.scene().physics().addBody(this);
    }

    public Shape shape() {
        return shape;
    }
    public Vector2 position() {
        return entity.position();
    }
    public Vector2 velocity() {
        return velocity;
    }
    public double restitution() {
        return restitution;
    }
    public double mass() {
        return mass;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }
    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }
    public void setRestitution(double restitution) {
        this.restitution = restitution;
    }
    public void setMass(double mass) {
        this.mass = mass;
        this.invMass = (mass == 0.0) ? 0.0 : 1.0 / mass;
    }

    private static void ResolveCollision(Body A, Body B, Vector2 normal)
    {
        // Do not resolve if both objects have an infinite mass
        if(A.invMass == 0 && B.invMass == 0) {
            return;
        }
        // Calculate relative velocity
        Vector2 rv = B.velocity.clone().sub(A.velocity);

        // Calculate relative velocity in terms of the normal direction
        double velAlongNormal = rv.dot(normal);

        // Do not resolve if velocities are separating
        if(velAlongNormal > 0) {
            return;
        }

        // Calculate restitution
        double e = Math.min(A.restitution, B.restitution);

        // Calculate impulse scalar
        double j = -(1 + e) * velAlongNormal / (A.invMass + B.invMass);

        // Apply impulse
        Vector2 impulse = normal.scl(j);
        A.velocity.sclSub(impulse, A.invMass);
        B.velocity.sclAdd(impulse, B.invMass);
    }

    private static void PositionalCorrection(Body A, Body B, double penetration, Vector2 normal) {
        final double corrRatio = 0.2; // Correction ratio, usually 20% to 80%
        final double slop = 0.01; // Penetration  threshold, usually 0.01 to 0.1
        // Do not perform positional correction if the penetration is below slop
        if(penetration <= slop) {
            return;
        }
        Vector2 correction = normal.clone().scl(corrRatio * penetration / (A.invMass + B.invMass));
    }
}
