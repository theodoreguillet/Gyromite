package gyromite.core;

/**
 * A 2D size
 */
public class Size2 implements Cloneable {
    public double width;
    public double height;

    public Size2() {
        this(0, 0);
    }

    public Size2(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Size2 clone() {
        try {
            return (Size2) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    public Size2 set(Size2 size) {
        width = size.width;
        height = size.height;
        return this;
    }

    public Size2 set(double w, double h) {
        width = w;
        height = h;
        return this;
    }
}
