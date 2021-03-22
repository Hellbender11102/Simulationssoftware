package model;

public class Position {
    private double xCoordinate, yCoordinate, rotation;

    public Position(double xCoordinate, double yCoordinate, double rotation) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.rotation = rotation % 360;
    }

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

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        while (rotation < 0) rotation += 360;
        this.rotation = rotation % 360;
    }

    public boolean equals(Position position) {
        return xCoordinate == position.xCoordinate &&
                yCoordinate == position.yCoordinate &&
                rotation == position.rotation;
    }

    @Override
    public String toString() {
        return "Position:" +
                "X:" + String.format("%,.2f", xCoordinate) +
                ", Y:" + String.format("%,.2f", yCoordinate) +
                ", rotation:" + rotation;
    }
}
