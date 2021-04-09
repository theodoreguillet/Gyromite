package gyromite.scene.physics;

import gyromite.core.Mat2;
import gyromite.core.Vector2;
import gyromite.scene.Node;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;

/**
 * The physics manager.
 * Provide physics constraints on bodies with shapes.
 */
public class PhysicsProvider {
    public static final double PENETRATION_ALLOWANCE = 0.05;
    public static final double PENETRATION_CORRECTION = 0.4;

    /**
     * The gravity force applied to all bodies
     */
    public final Vector2 gravity = new Vector2(0.0, 100.0);

    final double dt;
    final int iterations;
    private final ArrayList<Body> bodies = new ArrayList<>();
    private final ArrayList<Manifold> contacts = new ArrayList<>();

    public PhysicsProvider(double dt, int iterations) {
        this.dt = dt;
        this.iterations = iterations;
    }

    /**
     * Step physics. Called in game loop.
     */
    public void step() {
        // Remove pending
        bodies.removeIf(body -> body.pendingRemove);

        // Clear contacts
        contacts.clear();
        for(Body body : bodies) {
            body.clearContacts();
        }

        // Compute bodies position from node world position
        for(Body body : bodies) {
            body.computePosition();
        }

        // Generate new collision info
        for (int i = 0; i < bodies.size(); ++i) {
            Body A = bodies.get(i);

            for (int j = i + 1; j < bodies.size(); ++j) {
                Body B = bodies.get(j);

                Manifold m = new Manifold(A, B);
                m.solve();

                if (m.contactCount > 0) {
                    contacts.add(m);
                }
            }
        }

        // Integrate forces
        for (Body value : bodies) {
            value.integrateForces(dt);
        }

        // Initialize collision
        for (Manifold manifold : contacts) {
            manifold.initialize();
        }

        // Solve collisions
        for (int j = 0; j < iterations; ++j) {
            for (Manifold contact : contacts) {
                contact.applyImpulse();
            }
        }

        // Integrate velocities
        for (Body body : bodies) {
            body.integrateVelocity(dt);
        }

        // Correct positions
        for (Manifold contact : contacts) {
            contact.positionalCorrection();
        }

        // Clear all forces
        for (Body body : bodies) {
            body.clearForces();
        }

        // Update bodies contacts
        for (Manifold contact : contacts) {
            contact.A.addContact(contact.B, contact);
            contact.B.addContact(contact.A, contact);
        }
        for(Body b : bodies) {
            b.updateContacts();
        }
    }

    /**
     * Create a new body and add it
     * @param node The entity of the body
     * @param shape The shape
     * @param mode The body mode. See {@link Body.Mode}
     * @return The body
     */
    public Body add(Node node, Shape shape, Body.Mode mode) {
        Body b = new Body(this, node, shape, mode);
        bodies.add(b);
        return b;
    }

    /**
     * Remove a body from physics.
     * The body will be removed from the list in the next step.
     * @param body The body
     */
    public void remove(Body body) {
        body.pendingRemove = true;
    }

    /**
     * Remove all bodies and clear
     */
    public void clear() {
        contacts.clear();
        bodies.clear();
    }

    /**
     * Render physics bodies shapes and collision points for debugging
     */
    public void render(Graphics2D g) {
        for (Body b : bodies) {
            b.computePosition(); // Compute position for render
            if(b.mode() == Body.Mode.TRANSPARENT) {
                g.setColor(Color.GRAY);
            } else {
                g.setColor(Color.BLUE);
            }
            if (b.shape instanceof CircleShape) {
                CircleShape c = (CircleShape) b.shape;

                double rx = StrictMath.cos(b.orient()) * c.radius;
                double ry = StrictMath.sin(b.orient()) * c.radius;

                g.draw(new Ellipse2D.Double(b.position().x - c.radius, b.position().y - c.radius, c.radius * 2, c.radius * 2));
                g.draw(new Line2D.Double(b.position().x, b.position().y, b.position().x + rx, b.position().y + ry));
            } else if (b.shape instanceof PolygonShape) {
                PolygonShape p = (PolygonShape) b.shape;
                Mat2 u = b.orientMat();

                Path2D.Float path = new Path2D.Float();
                for (int i = 0; i < p.vertexCount; i++) {
                    Vector2 v = new Vector2(p.vertices[i]);
                    u.muli(v);
                    v.addi(b.position());

                    if (i == 0) {
                        path.moveTo(v.x, v.y);
                    } else {
                        path.lineTo(v.x, v.y);
                    }
                }
                path.closePath();

                g.draw(path);
            }
        }

        g.setColor(Color.RED);
        for (Manifold m : contacts) {
            for (int i = 0; i < m.contactCount; i++) {
                Vector2 v = m.contacts[i];
                Vector2 n = m.normal;

                g.draw(new Line2D.Double(v.x, v.y, v.x + n.x * 4.0, v.y + n.y * 4.0));
            }
        }
    }
}
