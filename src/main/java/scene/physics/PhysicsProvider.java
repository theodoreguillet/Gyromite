package scene.physics;

import core.MainLoop;
import core.MathUtils;
import core.Vector2;
import scene.Entity;

import java.util.ArrayList;


public class PhysicsProvider {
    public static final double DT = MainLoop.DT;
    public static final Vector2 GRAVITY = new Vector2(0.0, 100.0);
    public static final double RESTING = GRAVITY.mul(DT).len2() + MathUtils.EPSILON;
    public static final double PENETRATION_ALLOWANCE = 0.05;
    public static final double PENETRATION_CORRETION = 0.4;

    double dt;
    int iterations;
    ArrayList<Body> bodies = new ArrayList<>();
    ArrayList<Manifold> contacts = new ArrayList<>();

    public PhysicsProvider(double dt, int iterations) {
        this.dt = dt;
        this.iterations = iterations;
    }

    public void step() {
        // Generate new collision info
        contacts.clear();
        for (int i = 0; i < bodies.size(); ++i) {
            Body A = bodies.get(i);

            for (int j = i + 1; j < bodies.size(); ++j) {
                Body B = bodies.get(j);

                if (A.invMass == 0 && B.invMass == 0) {
                    continue;
                }

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
    }

    public Body add(Entity entity, Shape shape, Body.Mode mode) {
        Body b = new Body(entity, shape, mode);
        bodies.add(b);
        return b;
    }

    public void remove(Body body) {
        bodies.remove(body);
    }

    public void clear() {
        contacts.clear();
        bodies.clear();
    }
}
