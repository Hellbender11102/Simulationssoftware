package model;

public class Vector2D {

    double x, y;

    public Vector2D(Position destination) {
        x = destination.getX() - getX();
        y = destination.getY() - getY();
    }

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets this vector with values of given vector
     * @param vector Vector2D
     */
    public void set(Vector2D vector) {
        x = vector.x;
        y = vector.y;
    }

    /**
     * Sets this vector with given x and y
     * @param x double
     * @param y double
     */
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns a new vector as this gets multiplied with an scalar
     * @param scalar double
     * @return Vector2D
     */
    public Vector2D multiplication(double scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }

    /**
     * Returns a new vector as this gets subtracted with given vector
     * @param vector Vector2d
     * @return Vector2D
     */
    public Vector2D subtract(Vector2D vector) {
        return new Vector2D(x - vector.getX(), y - vector.getX());
    }

    /**
     * Returns a new vector as this gets added with given vector
     * @param vector Vector2D
     * @return Vector2D
     */
    public Vector2D add(Vector2D vector) {
        return new Vector2D(x + vector.getX(), y + vector.getX());
    }

    /**
     * Returns the scalar product of this and the given vector
     * @param vector Vector2D
     * @return Vector2D
     */
    public double scalarProduct(Vector2D vector) {
        return x * vector.getX() + y * vector.getX();
    }

    /**
     * Calculates the cross product
     * @param vector Vector2D
     * @return double
     */
    public double cross(Vector2D vector) {
        return x * vector.y - y * vector.x;
    }

    /**
     * Calculates the dot product
     * @param vector Vector2D
     * @return double
     */
    public double dot(Vector2D vector) {
        return x * vector.x + y * vector.y;
    }

    /**
     * Calculates the projection of this and a given vector
     * @param vector Vector2D
     * @return double
     */
    public double project(Vector2D vector) {
        return dot(vector) / getLength();
    }

    /**
     * Returns a new vector as this gets normalized
     * @return Vector2D
     */
    public Vector2D normalize() {
        return divide(getLength());
    }

    /**
     * Returns a new vector as this gets divided by scalar
     * @param scalar double
     * @return Vector2D
     */
    public Vector2D divide(double scalar) {
        return new Vector2D(x / scalar, y / scalar);
    }

    /**
     * Returns a new vector as this gets rotated by this angle
     * @param angle double
     * @return Vector2D
     */
    public Vector2D getRotatedBy(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector2D(x * cos - y * sin, x * sin + y * cos);
    }

    /**
     * Returns a new vector as this gets converted to cartesian form
     * x = length * Math.cos(angle)
     * y = length * Math.sin(angle)
     * @param length double
     * @param angle  double
     * @return Vector2D
     */
    public static Vector2D creatCartesian(double length, double angle) {
        return new Vector2D(length * Math.cos(angle), length * Math.sin(angle));
    }

    /**
     * Returns a new vector as this gets rotated by this angle
     * @param angle double
     * @return Vector2D
     */
    public Vector2D rotateTo(double angle) {
        return creatCartesian(getLength(), angle);
    }

    /**
     * Returns euclidean distance between Point(0,0) and Vector(x,y)
     * sqrt((x - vector.x)² + (y - vector.y)²)
     * @param vector Vector2D
     * @return double
     */
    public double distance(Vector2D vector) {
        return Math.hypot(x - vector.x, y - vector.y);
    }

    /**
     * Returns the euclidean distance between Point(0,0) and Vector(x,y)
     * @return double
     */
    public double getLength() {
        return Math.hypot(x, y);
    }

    /**
     * Returns a new vector as this gets reversed
     * x = -x
     * y = -y
     * @return Vector2D
     */
    public Vector2D reverse() {
        return new Vector2D(-x, -y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean containsNaN() {
        return Double.isNaN(x) && Double.isNaN(y);
    }

    public String toString() {
        return "Vector x:" + x + " y:" + y;
    }
}
