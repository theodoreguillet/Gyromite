package scene.physics;

import core.Vector2;

public class BoxShape extends Shape {
    private final Vector2 extents;

    public BoxShape(Body body, double width, double height) {
        this.extents = new Vector2(width / 2, height / 2);
    }

    public BoxShape(Body body, Vector2 extents) {
        this.extents = extents;
    }

    public Vector2 extents() {
        return extents;
    }

    /*
    public Vector2 min() {
        return position().clone().sub(extents);
    }

    public Vector2 max() {
        return position().clone().add(extents);
    }

    public static boolean BoxToBox(BoxShape a, BoxShape b) {
        // Exit with no intersection if found separated along an axis
        if(a.max().x < b.min().x || a.min().x > b.max().x) {
            return false;
        }
        if(a.max().y < b.min().y || a.min().y > b.max().y) {
            return false;
        }

        // No separating axis found, therefore there is at least one overlapping axis
        return true;
    }
    */
}
