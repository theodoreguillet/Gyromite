package scene.physics;

import core.Vector2;

import java.util.ArrayList;

public class PhysicsProvider {
    private final ArrayList<Body> bodies = new ArrayList<>();

    public PhysicsProvider(double dt, int iter, Vector2 gravity) {
        //
    }

    public void step() {
        //
    }

    public void addBody(Body body) {
        if(!bodies.contains(body)) {
            bodies.add(body);
        }
    }

    public void removeBody(Body body) {
        bodies.remove(body);
    }
}
