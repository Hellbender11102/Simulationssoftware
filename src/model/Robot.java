package model;

public class Robot {
    private double engineL;
    private double engineR;
    private double length = 1, width = 1;
    private double posX = 1, posY = 1, currRotation = 0;
    double distanceE = 1.0;
    double powerTransmission = 0;

    public Robot(double motorL, double motorR) {
        this.engineL = motorL;
        this.engineR = motorR;
    }

    public double angularVelocity() {
        return (engineL + engineR) / 2;
    }

    public double translationSpeed() {
        return (engineL - engineR) / (2 * distanceE);
    }

    public String toString() {
        return "Engine right: " + engineR + "\nEngine left: " + engineL + "\nEngine distance: " + distanceE;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
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

    public void setRotation(double currRotation) {
        this.currRotation = currRotation;
    }

    public double getRotation() {
        return currRotation;
    }
}
