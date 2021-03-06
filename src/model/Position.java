package model;

import java.awt.geom.Point2D;

public class Position extends Point2D {
    protected double xCoordinate, yCoordinate;

    public Position(Position position) {
        this.xCoordinate = position.getX();
        this.yCoordinate = position.getY();
    }

    public Position(double xCoordinate, double yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    /**
     * Returns the polar angle to this position
     * @return double
     */
    public double getPolarAngle() {
        if (distance(new Position(0, 0)) != 0.0)
            return Math.atan2(yCoordinate, xCoordinate);
        else return java.lang.Double.NaN;
    }

    /**
     * Returns the angle to the given position
     * With atan2(positiony - y, positionx -x)
     * [-pi,pi]
     *
     * @param position Position
     * @return double
     */
    public double getAngleToPosition(Position position) {
        return Math.atan2(position.getY() - yCoordinate, position.getX() - xCoordinate);
    }

    synchronized
    public boolean equals(Position position) {
        return xCoordinate == position.xCoordinate &&
                yCoordinate == position.yCoordinate;
    }

    /**
     * Returns the euclidean distance between this and the given position
     * @param position Position
     * @return double
     */
    public double getEuclideanDistance(Position position) {
        return Math.hypot(position.getX() - xCoordinate, position.getY() - yCoordinate);
    }
    /**
     * Returns the euclidean distance between this and the given coordinates
     * @param x double
     * @param y double
     * @return double
     */
    public double getEuclideanDistance(double x, double y) {
        return Math.hypot(xCoordinate - x, yCoordinate - y);
    }

    synchronized
    public void set(Position position) {
        xCoordinate = position.xCoordinate;
        yCoordinate = position.yCoordinate;
    }

    @Override
    synchronized
    public void setLocation(double x, double y) {
        xCoordinate = x;
        yCoordinate = y;
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
     * @param vector Vector2D
     */
    synchronized
    public Position subtractFromPosition(Vector2D vector) {
        xCoordinate -= vector.getX();
        yCoordinate -= vector.getY();
        return this;
    }

    synchronized
    public Position subtractFromPosition(double x, double y) {
        xCoordinate -= x;
        yCoordinate -= y;
        return this;
    }

    synchronized
    public Position addToPosition(Vector2D vector) {
        xCoordinate += vector.getX();
        yCoordinate += vector.getY();
        return this;
    }

    /**
     * @param vector Vector2D
     * @return Position
     */
    public Position creatPositionByDecreasing(Vector2D vector) {
        return new Position(xCoordinate - vector.getX(), yCoordinate - vector.getY());
    }
    /**
     * @param vector Vector2D
     * @return Position
     */
    public Position creatPositionByIncreasing(Vector2D vector) {
        return new Position(xCoordinate + vector.getX(), yCoordinate + vector.getY());
    }
    /**
     * @param x double
     * @param y double
     * @return Position
     */
    public Position creatPositionByIncreasing(double x,double y) {
        return new Position(xCoordinate + x, yCoordinate + y);
    }

    /**
     * @param x double
     * @param y double
     * @return position
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

    /**
     * Returns the vector to this position
     * @return Vector2D
     */
    public Vector2D toVector() {
        return new Vector2D(xCoordinate, yCoordinate);
    }
}
