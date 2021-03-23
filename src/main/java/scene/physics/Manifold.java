package scene.physics;

import core.Vector2;

public class Manifold {
    public Body A;
    public Body B;
    public double penetration;
    public Vector2 normal;
}
