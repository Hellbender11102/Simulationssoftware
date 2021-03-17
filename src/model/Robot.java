package model;

public class Robot extends Thread {
    private double engineL;
    private double engineR;
    private int width = 1, hight = 1;
    private Position position;
    private final double distanceE;
    double powerTransmission = 0;
    int cycles;

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
        } else if (position.getRotation() == 90.0) {
            position.setyCoordinate(position.getyCoordinate() + trajectorySpeed());
        } else if (position.getRotation() == 270.0) {
            position.setyCoordinate(position.getyCoordinate() - trajectorySpeed());
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

    public Position getLocalPosition() {
        return position;
    }

    public void setEngineL(double motorL) {
        this.engineL = motorL;
    }

    public double getEngineL() {
        return engineL;
    }

    public void setEngineR(double motorR) {
        this.engineR = motorR;
    }

    public double getEngineR() {
        return engineR;
    }
        public int getWidth() {
        return width;
    }
    public int getHight() {
        return hight;
    }


    @Override
    public void run() {
        for (int i = 0; i < cycles; i++) {
            drive();
            System.out.println(i + ": " + toString());
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void start(int cycles) {
        this.cycles = cycles;
        super.start();
    }
}
