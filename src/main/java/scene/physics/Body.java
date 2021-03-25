package scene.physics;

import core.Mat2;
import core.Vector2;
import scene.Entity;

public class Body {
    public enum Mode {
        /**
         * Rigid body mode.
         * It is affected by forces, and can move, rotate, and be affected by user code.
         */
        RIGID,
        /**
         * Character body mode.
         * This behaves like a rigid body, but can not rotate.
         */
        CHARACTER,
        /**
         * Static mode.
         * The body can only move by user code.
         */
        STATIC
    }

    private final Entity entity;
    private Mode mode;

    double mass, invMass, inertia, invInertia;

    public final Shape shape;

    public final Vector2 velocity = new Vector2();
    public final Vector2 force = new Vector2();
    public double angularVelocity;
    public double torque;

    public double staticFriction;
    public double dynamicFriction;
    public double restitution;

    public Body(Entity entity, Shape shape, Mode mode) {
        this.entity = entity;
        this.mode = mode;
        this.shape = shape;

        velocity.set(0, 0);
        angularVelocity = 0;
        torque = 0;
        force.set(0, 0);
        staticFriction = 0.5;
        dynamicFriction = 0.3f;
        restitution = 0.2f;

        shape.body = this;

        shape.initialize();

        setMode(mode);
    }

    public Vector2 position() {
        return entity.position();
    }
    public double orient() {
        return entity.orient();
    }
    public Mat2 orientMat() {
        return entity.orientMat();
    }

    public Mode mode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;

        shape.computeMass();

        if (mode == Mode.STATIC) {
            inertia = 0.0;
            invInertia = 0.0;
            mass = 0.0;
            invMass = 0.0;
        } else if (mode == Mode.CHARACTER) {
            inertia = 0.0;
            invInertia = 0.0;
        }
    }

    public void applyForce(Vector2 f) {
        force.addi(f);
    }

    public void applyImpulse(Vector2 impulse, Vector2 contactVector) {
        velocity.addsi(impulse, invMass);
        angularVelocity += invInertia * Vector2.cross(contactVector, impulse);
    }

    public void clearForces() {
        force.set(0, 0);
        torque = 0;
    }

    // Acceleration
    // F = mA
    // => A = F * 1/m

    // Explicit Euler
    // x += v * dt
    // v += (1/m * F) * dt

    // Semi-Implicit (Symplectic) Euler
    // v += (1/m * F) * dt
    // x += v * dt

    protected void integrateForces(double dt) {
        if (invMass == 0.0) {
            return;
        }

        double dts = dt * 0.5;

        velocity.addsi(force, invMass * dts);
        velocity.addsi(PhysicsProvider.GRAVITY, dts);
        angularVelocity += torque * invInertia * dts;
    }

    protected void integrateVelocity(double dt) {
        if (invMass == 0.0) {
            return;
        }

        position().addsi(velocity, dt);
        entity.setOrient(entity.orient() + angularVelocity * dt);

        integrateForces(dt);
    }
}
