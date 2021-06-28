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

    public double getPolarAngle() {
        if (getPolarDistance() != 0.0)
            return Math.atan2(yCoordinate, xCoordinate);
        else return Double.NaN;
    }

    /**
     * Returns the angle to the given position
     * [-pi,pi]
     *
     * @param position Position
     * @return double
     */
    public double getAngleForPosition(Position position) {
        return Math.atan2(position.getY() - yCoordinate,position.getX()-xCoordinate);
    }

    /**
     * Returns polar distance
     *
     * @return double
     */
    public double getPolarDistance() {
        return Math.hypot(xCoordinate, yCoordinate);
    }

    synchronized
    public boolean equals(Position position) {
        return xCoordinate == position.xCoordinate &&
                yCoordinate == position.yCoordinate;
    }

    public double getEuclideanDistance(Position position) {
        return Math.hypot(position.xCoordinate - xCoordinate, position.yCoordinate - yCoordinate);
    }

    public double getEuclideanDistance(double x, double y) {
        return Math.hypot(xCoordinate - x, yCoordinate - y);
    }

    synchronized
    public void set(double xCoordinate, double yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    synchronized
    public void set(Position position) {
        xCoordinate = position.xCoordinate;
        yCoordinate = position.yCoordinate;
    }


    @Override
    public String toString() {
        return "Position: " +
                "x:" + String.format("%,.2f", xCoordinate) +
                ", y:" + String.format("%,.2f", yCoordinate);
    }

    synchronized
    public void addToPosition(Position position) {
        xCoordinate += position.xCoordinate;
        yCoordinate += position.yCoordinate;
    }


    synchronized
    public void addToPosition(double addendX, double addendY) {
        xCoordinate += addendX;
        yCoordinate += addendY;
    }

    /**
     * 
     * @param vector Vector2D
     */
    synchronized
    public void subtractFromPosition(Vector2D vector) {
        xCoordinate -= vector.getX();
        yCoordinate -= vector.getY();
    }

    synchronized
    public void addToPosition(Vector2D vector) {
        xCoordinate += vector.getX();
        yCoordinate += vector.getY();
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

}
