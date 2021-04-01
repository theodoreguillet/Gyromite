package scene.physics;

import core.Mat2;
import core.Vector2;
import game.Column;
import scene.Node;

import java.util.*;

/**
 * A physics body
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
    private final Node node;
    private Mode mode;

    private HashSet<Body> contacts = new HashSet<>();
    private HashSet<Body> lastContacts = new HashSet<>();

    private final ArrayList<BodyListener> bodyListeners = new ArrayList<>();

    boolean pendingRemove = false;

    Vector2 position = new Vector2();
    double orient = 0.0;
    Mat2 orientMat = new Mat2(0.0);

    double mass, invMass, inertia, invInertia;

    /**
     * The shape of the body.
     */
    public final Shape shape;

    /**
     * The linear velocity.
     */
    public final Vector2 velocity = new Vector2(0.0, 0.0);
    /**
     * The linear force. Will be integrated to the body velocity.
     */
    public final Vector2 force = new Vector2(0.0, 0.0);
    /**
     * The angular velocity.
     */
    public double angularVelocity = 0.0;
    /**
     * The torque force. Will be integrated to the body angular velocity.
     */
    public double torque = 0.0;

    /**
     * The static friction constant.
     */
    public double staticFriction = 0.5;
    /**
     * The dynamic friction constant.
     */
    public double dynamicFriction = 0.3;
    /**
     * The coefficient of restitution.
     */
    public double restitution = 0.2;

    /**
     * The gravity force applied to the body.
     * By default the same as {@link PhysicsProvider#gravity}.
     */
    public Vector2 gravity = new Vector2();

    public Body(PhysicsProvider physics, Node node, Shape shape, Mode mode) {
        this.physics = physics;
        this.node = node;
        this.mode = mode;
        this.shape = shape;

        gravity.set(physics.gravity);

        shape.body = this;
        shape.initialize();

        setMode(mode);
    }

    /**
     * Add a collision event listener to the body.
     * @param listener The collision event listener.
     */
    public void addBodyListener(BodyListener listener) {
        bodyListeners.add(listener);
    }
    /**
     * Remove a collision event listener.
     * @param listener The collision event listener.
     */
    public void removeBodyListener(BodyListener listener) {
        bodyListeners.remove(listener);
    }

    /**
     * @return The bodies currently in contact with this body.
     */
    public Set<Body> contacts() {
        return Collections.unmodifiableSet(contacts);
    }

    /**
     * @return The physics manager.
     */
    public PhysicsProvider physics() {
        return physics;
    }
    /**
     * @return The body node.
     */
    public Node node() {
        return node;
    }
    /**
     * @return The body mode.
     */
    public Mode mode() {
        return mode;
    }

    /**
     * Set the body mode.
     */
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

    /**
     * Set the body gravity to {@link PhysicsProvider#gravity}
     */
    public void resetGravity() {
        gravity.set(physics.gravity);
    }


    void applyForce(Vector2 f) {
        force.addi(f);
    }

    void applyImpulse(Vector2 impulse, Vector2 contactVector) {
        velocity.addsi(impulse, invMass);
        angularVelocity += invInertia * Vector2.cross(contactVector, impulse);
    }

    void clearForces() {
        force.set(0, 0);
        torque = 0;
    }

    Vector2 position() {
        return position;
    }
    double orient() {
        return orient;
    }
    Mat2 orientMat() {
        return orientMat;
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

    void integrateForces(double dt) {
        if (invMass == 0) {
            return;
        }

        double dts = dt * 0.5;

        Vector2 worldGravity = node.owner() != null
                ? gravity.clone().rotate(-node.owner().worldOrient())
                : gravity;

        velocity.addsi(force, invMass * dts);
        velocity.addsi(worldGravity, dts);
        angularVelocity += torque * invInertia * dts;
    }

    void integrateVelocity(double dt) {
        node.position().addsi(velocity, dt);
        node.setOrient(node.orient() + angularVelocity * dt);
        computePosition();

        integrateForces(dt);
    }

    void applyCorrection(Vector2 normal, double correction) {
        node.position().addsi(normal, correction);
        computePosition();
    }

    void clearContacts() {
        lastContacts = contacts;
        contacts = new HashSet<>();
    }

    void computePosition() {
        position = node.worldPosition();
        orient = node.worldOrient();
        orientMat = new Mat2(orient);
    }

    void addContact(Body other) {
        contacts.add(other);
    }

    void updateContacts() {
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
