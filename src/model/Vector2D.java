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


    public void set(Vector2D vector) {
        x = vector.x;
        y = vector.y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D multiplication(double scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }

    public Vector2D subtract(Vector2D vector) {
        return new Vector2D(x - vector.getX(), y - vector.getX());
    }

    public Vector2D add(Vector2D vector) {
        return new Vector2D(x + vector.getX(), y + vector.getX());
    }

    public Double scalarProduct(Vector2D vector) {
        return x * vector.getX() + y * vector.getX();
    }

    public double cross(Vector2D vector) {
        return x * vector.y - y * vector.x;
    }


    public double dot(Vector2D vector) {
        return x * vector.x + y * vector.y;
    }

    public double project(Vector2D vector) {
        return this.dot(vector) / this.getLength();
    }

    public Vector2D normalize() {
        return divide(getLength());
    }

    public Vector2D divide(double scalar) {
        return new Vector2D(x / scalar, y / scalar);
    }


    public Vector2D getRotatedBy(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector2D(x * cos - y * sin, x * sin + y * cos);
    }

    public static Vector2D creatCartesion(double length, double angle) {
        return new Vector2D(length * Math.cos(angle), length * Math.sin(angle));
    }

    public Vector2D rotateTo(double angle) {
        return creatCartesion(getLength(), angle);
    }

    public double distance(Vector2D vector) {
        return Math.hypot(vector.x - x, vector.y - y);
    }

    public double getLength() {
        return Math.hypot(x, y);
    }

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

    public String toString(){
        return "Vector x:"+x +" y:"+y;
    }

}
