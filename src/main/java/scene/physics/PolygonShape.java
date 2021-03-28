package scene.physics;

import core.Vector2;

/**
 * Physics body polygon shape
 */
public class PolygonShape extends Shape {

    public static final int MAX_POLY_VERTEX_COUNT = 64;

    int vertexCount;
    Vector2[] vertices = Vector2.arrayOf(MAX_POLY_VERTEX_COUNT);
    Vector2[] normals = Vector2.arrayOf(MAX_POLY_VERTEX_COUNT);

    private double I = 0.0;
    private double area = 0.0;
    private final Vector2 centroid = new Vector2();

    public PolygonShape() {
    }

    public PolygonShape(Vector2... verts) {
        set(verts);
    }

    public PolygonShape(double hw, double hh) {
        setBox(hw, hh);
    }

    @Override
    public Shape clone() {
        PolygonShape p = new PolygonShape();
        for (int i = 0; i < vertexCount; i++) {
            p.vertices[i].set(vertices[i]);
            p.normals[i].set(normals[i]);
        }
        p.vertexCount = vertexCount;
        return p;
    }

    public double inertia() {
        return I;
    }
    public double area() {
        return area;
    }
    public Vector2 centroid() {
        return centroid.clone();
    }

    @Override
    protected void initialize() {
        computeMass();
    }

    @Override
    protected void computeMass() {
        body.mass = density() * area;
        body.invMass = (body.mass != 0.0) ? 1.0 / body.mass : 0.0;
        body.inertia = I * density();
        body.invInertia = (body.inertia != 0.0) ? 1.0 / body.inertia : 0.0;
    }

    private void computeCentroid() {
        // Calculate centroid and moment of inertia
        centroid.set(0.0, 0.0); // centroid
        area = 0.0;
        I = 0.0;
        final double k_inv3 = 1.0 / 3.0;

        for (int i = 0; i < vertexCount; ++i) {
            // Triangle vertices, third vertex implied as (0, 0)
            Vector2 p1 = vertices[i];
            Vector2 p2 = vertices[(i + 1) % vertexCount];

            double D = Vector2.cross(p1, p2);
            double triangleArea = 0.5 * D;

            area += triangleArea;

            // Use area to weight the centroid average, not just vertex position
            double weight = triangleArea * k_inv3;
            centroid.addsi(p1, weight);
            centroid.addsi(p2, weight);

            double intx2 = p1.x * p1.x + p2.x * p1.x + p2.x * p2.x;
            double inty2 = p1.y * p1.y + p2.y * p1.y + p2.y * p2.y;
            I += (0.25f * k_inv3 * D) * (intx2 + inty2);
        }

        centroid.muli(1.0 / area);
    }

    public void setBox(double hw, double hh) {
        vertexCount = 4;
        vertices[0].set(-hw, -hh);
        vertices[1].set(hw, -hh);
        vertices[2].set(hw, hh);
        vertices[3].set(-hw, hh);
        normals[0].set(0.0, -1.0);
        normals[1].set(1.0, 0.0);
        normals[2].set(0.0, 1.0);
        normals[3].set(-1.0, 0.0);

        // Calculate centroid and moment of inertia
        computeCentroid();
    }

    public void set(Vector2... verts) {
        // Find the right most point on the hull
        int rightMost = 0;
        double highestXCoord = verts[0].x;
        for (int i = 1; i < verts.length; ++i) {
            double x = verts[i].x;

            if (x > highestXCoord) {
                highestXCoord = x;
                rightMost = i;
            }
            // If matching x then take farthest negative y
            else if (x == highestXCoord) {
                if (verts[i].y < verts[rightMost].y) {
                    rightMost = i;
                }
            }
        }

        int[] hull = new int[MAX_POLY_VERTEX_COUNT];
        int outCount = 0;
        int indexHull = rightMost;

        for (; ; ) {
            hull[outCount] = indexHull;

            // Search for next index that wraps around the hull
            // by computing cross products to find the most counter-clockwise
            // vertex in the set, given the previos hull index
            int nextHullIndex = 0;
            for (int i = 1; i < verts.length; ++i) {
                // Skip if same coordinate as we need three unique
                // points in the set to perform a cross product
                if (nextHullIndex == indexHull) {
                    nextHullIndex = i;
                    continue;
                }

                // Cross every set of three unique vertices
                // Record each counter clockwise third vertex and add
                // to the output hull
                // See : http://www.oocities.org/pcgpe/math2d.html
                Vector2 e1 = verts[nextHullIndex].sub(verts[hull[outCount]]);
                Vector2 e2 = verts[i].sub(verts[hull[outCount]]);
                double c = Vector2.cross(e1, e2);
                if (c < 0.0) {
                    nextHullIndex = i;
                }

                // Cross product is zero then e vectors are on same line
                // therefore want to record vertex farthest along that line
                if (c == 0.0 && e2.len2() > e1.len2()) {
                    nextHullIndex = i;
                }
            }

            ++outCount;
            indexHull = nextHullIndex;

            // Conclude algorithm upon wrap-around
            if (nextHullIndex == rightMost) {
                vertexCount = outCount;
                break;
            }
        }

        // Copy vertices into shape's vertices
        for (int i = 0; i < vertexCount; ++i) {
            vertices[i].set(verts[hull[i]]);
        }

        // Compute face normals
        for (int i = 0; i < vertexCount; ++i) {
            Vector2 face = vertices[(i + 1) % vertexCount].sub(vertices[i]);

            // Calculate normal with 2D cross product between vector and scalar
            normals[i].cross(face, 1.0);
            normals[i].normalize();
        }

        // Calculate centroid and moment of inertia
        computeCentroid();
    }

    public Vector2 getSupport(Vector2 dir) {
        double bestProjection = -Float.MAX_VALUE;
        Vector2 bestVertex = null;

        for (int i = 0; i < vertexCount; ++i) {
            Vector2 v = vertices[i];
            double projection = Vector2.dot(v, dir);

            if (projection > bestProjection) {
                bestVertex = v;
                bestProjection = projection;
            }
        }

        return bestVertex;
    }

}
