package core;

public class Mat2 implements Cloneable {
    private double[][] data;

    public static Mat2 Identity() {
        return new Mat2(
                1.0, 0.0,
                0.0, 1.0
        );
    }

    public Mat2() {
        data = new double[][]{ { 0.0, 0.0 }, { 0.0, 0.0 } };
    }

    public Mat2(double a, double b, double c, double d) {
        data = new double[][]{ { a, b }, { c, d } };
    }

    public Mat2(Mat2 m) {
        data = new double[][]{ { m.data[0][0], m.data[0][1] }, { m.data[1][0], m.data[1][1] } };
    }

    /**
     * Rotation matrix of given angle
     * @param radians Angle of Rotation in radians
     */
    public Mat2(double radians) {
        double c = Math.cos(radians);
        double s = Math.sin(radians);
        data = new double[][]{ { c, -s }, { s, c } };
    }

    /**
     * Set this matrix from the given matrix
     * @param m The matrix
     * @return This matrix for chaining
     */
    public Mat2 set(Mat2 m) {
        data[0][0] = m.data[0][0];
        data[0][1] = m.data[0][1];
        data[1][0] = m.data[1][0];
        data[1][1] = m.data[1][1];
        return this;
    }

    /**
     * Set this matrix from the given matrix
     * @return This matrix for chaining
     */
    public Mat2 set(double a, double b, double c, double d) {
        data[0][0] = a;
        data[0][1] = b;
        data[1][0] = c;
        data[1][1] = d;
        return this;
    }

    /**
     * Set this matrix from the rotation matrix of given angle
     * @param radians Angle of Rotation in radians
     * @return This matrix for chaining
     */
    public Mat2 set(double radians) {
        double c = Math.cos(radians);
        double s = Math.sin(radians);
        data[0][0] = c;
        data[0][1] = -s;
        data[1][0] = s;
        data[1][1] = c;
        return this;
    }

    @Override
    public Mat2 clone() {
        try {
            Mat2 m = (Mat2)super.clone();
            return m.set(this);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    public double get(int i, int j) {
        assert(i >= 0 && i < 2 && j >= 0 && j < 2);
        return this.data[i][j];
    }

    /**
     * Compute the matrix multiplication of this by another matrix
     * @param m The matrix
     * @return A new {@link Mat2} result of the matrix multiplication
     */
    public Mat2 mul(Mat2 m) {
        return new Mat2(
                data[0][0] * m.data[0][0] + data[0][1] * m.data[1][0],
                data[0][0] * m.data[0][1] + data[0][1] * m.data[1][1],
                data[1][0] * m.data[0][0] + data[1][1] * m.data[1][0],
                data[1][0] * m.data[0][1] + data[1][1] * m.data[1][1]
        );
    }

    /**
     * Compute the matrix multiplication of this by a {@link Vector2}
     * @param v The vector
     * @return A new {@link Vector2} result of the matrix multiplication by the vector
     */
    public Vector2 mul(Vector2 v) {
        return new Vector2(
                v.x * data[0][0] + v.y * data[0][1],
                v.x * data[1][0] + v.y * data[1][1]
        );
    }

    /**
     * Transpose this matrix
     * @return A new {@link Mat2} result of the transposition
     */
    public Mat2 transpose() {
        return new Mat2(
                data[0][0], data[1][0],
                data[0][1], data[1][1]
        );
    }

    /**
     * Return first column in a {@link Vector2}
     * @return The first column of the matrix
     */
    public Vector2 axisX() {
        return new Vector2(data[0][0], data[1][0]);
    }

    /**
     * Return second column in a {@link Vector2}
     * @return The second column of the matrix
     */
    public Vector2 axisY() {
        return new Vector2(data[0][1], data[1][1]);
    }

    /**
     * Add the given matrix to this matrix
     * @param m The matrix to add
     * @return This matrix for chaining
     */
    public Mat2 add(Mat2 m) {
        data[0][0] += m.data[0][0];
        data[0][1] += m.data[0][1];
        data[1][0] += m.data[1][0];
        data[1][1] += m.data[1][1];
        return this;
    }

    /**
     * Substrate the given matrix to this matrix
     * @param m The matrix to add
     * @return This matrix for chaining
     */
    public Mat2 sub(Mat2 m) {
        data[0][0] -= m.data[0][0];
        data[0][1] -= m.data[0][1];
        data[1][0] -= m.data[1][0];
        data[1][1] -= m.data[1][1];
        return this;
    }

    /**
     * Scales this matrix by a scalar
     * @param scalar The scalar
     * @return This matrix for chaining
     */
    public Mat2 scl(double scalar) {
        data[0][0] *= scalar;
        data[0][1] *= scalar;
        data[1][0] *= scalar;
        data[1][1] *= scalar;
        return this;
    }

    /**
     * Scales this matrix by the given matrix components
     * @return This matrix for chaining
     */
    public Mat2 scl(Mat2 m) {
        data[0][0] *= m.data[0][0];
        data[0][1] *= m.data[0][1];
        data[1][0] *= m.data[1][0];
        data[1][1] *= m.data[1][1];
        return this;
    }
}
