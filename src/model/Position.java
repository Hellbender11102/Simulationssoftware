package model;

public class Position {
    private double xCoordinate, yCoordinate;

    public Position(double xCoordinate, double yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
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


    public double getPolarAngle() {
        if (getPolarDistance() != 0.0)
            return  (Math.atan2(yCoordinate, xCoordinate) / Math.PI) * 180;
        else return Double.NaN;
    }

    synchronized
    public double getPolarDistance() {
        return Math.sqrt(Math.exp(xCoordinate) + Math.exp(yCoordinate));
    }

    synchronized
    public boolean equals(Position position) {
        return xCoordinate == position.xCoordinate &&
                yCoordinate == position.yCoordinate;
    }

    synchronized
    public double euclideanDistance(Position position) {
        return Math.sqrt((Math.pow(position.xCoordinate - xCoordinate, 2)) +
                (Math.pow(position.yCoordinate - yCoordinate, 2)));
    }

    @Override
    public String toString() {
        return "Position:" +
                "X:" + String.format("%,.2f", xCoordinate) +
                ", Y:" + String.format("%,.2f", yCoordinate);
    }

    public void incPosition(Position vector) {
        xCoordinate += vector.xCoordinate;
        yCoordinate += vector.yCoordinate;
    }

    public void incPosition(double addendX, double addendY) {
        xCoordinate += addendX;
        yCoordinate +=addendY;
    }

    public void decPosition(Position vector) {
        xCoordinate -= vector.xCoordinate;
        yCoordinate -= vector.yCoordinate;
    }


    public Position getDiffrence(Position position) {
        return new Position(xCoordinate - position.xCoordinate, yCoordinate - position.yCoordinate);
    }
}
