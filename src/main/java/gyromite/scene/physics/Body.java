package gyromite.scene.physics;

import gyromite.core.Mat2;
import gyromite.core.Vector2;
import gyromite.scene.Node;

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

    private HashMap<Body, Manifold> contacts = new HashMap();
    private HashMap<Body, Manifold> lastContacts = new HashMap<>();

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
     *
     * @param listener The collision event listener.
     */
    public void addBodyListener(BodyListener listener) {
        bodyListeners.add(listener);
    }

    /**
     * Remove a collision event listener.
     *
     * @param listener The collision event listener.
     */
    public void removeBodyListener(BodyListener listener) {
        bodyListeners.remove(listener);
    }

    /**
     * @return The bodies currently in contact with this body.
     */
    public Map<Body, Manifold> contacts() {
        return Collections.unmodifiableMap(contacts);
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
        if(mode != Mode.CHARACTER) {
            angularVelocity += invInertia * Vector2.cross(contactVector, impulse);
        }
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
        contacts = new HashMap<>();
    }

    void computePosition() {
        position = node.worldPosition();
        orient = node.worldOrient();
        orientMat = new Mat2(orient);
    }

    void addContact(Body other, Manifold m) {
        contacts.put(other, m);
    }

    void updateContacts() {
        contacts.forEach((body, m) -> {
            if (lastContacts.remove(body) == null) {
                handleBodyEntered(body);
            }
        });
        lastContacts.forEach((body, m) -> {
            handleBodyExited(body);
        });
        lastContacts.clear();
    }

    private void handleBodyEntered(Body body) {
        for (BodyListener listener : bodyListeners) {
            listener.bodyEntered(body);
        }
    }

    private void handleBodyExited(Body body) {
        for (BodyListener listener : bodyListeners) {
            listener.bodyExited(body);
        }
    }
}
