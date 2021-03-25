package scene.physics;

import core.Mat2;
import core.Vector2;
import scene.Entity;

import java.util.*;

/**
 * Physics body
 */
public class Body {
    /**
     * The body mode
     */
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
        STATIC,
        /**
         * Transparent mode
         * The body is static and does not interact with other bodies
         */
        TRANSPARENT
    }

    private final PhysicsProvider physics;
    private final Entity entity;
    private Mode mode;

    private HashSet<Body> contacts = new HashSet<>();
    private HashSet<Body> lastContacts = new HashSet<>();

    private final ArrayList<BodyListener> bodyListeners = new ArrayList<>();

    double mass, invMass, inertia, invInertia;

    public final Shape shape;

    public final Vector2 velocity = new Vector2(0.0, 0.0);
    public final Vector2 force = new Vector2(0.0, 0.0);
    public double angularVelocity = 0.0;
    public double torque = 0.0;

    public double staticFriction = 0.5;
    public double dynamicFriction = 0.3;
    public double restitution = 0.2;

    public Vector2 gravity = new Vector2();

    public Body(PhysicsProvider physics, Entity entity, Shape shape, Mode mode) {
        this.physics = physics;
        this.entity = entity;
        this.mode = mode;
        this.shape = shape;

        gravity.set(physics.gravity);

        shape.body = this;
        shape.initialize();

        setMode(mode);
    }

    public void addBodyListener(BodyListener listener) {
        bodyListeners.add(listener);
    }
    public void removeBodyListener(BodyListener listener) {
        bodyListeners.remove(listener);
    }

    public Set<Body> contacts() {
        return Collections.unmodifiableSet(contacts);
    }

    public PhysicsProvider physics() {
        return physics;
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

        if (mode == Mode.STATIC || mode == Mode.TRANSPARENT) {
            inertia = 0.0;
            invInertia = 0.0;
            mass = 0.0;
            invMass = 0.0;
        } else if (mode == Mode.CHARACTER) {
            inertia = 0.0;
            invInertia = 0.0;
        }
    }

    public void resetGravity() {
        gravity.set(physics.gravity);
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
        velocity.addsi(gravity, dts);
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

    protected void clearContacts() {
        lastContacts = contacts;
        contacts = new HashSet<>();
    }

    protected void addContact(Body other) {
        contacts.add(other);
    }

    protected void updateContacts() {
        for(Body body : contacts) {
            if(!lastContacts.remove(body)) {
                handleBodyEntered(body);
            }
        }
        for(Body body : lastContacts) {
            handleBodyExited(body);
        }
        lastContacts.clear();
    }

    private void handleBodyEntered(Body body) {
        for(BodyListener listener : bodyListeners) {
            listener.bodyEntered(body);
        }
    }
    private void handleBodyExited(Body body) {
        for(BodyListener listener : bodyListeners) {
            listener.bodyExited(body);
        }
    }
}
