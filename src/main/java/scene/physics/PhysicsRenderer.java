package scene.physics;

import core.Mat2;
import core.Vector2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

/**
 * Render physics bodies shapes and collision points for debugging
 */
public class PhysicsRenderer {
    public static void render(PhysicsProvider physics, Graphics2D g) {
        for (Body b : physics.bodies) {
            if (b.shape instanceof CircleShape) {
                CircleShape c = (CircleShape) b.shape;

                double rx = StrictMath.cos(b.orient()) * c.radius;
                double ry = StrictMath.sin(b.orient()) * c.radius;

                g.setColor(Color.BLUE);
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

                g.setColor(Color.blue);
                g.draw(path);
            }
        }

        g.setColor(Color.RED);
        for (Manifold m : physics.contacts) {
            for (int i = 0; i < m.contactCount; i++) {
                Vector2 v = m.contacts[i];
                Vector2 n = m.normal;

                g.draw(new Line2D.Double(v.x, v.y, v.x + n.x * 4.0, v.y + n.y * 4.0));
            }
        }
    }

}
