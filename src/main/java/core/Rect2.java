package core;

public class Rect2 implements Cloneable {
    public final Vector2 min;
    public final Vector2 max;

    public Rect2() {
        min = new Vector2();
        max = new Vector2();
    }

    public Rect2(double x1, double y1, double x2, double y2) {
        min = new Vector2(x1, y1);
        max = new Vector2(x2, y2);
    }

    @Override
    public Rect2 clone() {
        try {
            Rect2 rect = (Rect2) super.clone();
            rect.min.set(min);
            rect.max.set(max);
            return rect;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    public Rect2 set(Rect2 rect) {
        this.min.set(rect.min);
        this.max.set(rect.max);
        return this;
    }

    public Rect2 set(double x1, double y1, double x2, double y2) {
        this.min.set(x1, y1);
        this.max.set(x2, y2);
        return this;
    }

    public boolean isEmpty() {
        return min.x == max.x && min.y == max.y;
    }

    public Size size() {
        return new Size(max.x - min.x, max.y - min.y);
    }
}
