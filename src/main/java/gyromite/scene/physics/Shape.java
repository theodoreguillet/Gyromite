package gyromite.scene.physics;

/**
 * Physics Shape
 */
public abstract class Shape {
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
