package core;

/**
 * A 2D Vector with math operations.
 */
public class Vector2 implements Cloneable {
    public double x;
    public double y;

    public Vector2() {
        this(0, 0);
    }

    public Vector2(Vector2 v) {
        this(v.x, v.y);
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Vector2 clone() {
        try {
            return (Vector2) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    /**
     * Sets the components of this vector
     * @param x The x component
     * @param y The y component
     * @return This vector for chaining
     */
    public Vector2 set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Sets this vector from the given vector
     * @param v The vector
     * @return This vector for chaining
     */
    public Vector2 set(Vector2 v) {
        x = v.x;
        y = v.y;
        return this;
    }

    /**
     * Adds the given components to this vector
     * @param x The x component
     * @param y The y component
     * @return This vector for chaining
     */
    public Vector2 add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    /**
     * Add the given vector to this vector
     * @param v The vector to add
     * @return This vector for chaining
     */
    public Vector2 add(Vector2 v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    /**
     * Substrates the given components to this vector
     * @param x The x component
     * @param y The y component
     * @return This vector for chaining
     */
    public Vector2 sub(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    /**
     * Substrate the given vector to this vector
     * @param v The vector to add
     * @return This vector for chaining
     */
    public Vector2 sub(Vector2 v) {
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    /**
     * Divide this vector by a scalar
     * @param scalar The scalar
     * @return This vector for chaining
     */
    public Vector2 div(double scalar) {
        x /= scalar;
        y /= scalar;
        return this;
    }

    /**
     * Scales this vector by a scalar
     * @param scalar The scalar
     * @return This vector for chaining
     */
    public Vector2 scl(double scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    /**
     * Scales this vector by a scalar
     * @return This vector for chaining
     */
    public Vector2 scl(double x, double y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    /**
     * Scales this vector by another vector
     * @return This vector for chaining
     */
    public Vector2 scl(Vector2 v) {
        this.x *= v.x;
        this.y *= v.y;
        return this;
    }

    /**
     * First scale a supplied vector, then add it to this vector.
     * @param scalar The scalar
     * @return This vector for chaining
     */
    public Vector2 sclAdd(Vector2 vec, double scalar) {
        x += scalar * vec.x;
        y += scalar * vec.y;
        return this;
    }

    /**
     * First scale a supplied vector, then add it to this vector.
     * @return This vector for chaining
     */
    public Vector2 sclAdd(Vector2 vec, Vector2 sclVec) {
        x += sclVec.x * vec.x;
        y += sclVec.y * vec.y;
        return this;
    }

    /**
     * First scale a supplied vector, then sub it to this vector.
     * @param scalar The scalar
     * @return This vector for chaining
     */
    public Vector2 sclSub(Vector2 vec, double scalar) {
        x -= scalar * vec.x;
        y -= scalar * vec.y;
        return this;
    }

    /**
     * First scale a supplied vector, then sub it to this vector.
     * @return This vector for chaining
     */
    public Vector2 sclSub(Vector2 vec, Vector2 sclVec) {
        x -= sclVec.x * vec.x;
        y -= sclVec.y * vec.y;
        return this;
    }

    /**
     * Calculates the 2D cross product between this and the given vector.
     * @param v The other vector
     * @return The cross product
     */
    public double crs(Vector2 v) {
        return (this.x * v.y) - (this.y * v.x);
    }

    /**
     * Calculates the 2D cross product between this and the given vector.
     * @param x The x component
     * @param y The y component
     * @return The cross product
     */
    public double crs(double x, double y) {
        return (this.x * y) - (this.y * x);
    }

    /**
     * Calculates the 2D cross product between this and a scalar.
     * @param scalar The scalar
     * @return The cross product
     */
    public Vector2 crs(double scalar) {
        x = scalar * y;
        y = -scalar * x;
        return this;
    }

    /**
     * Calculates the dot product between this and the given vector.
     * @param v The other vector
     * @return The cross product
     */
    public double dot(Vector2 v) {
        return (this.x * v.x) + (this.y * v.y);
    }

    /**
     * Calculates the dot product between this and the given vector.
     * @param x The x component
     * @param y The y component
     * @return The cross product
     */
    public double dot(double x, double y) {
        return (this.x * x) + (this.y * y);
    }

    /**
     * @return The euclidean length of this vector
     */
    public double len() {
        return Math.sqrt(len2());
    }

    /**
     * Compute squared euclidean length avoiding calculating a square root.
     * Useful for comparisons faster than {@link Vector2#len()}
     * @return The squared euclidean length of this vector
     */
    public double len2() {
        return x * x + y * y;
    }

    /**
     * Compute the distance between this and the other vector.
     * @param v The other vector
     * @return The distance between this and the other vector
     */
    public double dst(Vector2 v) {
        return Math.sqrt(dst2(v));
    }

    /**
     * Compute the squared distance between this and the other vector.
     * Useful for comparisons faster than {@link Vector2#dst(Vector2 v)}
     * @param v The other vector
     * @return The squared distance between this and the other vector
     */
    public double dst2(Vector2 v) {
        double dx = x - v.x;
        double dy = y - v.y;
        return dx * dx + dy * dy;
    }

    /**
     * Normalizes this vector. Does nothing if it is zero.
     * @return This vector for chaining
     */
    public Vector2 normalize() {
        double len = len();
        double invLen = 1.0 / len;
        x *= invLen;
        y *= invLen;
        return this;
    }

    /**
     * Left-multiplies this vector by the given matrix
     * @param mat The matrix
     * @return This vector for chaining
     */
    public Vector2 mul(Mat2 mat) {
        set(mat.mul(this));
        return this;
    }
}
