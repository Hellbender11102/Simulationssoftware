package model;

public class Position {
    private double xCoordinate, yCoordinate;

    public Position(double xCoordinate, double yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public double getPolarAngle() {
        if (getPolarDistance() != 0.0)
            return Math.atan2(yCoordinate, xCoordinate);
        else return 0;
    }

    public double getPolarDistance() {
        return Math.sqrt(Math.exp(xCoordinate) + Math.exp(yCoordinate));
    }

    public boolean equals(Position position) {
        return xCoordinate == position.xCoordinate &&
                yCoordinate == position.yCoordinate;
    }

    @Override
    public String toString() {
        return "Position:" +
                "X:" + String.format("%,.2f", xCoordinate) +
                ", Y:" + String.format("%,.2f", yCoordinate);
    }
}
