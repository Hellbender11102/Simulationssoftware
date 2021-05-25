package model;

public class Position {
    double xCoordinate, yCoordinate;

    public Position(double xCoordinate, double yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }


    public Position clone() {
        return new Position(xCoordinate, yCoordinate);
    }

    synchronized
    public double getXCoordinate() {
        return xCoordinate;
    }

    synchronized
    public void setXCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    synchronized
    public double getYCoordinate() {
        return yCoordinate;
    }

    synchronized
    public void setYCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    synchronized
    public double getPolarAngle() {
        if (getPolarDistance() != 0.0)
            return Math.atan2(yCoordinate, xCoordinate);
        else return Double.NaN;
    }


    public double calcAngleForPosition(Position position) {
        return position.creatPositionByDecreasing(this).getPolarAngle();
    }

    synchronized
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
    public void incPosition(double addendX, double addendY) {
        xCoordinate += addendX;
        yCoordinate += addendY;
    }

    synchronized
    public void decPosition(Position vector) {
        xCoordinate -= vector.xCoordinate;
        yCoordinate -= vector.yCoordinate;
    }

    /**
     * @param vector Position
     * @return position - position
     */
    synchronized
    public Position creatPositionByDecreasing(Position vector) {
        return new Position(xCoordinate - vector.xCoordinate, yCoordinate - vector.yCoordinate);
    }
}
