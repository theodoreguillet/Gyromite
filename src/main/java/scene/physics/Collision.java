package scene.physics;

import core.MathUtils;
import core.Vector2;

public class Collision {
    private static boolean CircleToCircle(Manifold m) {
        Body A = m.A;
        Body B = m.B;
        CircleShape a = (CircleShape) A.shape();
        CircleShape b = (CircleShape) B.shape();

        // Vector from A to B
        Vector2 vecAB = B.position().clone().sub(A.position());

        double radius = a.radius() + b.radius();
        double dist2 = vecAB.len2();

        if(dist2 > radius * radius) {
            return false;
        }

        // Circles have collided, now compute manifold
        double distance = Math.sqrt(dist2); // perform actual sqrt

        if(distance == 0.0) {
            // Circles are on same position
            m.penetration = a.radius();
            m.normal = new Vector2(1, 0);
        } else {
            // Penetration is difference between radius and distance
            m.penetration = radius - distance;
            // Normalize AB vector
            m.normal = vecAB.clone().div(distance);
        }
        return true;
    }

    private static boolean BoxToBox(Manifold m) {
        Body A = m.A;
        Body B = m.B;
        BoxShape a = (BoxShape) A.shape();
        BoxShape b = (BoxShape) B.shape();

        // Vector from A to B
        Vector2 vecAB = B.position().clone().sub(A.position());

        // Calculate overlap on x axis
        double xOverlap = a.extents().x + b.extents().x - Math.abs(vecAB.x);

        // SAT test on x axis
        if(xOverlap > 0) {
            // Calculate overlap on y axis
            double yOverlap = a.extents().y + b.extents().y - Math.abs(vecAB.y);

            // SAT test on y axis
            if(yOverlap > 0) {
                // Find out which axis is axis of least penetration
                if(xOverlap > yOverlap) {
                    m.normal = new Vector2(vecAB.x < 0 ? -1 : 1, 0);
                    m.penetration = xOverlap;
                } else {
                    m.normal = new Vector2(0, vecAB.y < 0 ? -1 : 1);
                    m.penetration = yOverlap;
                }
                return true;
            }
        }
        return false;
    }

    private static boolean BoxToCircle(Manifold m) {
        Body A = m.A;
        Body B = m.B;
        BoxShape a = (BoxShape) A.shape();
        CircleShape b = (CircleShape) B.shape();

        // Vector from A to B
        Vector2 vecAB = B.position().clone().sub(A.position());

        // Closest point on A to center of B
        Vector2 closest = new Vector2();
        /*
        Vector2 closest = new Vector2(
                MathUtils.clamp(vecAB.x, A.position().x - a.extents().x, A.position().x + a.extents().x),
                MathUtils.clamp(vecAB.y, A.position().y - a.extents().y, A.position().y + a.extents().y)
        );*/

        boolean inside = true;

        // Clamp point to edges of the box
        if(vecAB.x < -a.extents().x) {
            closest.x = -a.extents().x;
            inside = false;
        } else if(vecAB.x > a.extents().x) {
            closest.x = a.extents().x;
            inside = false;
        }
        if(vecAB.y < -a.extents().y) {
            closest.y = -a.extents().y;
            inside = false;
        } else if(vecAB.y > a.extents().y) {
            closest.y = a.extents().y;
            inside = false;
        }

        // Circle is inside the box, so we need to clamp the circle's center to the closest edge
        if(inside) {
            // Find closest axis
            if(Math.abs(vecAB.x) > Math.abs(vecAB.y)) {
                // Clamp to closest extent
                closest.x = closest.x > 0 ? a.extents().x : -a.extents().x;
            } else {
                closest.y = closest.y > 0 ? a.extents().y : -a.extents().y;
            }
        }

        Vector2 normal = vecAB.clone().sub(closest);
        double d2 = normal.len2();
        double r = b.radius();

        // Early out of the radius is shorter than distance to closest point and Circle not inside the Box
        if(!inside && d2 > r * r) {
            return false;
        }

        double d = Math.sqrt(d2);
        normal.normalize();

        // Collision normal needs to be flipped to point outside if circle was inside the Box
        m.normal = inside ? normal.clone().scl(-1) : normal.clone();
        m.penetration = r - d;

        return true;
    }
}
