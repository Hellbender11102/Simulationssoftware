package model;

public class Vector2D {

    private Position origin, destination;
    private double length, polarAngle;
    double x, y;

    public Vector2D(Position origin, Position destination) {
        this.origin = origin;
        this.destination = destination;
        length = Math.hypot(destination.getX() - origin.getX(), destination.getY() - origin.getY());
        polarAngle = Math.atan2(destination.getX() - origin.getX(), destination.getY() - origin.getY());
        x = origin.getX() - destination.getX();
        y = origin.getY() - destination.getY();
    }

    public Vector2D(Position destination) {
        origin = new Position(0, 0);
        destination = destination;
        length = Math.hypot(destination.getX(), destination.getY());
        polarAngle = Math.atan2(destination.getX(), destination.getY());
        x = origin.getX() - destination.getX();
        y = origin.getY() - destination.getY();
    }

    public Vector2D(double x, double y) {
        origin = new Position(0, 0);
        destination = new Position(x, y);
        length = Math.hypot(destination.getX(), destination.getY());
        polarAngle = Math.atan2(destination.getX(), destination.getY());
        this.x = x;
        this.y = y;
    }

    public Vector2D(Position origin, double length, double polarAngle) {
        this.origin = origin;
        this.length = length;
        this.polarAngle = polarAngle;
        destination = new Pose(origin, polarAngle).getPositionInDirection(length);
        x = origin.getX() - destination.getX();
        y = origin.getY() - destination.getY();
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

    public Double scalarProdukt(Vector2D vector) {
        return x * vector.getX() + y * vector.getX();
    }

    public Vector2D normalize() {
        double temp = 1 / length;
        return multiplication(temp);
    }

    public double getLength() {
        return length;
    }

    public double getPolarAngle() {
        return polarAngle;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean containsNaN() {
        return Double.isNaN(x) && Double.isNaN(y) && Double.isNaN(length) && Double.isNaN(polarAngle);
    }

    @Override
    public String toString() {
        return "Vector starts at x:" + origin.getX() + " y:" + origin.getY() + " and goes to x:" + destination.getX() + "y:" + destination.getY();
    }
}
