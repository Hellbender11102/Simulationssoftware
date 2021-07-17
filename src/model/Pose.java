package model;

public class Pose extends Position {
    double rotation;

    public Pose(double xCoordinate, double yCoordinate, double rotation) {
        super(xCoordinate, yCoordinate);
        setRotation(rotation);
    }

    public Pose(Position position, double rotation) {
        super(position.getX(), position.getY());
        setRotation(rotation);
    }

    public Pose(Pose pose) {
        super(pose.getX(), pose.getY());
        setRotation(pose.getRotation());
    }

    public Pose clone() {
        return new Pose(xCoordinate, yCoordinate, rotation);
    }

    public void setRotation(double rotation) {
        if (rotation < 0) rotation += 2 * Math.PI;
        this.rotation = rotation % (2 * Math.PI);
    }

    public void incRotation(double rotation) {
        setRotation(this.rotation + rotation);
    }

    public double getRotation() {
        return rotation;
    }

    public boolean equals(Pose position) {
        return super.equals(position) && rotation == position.rotation;
    }

    public Position getPositionInDirection(double distance) {
        return getPositionInDirection(distance, rotation, xCoordinate, yCoordinate);
    }

    public Position getPositionInDirection(double distance, double rotation) {
        return getPositionInDirection(distance, rotation, xCoordinate, yCoordinate);
    }

    public Pose getPoseInDirection(double distance) {
        return new Pose(getPositionInDirection(distance, rotation, xCoordinate, yCoordinate), rotation);
    }

    public Vector2D getVectorInDirection(double distance) {
        return new Vector2D(getPositionInDirection(distance, rotation, 0, 0));
    }

    public Vector2D getVectorInDirection(double distance, double rotation) {
        return new Vector2D(getPositionInDirection(distance, rotation, 0, 0));
    }

    public Pose getPoseInDirection(Vector2D vector2D) {
        return new Pose(xCoordinate + vector2D.getX(), yCoordinate + vector2D.getY(), rotation);
    }

    public Pose getPoseInDirection(double distance, double rotation) {
        return new Pose(getPositionInDirection(distance, rotation, xCoordinate, yCoordinate), rotation);
    }


    /**
     * @param distance double
     * @param rotation double
     * @param x        double
     * @param y        double
     * @return Position
     */
    private static Position getPositionInDirection(double distance, double rotation, double x, double y) {
        x += distance * Math.cos(rotation);
        y += distance * Math.sin(rotation);
        return new Position(x, y);
    }

    /**
     * Returns the angle different with [-PI,PI]
     *
     * @param angleInRadians double
     * @return double
     */
    public double getAngleDiff(double angleInRadians) {
        double retVal = angleInRadians - rotation;
        retVal = retVal >= Math.PI ? retVal - 2 * Math.PI : retVal;
        retVal = retVal < -Math.PI ? retVal + 2 * Math.PI : retVal;
        return retVal;
    }

    @Override
    public String toString() {
        return "Pose x:" + String.format("%,.2f", xCoordinate) +
                ", y:" + String.format("%,.2f", yCoordinate) +
                ", rotation:" + String.format("%,.2f", Math.toDegrees(rotation));
    }
}
