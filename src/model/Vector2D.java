package model;


public class Vector2D {

    //TODO REFACTOR
    private Position origin;
    double x, y;

    public Vector2D(Position origin, Position destination) {
        this.origin = origin;
        x = origin.getX() - destination.getX();
        y = origin.getY() - destination.getY();
    }

    public Vector2D(Position target) {
        this.origin = new Position(0,0);
        x = target.getX();
        y = target.getY();
    }

    public Vector2D(double x, double y) {
        origin = new Position(0, 0);
        this.x = x;
        this.y = y;
    }

    public Vector2D(Position origin, double x, double y) {
        this.origin = origin;
        this.x = origin.getX() + x;
        this.y = origin.getY() + y;
    }

    public Vector2D multiplication(double scalar) {
        return new Vector2D(origin,x * scalar, y * scalar);
    }

    public Vector2D subtract(Vector2D vector) {
        return new Vector2D(origin,x - vector.getX(), y - vector.getX());
    }

    public Vector2D add(Vector2D vector) {
        return new Vector2D(origin,x + vector.getX(), y + vector.getX());
    }

    public Double scalarProdukt(Vector2D vector) {
        return x * vector.getX() + y * vector.getX();
    }

    public Vector2D normalize() {
        double temp = 1 / Math.hypot(x,y);;
        return multiplication(temp);
    }

    public double getLength() {
        return Math.hypot(x,y);
    }

    public double getPolarAngle() {
        return Math.atan2(origin.yCoordinate-y,origin.xCoordinate-x);
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

    @Override
    public String toString() {
        return "Vector starts at x:" + origin.getX() + " y:" + origin.getY();
    }
}
