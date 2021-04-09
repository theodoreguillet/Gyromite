package gyromite.scene.physics;

import gyromite.core.Mat2;
import gyromite.core.MathUtils;
import gyromite.core.Vector2;
import gyromite.util.Pair;

import java.util.HashMap;

abstract class Collisions {
    private interface Solver {
        void solve(Manifold m, Body a, Body b);
    }

    private static class CollisionPair extends Pair<Class<? extends Shape>, Class<? extends Shape>> {
        public CollisionPair(Class<? extends Shape> first, Class<? extends Shape> second) {
            super(first, second);
        }
    }

    private static final HashMap<CollisionPair, Solver> solvers = InitSolvers();

    public static void Solve(Manifold m, Body a, Body b) {
        solvers.get(new CollisionPair(a.shape.getClass(), b.shape.getClass())).solve(m, a, b);
    }

    private static HashMap<CollisionPair, Solver> InitSolvers() {
        var solvers = new HashMap<CollisionPair, Solver>();
        solvers.put(new CollisionPair(CircleShape.class, CircleShape.class), Collisions::CircleToCircle);
        solvers.put(new CollisionPair(CircleShape.class, PolygonShape.class), Collisions::CircleToPolygon);
        solvers.put(new CollisionPair(PolygonShape.class, CircleShape.class), Collisions::PolygonToCircle);
        solvers.put(new CollisionPair(PolygonShape.class, PolygonShape.class), Collisions::PolygonToPolygon);
        return solvers;
    }

    private static void CircleToCircle(Manifold m, Body a, Body b) {
        CircleShape A = (CircleShape) a.shape;
        CircleShape B = (CircleShape) b.shape;

        // Calculate translational vector, which is normal
        Vector2 normal = b.position().sub(a.position());

        double dist_sqr = normal.len2();
        double radius = A.radius + B.radius;

        // Not in contact
        if (dist_sqr >= radius * radius) {
            m.contactCount = 0;
            return;
        }

        double distance = StrictMath.sqrt(dist_sqr);

        m.contactCount = 1;

        if (distance == 0.0) {
            m.penetration = A.radius;
            m.normal.set(1.0, 0.0);
            m.contacts[0].set(a.position());
        } else {
            m.penetration = radius - distance;
            m.normal.set(normal).divi(distance);
            m.contacts[0].set(m.normal).muli(A.radius).addi(a.position());
        }
    }

    private static void CircleToPolygon(Manifold m, Body a, Body b) {
        CircleShape A = (CircleShape) a.shape;
        PolygonShape B = (PolygonShape) b.shape;

        Mat2 B_u = b.orientMat();

        m.contactCount = 0;

        // Transform circle center to Polygon model space
        Vector2 center = B_u.transpose().muli(a.position().sub(b.position()));

        // Find edge with minimum penetration
        // Exact concept as using support points in Polygon vs Polygon
        double separation = -Float.MAX_VALUE;
        int faceNormal = 0;
        for (int i = 0; i < B.vertexCount; ++i) {
            double s = Vector2.dot(B.normals[i], center.sub(B.vertices[i]));

            if (s > A.radius) {
                return;
            }

            if (s > separation) {
                separation = s;
                faceNormal = i;
            }
        }

        // Grab face's vertices
        Vector2 v1 = B.vertices[faceNormal];
        int i2 = faceNormal + 1 < B.vertexCount ? faceNormal + 1 : 0;
        Vector2 v2 = B.vertices[i2];

        // Check to see if center is within polygon
        if (separation < MathUtils.EPSILON) {
            m.contactCount = 1;
            B_u.mul(B.normals[faceNormal], m.normal).negi();
            m.contacts[0].set(m.normal).muli(A.radius).addi(a.position());
            m.penetration = A.radius;
            return;
        }

        // Determine which voronoi region of the edge center of circle lies within
        double dot1 = Vector2.dot(center.sub(v1), v2.sub(v1));
        double dot2 = Vector2.dot(center.sub(v2), v1.sub(v2));
        m.penetration = A.radius - separation;

        // Closest to v1
        if (dot1 <= 0.0) {
            if (Vector2.dist2(center, v1) > A.radius * A.radius) {
                return;
            }

            m.contactCount = 1;
            B_u.muli(m.normal.set(v1).subi(center)).normalize();
            B_u.mul(v1, m.contacts[0]).addi(b.position());
        }

        // Closest to v2
        else if (dot2 <= 0.0) {
            if (Vector2.dist2(center, v2) > A.radius * A.radius) {
                return;
            }

            m.contactCount = 1;
            B_u.muli(m.normal.set(v2).subi(center)).normalize();
            B_u.mul(v2, m.contacts[0]).addi(b.position());
        }

        // Closest to face
        else {
            Vector2 n = B.normals[faceNormal];

            if (Vector2.dot(center.sub(v1), n) > A.radius) {
                return;
            }

            m.contactCount = 1;
            B_u.mul(n, m.normal).negi();
            m.contacts[0].set(a.position()).addsi(m.normal, A.radius);
        }
    }

    private static void PolygonToCircle(Manifold m, Body a, Body b) {
        CircleToPolygon(m, b, a);

        if (m.contactCount > 0) {
            m.normal.negi();
        }
    }

    private static void PolygonToPolygon(Manifold m, Body a, Body b) {
        PolygonShape A = (PolygonShape) a.shape;
        PolygonShape B = (PolygonShape) b.shape;
        m.contactCount = 0;

        // Check for a separating axis with A's face planes
        int[] faceA = {0};
        double penetrationA = findAxisLeastPenetration(faceA, A, B);
        if (penetrationA >= 0.0) {
            return;
        }

        // Check for a separating axis with B's face planes
        int[] faceB = {0};
        double penetrationB = findAxisLeastPenetration(faceB, B, A);
        if (penetrationB >= 0.0) {
            return;
        }

        int referenceIndex;
        boolean flip; // Always point from a to b

        PolygonShape RefPoly; // Reference
        PolygonShape IncPoly; // Incident

        // Determine which shape contains reference face
        if (MathUtils.biasGreaterThan(penetrationA, penetrationB)) {
            RefPoly = A;
            IncPoly = B;
            referenceIndex = faceA[0];
            flip = false;
        } else {
            RefPoly = B;
            IncPoly = A;
            referenceIndex = faceB[0];
            flip = true;
        }

        // World space incident face
        Vector2[] incidentFace = Vector2.arrayOf(2);

        findIncidentFace(incidentFace, RefPoly, IncPoly, referenceIndex);

        // y
        // ^ .n ^
        // +---c ------posPlane--
        // x < | i |\
        // +---+ c-----negPlane--
        // \ v
        // r
        //
        // r : reference face
        // i : incident poly
        // c : clipped point
        // n : incident normal

        // Setup reference face vertices
        Vector2 v1 = RefPoly.vertices[referenceIndex];
        referenceIndex = referenceIndex + 1 == RefPoly.vertexCount ? 0 : referenceIndex + 1;
        Vector2 v2 = RefPoly.vertices[referenceIndex];

        // Transform vertices to world space
        Mat2 u = RefPoly.body.orientMat();
        v1 = u.mul(v1).addi(RefPoly.body.position());
        v2 = u.mul(v2).addi(RefPoly.body.position());

        // Calculate reference face side normal in world space
        Vector2 sidePlaneNormal = v2.sub(v1);
        sidePlaneNormal.normalize();

        // Orthogonalize
        Vector2 refFaceNormal = Vector2.cross(sidePlaneNormal, 1.0, new Vector2());

        // ax + by = c
        // c is distance from origin
        double refC = Vector2.dot(refFaceNormal, v1);
        double negSide = -Vector2.dot(sidePlaneNormal, v1);
        double posSide = Vector2.dot(sidePlaneNormal, v2);

        // Clip incident face to reference face side planes
        if (clip(sidePlaneNormal.neg(), negSide, incidentFace) < 2) {
            return; // Due to floating point error, possible to not have required points
        }

        if (clip(sidePlaneNormal, posSide, incidentFace) < 2) {
            return; // Due to floating point error, possible to not have required points
        }

        // Flip
        m.normal.set(refFaceNormal);
        if (flip) {
            m.normal.negi();
        }

        // Keep points behind reference face
        int cp = 0; // clipped points behind reference face
        double separation = Vector2.dot(refFaceNormal, incidentFace[0]) - refC;
        if (separation <= 0.0) {
            m.contacts[cp].set(incidentFace[0]);
            m.penetration = -separation;
            ++cp;
        } else {
            m.penetration = 0;
        }

        separation = Vector2.dot(refFaceNormal, incidentFace[1]) - refC;

        if (separation <= 0.0) {
            m.contacts[cp].set(incidentFace[1]);

            m.penetration += -separation;
            ++cp;

            // Average penetration
            m.penetration /= cp;
        }

        m.contactCount = cp;
    }

    private static double findAxisLeastPenetration(int[] faceIndex, PolygonShape A, PolygonShape B) {
        double bestDistance = -Float.MAX_VALUE;
        int bestIndex = 0;

        Mat2 Au = A.body.orientMat();
        Mat2 Bu = B.body.orientMat();

        for (int i = 0; i < A.vertexCount; ++i) {
            // Retrieve a face normal from A
            Vector2 nw = Au.mul(A.normals[i]);

            // Transform face normal into B's model space
            Mat2 buT = Bu.transpose();
            Vector2 n = buT.mul(nw);

            // Retrieve support point from B along -n
            Vector2 s = B.getSupport(n.neg());

            // Retrieve vertex on face from A, transform into
            // B's model space
            Vector2 v = buT.muli(Au.mul(A.vertices[i]).addi(A.body.position()).subi(B.body.position()));

            // Compute penetration distance (in B's model space)
            double d = Vector2.dot(n, s.sub(v));

            // Store greatest distance
            if (d > bestDistance) {
                bestDistance = d;
                bestIndex = i;
            }
        }

        faceIndex[0] = bestIndex;
        return bestDistance;
    }

    private static void findIncidentFace(Vector2[] v, PolygonShape RefPoly, PolygonShape IncPoly, int referenceIndex) {
        Vector2 referenceNormal = RefPoly.normals[referenceIndex];

        Mat2 RefPoly_u = RefPoly.body.orientMat();
        Mat2 IncPoly_u = IncPoly.body.orientMat();

        // Calculate normal in incident's frame of reference
        referenceNormal = RefPoly_u.mul(referenceNormal); // To world space
        referenceNormal = IncPoly_u.transpose().mul(referenceNormal); // To incident's model space

        // Find most anti-normal face on incident polygon
        int incidentFace = 0;
        double minDot = Float.MAX_VALUE;
        for (int i = 0; i < IncPoly.vertexCount; ++i) {
            double dot = Vector2.dot(referenceNormal, IncPoly.normals[i]);

            if (dot < minDot) {
                minDot = dot;
                incidentFace = i;
            }
        }

        // Assign face vertices for incidentFace
        v[0] = IncPoly_u.mul(IncPoly.vertices[incidentFace]).addi(IncPoly.body.position());
        incidentFace = incidentFace + 1 >= IncPoly.vertexCount ? 0 : incidentFace + 1;
        v[1] = IncPoly_u.mul(IncPoly.vertices[incidentFace]).addi(IncPoly.body.position());
    }

    private static int clip(Vector2 n, double c, Vector2[] face) {
        int sp = 0;
        Vector2[] out = {
                new Vector2(face[0]),
                new Vector2(face[1])
        };

        // Retrieve distances from each endpoint to the line
        // d = ax + by - c
        double d1 = Vector2.dot(n, face[0]) - c;
        double d2 = Vector2.dot(n, face[1]) - c;

        // If negative (behind plane) clip
        if (d1 <= 0.0) out[sp++].set(face[0]);
        if (d2 <= 0.0) out[sp++].set(face[1]);

        // If the points are on different sides of the plane
        if (d1 * d2 < 0.0) // less than to ignore -0.0
        {
            // Push intersection point
            double alpha = d1 / (d1 - d2);
            out[sp++].set(face[1]).subi(face[0]).muli(alpha).addi(face[0]);
        }

        // Assign our new converted values
        face[0] = out[0];
        face[1] = out[1];

        return sp;
    }
}
