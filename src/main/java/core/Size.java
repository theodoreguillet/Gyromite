package core;

public class Size implements Cloneable {
    public double width;
    public double height;

    public Size() {
        this(0, 0);
    }

    public Size(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Size clone() {
        try {
            return (Size) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    public Size set(Size size) {
        width = size.width;
        height = size.height;
        return this;
    }

    public Size set(double w, double h) {
        width = w;
        height = h;
        return this;
    }
}
