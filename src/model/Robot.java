package model;

public class Robot {
    private double motorL;
    private double motorR;
    private double length=1,width=1;
    double entfernungM = 1.0;
    double leistungsUebertragung = 0;

    public Robot(double motorL, double motorR) {
        this.motorL = motorL;
        this.motorR = motorR;
    }

    public void setmotorL(double motorL) {
        this.motorL = motorL;
    }

    public void setmotorR(double motorR) {
        this.motorR = motorR;
    }

    private double winkelgeschwindigkeit() {
        return (motorL + motorR) / 2;
    }

    private double translationsgeschwindigkeit() {
        return (motorL - motorR) / (2 * entfernungM);
    }

}
