package scene.physics;

public abstract class Shape {
    public enum Type {
        Circle, Poly, Count
    }

    private double density = 1.0;

    Body body;

    public Shape() {
    }

    public double density() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
        if (body != null) {
            computeMass();
        }
    }

    public abstract Shape clone();

    protected abstract void initialize();

    protected abstract void computeMass();
}
