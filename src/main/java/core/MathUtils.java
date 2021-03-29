package core;

/**
 * Math functions.
 */
public class MathUtils {
    public static final double EPSILON = 0.0000001;
    public static final double EPSILON_SQ = EPSILON * EPSILON;
    public static final double PI = (double)StrictMath.PI;

    public static boolean biasGreaterThan(double a, double b) {
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
     * Clamp val between min to max
     */
    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double random( double min, double max )
    {
        return (double)((max - min) * Math.random() + min);
    }

    public static int random( int min, int max )
    {
        return (int)((max - min + 1) * Math.random() + min);
    }
}
