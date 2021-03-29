package scene.physics;

import core.MathUtils;
import core.Vector2;

class Manifold {
    Body A;
    Body B;
    boolean transparent;
    double penetration;
    final Vector2 normal = new Vector2();
    final Vector2[] contacts = {new Vector2(), new Vector2()};
    int contactCount;
    double e;
    double df;
    double sf;

    Manifold(Body a, Body b) {
        A = a;
        B = b;
    }

    void solve() {
        transparent = A.mode() == Body.Mode.TRANSPARENT || B.mode() == Body.Mode.TRANSPARENT;
        Collisions.Solve(this, A, B);
    }

    void initialize() {
        // Calculate average restitution
        e = StrictMath.min(A.restitution, B.restitution);

        // Calculate static and dynamic friction
        sf = StrictMath.sqrt(A.staticFriction * A.staticFriction + B.staticFriction * B.staticFriction);
        df = StrictMath.sqrt(A.dynamicFriction * A.dynamicFriction + B.dynamicFriction * B.dynamicFriction);

        for (int i = 0; i < contactCount; ++i)
        {
            // Calculate radii from COM to contact
            Vector2 ra = contacts[i].sub( A.position );
            Vector2 rb = contacts[i].sub( B.position );

            Vector2 rv = B.velocity.add( Vector2.cross( B.angularVelocity, rb, new Vector2() ) ).subi( A.velocity ).subi( Vector2.cross( A.angularVelocity, ra, new Vector2() ) );

            // Determine if we should perform a resting collision or not
            // The idea is if the only thing moving this object is gravity,
            // then the collision should be performed without any restitution
            if (rv.len2() < (A.mode() == Body.Mode.STATIC ? B : A).gravity.mul(A.physics().dt).len2() + MathUtils.EPSILON)
            {
                e = 0.0;
            }
        }
    }

    void applyImpulse() {
        if(transparent) {
            return;
        }

        // Early out and positional correct if both objects have infinite mass
        if (MathUtils.equal(A.invMass + B.invMass, 0)) {
            infiniteMassCorrection();
            return;
        }

        for (int i = 0; i < contactCount; ++i) {
            // Calculate radii from COM to contact
            Vector2 ra = contacts[i].sub(A.position());
            Vector2 rb = contacts[i].sub(B.position());

            // Relative velocity
            Vector2 rv = B.velocity.add(Vector2.cross(B.angularVelocity, rb, new Vector2())).subi(A.velocity).subi(Vector2.cross(A.angularVelocity, ra, new Vector2()));

            // Relative velocity along the normal
            double contactVel = Vector2.dot(rv, normal);

            // Do not resolve if velocities are separating
            if (contactVel > 0) {
                return;
            }

            double raCrossN = Vector2.cross(ra, normal);
            double rbCrossN = Vector2.cross(rb, normal);
            double invMassSum = A.invMass + B.invMass + (raCrossN * raCrossN) * A.invInertia + (rbCrossN * rbCrossN) * B.invInertia;

            // Calculate impulse scalar
            double j = -(1.0 + e) * contactVel;
            j /= invMassSum;
            j /= contactCount;

            // Apply impulse
            Vector2 impulse = normal.mul(j);
            A.applyImpulse(impulse.neg(), ra);
            B.applyImpulse(impulse, rb);

            // Friction impulse
            rv = B.velocity.add(Vector2.cross(B.angularVelocity, rb, new Vector2())).subi(A.velocity).subi(Vector2.cross(A.angularVelocity, ra, new Vector2()));

            Vector2 t = new Vector2(rv);
            t.addsi(normal, -Vector2.dot(rv, normal));
            t.normalize();

            // j tangent magnitude
            double jt = -Vector2.dot(rv, t);
            jt /= invMassSum;
            jt /= contactCount;

            // Don't apply tiny friction impulses
            if (MathUtils.equal(jt, 0.0)) {
                return;
            }

            // Coulumb's law
            Vector2 tangentImpulse;
            if (StrictMath.abs(jt) < j * sf) {
                tangentImpulse = t.mul(jt);
            } else {
                tangentImpulse = t.mul(j).muli(-df);
            }

            // Apply friction impulse
            A.applyImpulse(tangentImpulse.neg(), ra);
            B.applyImpulse(tangentImpulse, rb);
        }
    }

    void positionalCorrection() {
        if(transparent || MathUtils.equal(A.invMass + B.invMass, 0)) {
            return;
        }
        double correction = StrictMath.max(penetration - PhysicsProvider.PENETRATION_ALLOWANCE, 0.0) / (A.invMass + B.invMass) * PhysicsProvider.PENETRATION_CORRECTION;

        A.applyCorrection(normal, -A.invMass * correction);
        B.applyCorrection(normal, B.invMass * correction);
    }

    void infiniteMassCorrection() {
        if(transparent) {
            return;
        }
        A.velocity.set(0, 0);
        B.velocity.set(0, 0);
    }
}
