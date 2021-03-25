package core;

public class Mat2 implements Cloneable {

    public double m00, m01;
    public double m10, m11;

    public Mat2() {
    }

    public Mat2(double radians) {
        set(radians);
    }

    public Mat2(double a, double b, double c, double d) {
        set(a, b, c, d);
    }

    /**
     * Sets this matrix to a rotation matrix with the given radians.
     */
    public void set(double radians) {
        double c = (double) StrictMath.cos(radians);
        double s = (double) StrictMath.sin(radians);

        m00 = c;
        m01 = -s;
        m10 = s;
        m11 = c;
    }

    /**
     * Sets the values of this matrix.
     */
    public void set(double a, double b, double c, double d) {
        m00 = a;
        m01 = b;
        m10 = c;
        m11 = d;
    }

    /**
     * Sets this matrix to have the same values as the given matrix.
     */
    public void set(Mat2 m) {
        m00 = m.m00;
        m01 = m.m01;
        m10 = m.m10;
        m11 = m.m11;
    }

    @Override
    public Mat2 clone() {
        try {
            return (Mat2) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    /**
     * Sets the values of this matrix to their absolute value.
     */
    public void absi() {
        abs(this);
    }

    /**
     * Returns a new matrix that is the absolute value of this matrix.
     */
    public Mat2 abs() {
        return abs(new Mat2());
    }

    /**
     * Sets out to the absolute value of this matrix.
     */
    public Mat2 abs(Mat2 out) {
        out.m00 = StrictMath.abs(m00);
        out.m01 = StrictMath.abs(m01);
        out.m10 = StrictMath.abs(m10);
        out.m11 = StrictMath.abs(m11);
        return out;
    }

    /**
     * Sets out to the x-axis (1st column) of this matrix.
     */
    public Vector2 getAxisX(Vector2 out) {
        out.x = m00;
        out.y = m10;
        return out;
    }

    /**
     * Returns a new vector that is the x-axis (1st column) of this matrix.
     */
    public Vector2 getAxisX() {
        return getAxisX(new Vector2());
    }

    /**
     * Sets out to the y-axis (2nd column) of this matrix.
     */
    public Vector2 getAxisY(Vector2 out) {
        out.x = m01;
        out.y = m11;
        return out;
    }

    /**
     * Returns a new vector that is the y-axis (2nd column) of this matrix.
     */
    public Vector2 getAxisY() {
        return getAxisY(new Vector2());
    }

    /**
     * Sets the matrix to it's transpose.
     */
    public void transposei() {
        double t = m01;
        m01 = m10;
        m10 = t;
    }

    /**
     * Sets out to the transpose of this matrix.
     */
    public Mat2 transpose(Mat2 out) {
        out.m00 = m00;
        out.m01 = m10;
        out.m10 = m01;
        out.m11 = m11;
        return out;
    }

    /**
     * Returns a new matrix that is the transpose of this matrix.
     */
    public Mat2 transpose() {
        return transpose(new Mat2());
    }

    /**
     * Transforms v by this matrix.
     */
    public Vector2 muli(Vector2 v) {
        return mul(v.x, v.y, v);
    }

    /**
     * Sets out to the transformation of v by this matrix.
     */
    public Vector2 mul(Vector2 v, Vector2 out) {
        return mul(v.x, v.y, out);
    }

    /**
     * Returns a new vector that is the transformation of v by this matrix.
     */
    public Vector2 mul(Vector2 v) {
        return mul(v.x, v.y, new Vector2());
    }

    /**
     * Sets out the to transformation of {x,y} by this matrix.
     */
    public Vector2 mul(double x, double y, Vector2 out) {
        out.x = m00 * x + m01 * y;
        out.y = m10 * x + m11 * y;
        return out;
    }

    /**
     * Multiplies this matrix by x.
     */
    public void muli(Mat2 x) {
        set(
                m00 * x.m00 + m01 * x.m10,
                m00 * x.m01 + m01 * x.m11,
                m10 * x.m00 + m11 * x.m10,
                m10 * x.m01 + m11 * x.m11);
    }

    /**
     * Sets out to the multiplication of this matrix and x.
     */
    public Mat2 mul(Mat2 x, Mat2 out) {
        out.m00 = m00 * x.m00 + m01 * x.m10;
        out.m01 = m00 * x.m01 + m01 * x.m11;
        out.m10 = m10 * x.m00 + m11 * x.m10;
        out.m11 = m10 * x.m01 + m11 * x.m11;
        return out;
    }

    /**
     * Returns a new matrix that is the multiplication of this and x.
     */
    public Mat2 mul(Mat2 x) {
        return mul(x, new Mat2());
    }

}
