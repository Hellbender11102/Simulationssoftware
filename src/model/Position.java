package model;

public class Position {
    double xCoordinate, yCoordinate;

    public Position(Position position) {
        this.xCoordinate = position.getX();
        this.yCoordinate = position.getY();
    }

    public Position(double xCoordinate, double yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    synchronized
    public Position clone() {
        return new Position(xCoordinate, yCoordinate);
    }


    public double getX() {
        return xCoordinate;
    }

    synchronized
    public void setX(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }


    public double getY() {
        return yCoordinate;
    }

    synchronized
    public void setY(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public double getPolarAngle() {
        if (getPolarDistance() != 0.0)
            return Math.atan2(yCoordinate, xCoordinate);
        else return Double.NaN;
    }

    /**
     * Calculates the PolarAngle for an given position
     *
     * @param position Position
     * @return double[-pi,pi]
     */
    public double calcAngleForPosition(Position position) {
        return position.creatPositionByDecreasing(this).getPolarAngle();
    }


    public double getPolarDistance() {
        return Math.hypot(xCoordinate, yCoordinate);
    }

    synchronized
    public boolean equals(Position position) {
        return xCoordinate == position.xCoordinate &&
                yCoordinate == position.yCoordinate;
    }


    public double euclideanDistance(Position position) {
        return Math.sqrt((position.xCoordinate - xCoordinate) * (position.xCoordinate - xCoordinate) +
                (position.yCoordinate - yCoordinate) * (position.yCoordinate - yCoordinate));
    }

    @Override
    public String toString() {
        return "Position: " +
                "x:" + String.format("%,.2f", xCoordinate) +
                ", y:" + String.format("%,.2f", yCoordinate);
    }

    synchronized
    public void incPosition(Position vector) {
        xCoordinate += vector.xCoordinate;
        yCoordinate += vector.yCoordinate;
    }

    synchronized
    public void incPosition(Vector2D vector) {
        xCoordinate += vector.getX();
        yCoordinate += vector.getY();
    }

    synchronized
    public void incPosition(double addendX, double addendY) {
        xCoordinate += addendX;
        yCoordinate += addendY;
    }

    synchronized
    public void decPosition(Position vector) {
        xCoordinate -= vector.xCoordinate;
        yCoordinate -= vector.yCoordinate;
    }

    synchronized
    public void decPosition(Vector2D vector) {
        xCoordinate -= vector.getX();
        yCoordinate -= vector.getY();
    }

    /**
     * @param vector Position
     * @return position - position
     */
    public Position creatPositionByDecreasing(Position vector) {
        return new Position(xCoordinate - vector.xCoordinate, yCoordinate - vector.yCoordinate);
    }

    /**
     * @param x double
     * @param y double
     * @return position - position
     */
    public Position creatPositionByDecreasing(double x, double y) {
        return new Position(xCoordinate - x, yCoordinate - y);
    }
}
