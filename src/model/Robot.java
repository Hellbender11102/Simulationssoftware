package model;

public class Robot {
    private double engineL;
    private double engineR;
    private double length = 1, width = 1;
    private Position position;
    final double distanceE;
    double powerTransmission = 0;

    public Robot(double motorR, double motorL, double distanceE) {
        this.engineL = motorL;
        this.engineR = motorR;
        this.distanceE = distanceE;
        this.position = new Position(0, 0, 0);
    }

    public double trajectorySpeed() {
        return (engineR + engineL) / 2;
    }

    public double angularVelocity() {
        return (engineR - engineL) / distanceE;
    }


    public void drive() {
        position.setRotation(position.getRotation() + angularVelocity());
        double rotation = position.getRotation() % 90;
        if (position.getRotation() == 0.0) {
            position.setxCoordinate(position.getxCoordinate() + trajectorySpeed());
        } else if (position.getRotation() == 180.0) {
            position.setxCoordinate(position.getxCoordinate() - trajectorySpeed());
        } else if (position.getRotation() <= 90.0) {
            position.setxCoordinate(position.getxCoordinate() + (trajectorySpeed() * (1 - rotation / 90)));
            position.setyCoordinate(position.getyCoordinate() + (trajectorySpeed() * (rotation / 90)));
        } else if (position.getRotation() <= 180.0) {
            position.setxCoordinate(position.getxCoordinate() - (trajectorySpeed() * (rotation / 90)));
            position.setyCoordinate(position.getyCoordinate() + (trajectorySpeed() * (1 - rotation / 90)));
        } else if (position.getRotation() <= 270.0) {
            position.setxCoordinate(position.getxCoordinate() - (trajectorySpeed() * (1 - rotation / 90)));
            position.setyCoordinate(position.getyCoordinate() - (trajectorySpeed() * (rotation / 90)));
        } else if (position.getRotation() <= 360.0) {
            position.setxCoordinate(position.getxCoordinate() + (trajectorySpeed() * (rotation / 90)));
            position.setyCoordinate(position.getyCoordinate() - (trajectorySpeed() * (1 - rotation / 90)));
        }
    }

    public String toString() {
        return position.toString();
    }

    public Position getPosition() {
        return position;
    }

    public void setmotorL(double motorL) {
        this.engineL = motorL;
    }

    public double getmotorL() {
        return engineL;
    }

    public void setmotorR(double motorR) {
        this.engineR = motorR;
    }

    public double getmotorR() {
        return engineR;
    }
}
