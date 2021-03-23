package core;

public class MathUtils {
    public static final double EPSILON = 0.0001;

    public static boolean BiasGreaterThan(double a, double b) {
        final double k_biasRelative = 0.95;
        final double k_biasAbsolute = 0.01;
        return a >= b * k_biasRelative + a * k_biasAbsolute;
    }

    /**
     * Comparison with tolerance of EPSILON
     */
    public static boolean equal(double a, double b) {
        return Math.abs(a - b) <= EPSILON;
    }

    /**
     * 2D cross product between a vector and a scalar
     * @param v The vector
     * @param a The scalar
     */
    public static Vector2 cross(Vector2 v, double a) {
        return new Vector2(a * v.y, -a * v.x);
    }
    /**
     * 2D cross product between a scalar and a vector
     * @param a The scalar
     * @param v The vector
     */
    public static Vector2 cross(double a, Vector2 v) {
        return new Vector2(-a * v.y, a * v.x);
    }

    /**
     * Clamp val between min to max
     */
    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}
