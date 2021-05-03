package model;

public class Pose extends Position {
    double rotation;

    public Pose(double xCoordinate, double yCoordinate, double rotation) {
        super(xCoordinate, yCoordinate);
        setRotation(rotation);
    }

    Pose(Position position, double rotation) {
        super(position.xCoordinate, position.yCoordinate);
        setRotation(rotation);
    }

    public Pose clone() {
        return new Pose(xCoordinate, yCoordinate, rotation);
    }

    public void setRotation(double rotation) {
        if (rotation < 0) rotation += 2 * Math.PI;
        if (rotation >= 2 * Math.PI) this.rotation = rotation - 2 * Math.PI;
        else this.rotation = rotation;
    }

    public void incRotation(double rotation) {
        setRotation(this.rotation + rotation);
    }

    public double getRotation() {
        return rotation;
    }

    public boolean equals(Pose position) {
        return super.equals(this) && rotation == position.rotation;
    }

    public Position getPositionInDirection(double distance) {
        return getPositionInDirection(distance, rotation, xCoordinate, yCoordinate);
    }

    public Pose getPoseInDirection(double distance) {
        return new Pose(getPositionInDirection(distance, rotation, xCoordinate, yCoordinate), rotation);
    }

    public Position getPositionInDirection(double distance, double rotation) {
        return getPositionInDirection(distance, rotation, xCoordinate, yCoordinate);
    }

    public static Position getPositionInDirection(double distance, double rotation, double x, double y) {
        x += distance * Math.cos(rotation);
        y += distance * Math.sin(rotation);
        return new Position(x, y);
    }

}
