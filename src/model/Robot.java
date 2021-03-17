package model;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Robot extends Thread {
    private double engineL;
    private double engineR;
    private final int width = 1, hight = 1;
    private Position position;
    private final double distanceE;
    double powerTransmission = 0;
    int cycles = 10000;
    ConcurrentLinkedQueue<Position> conQueue;

    public Robot(double motorR, double motorL, double distanceE, Position position, ConcurrentLinkedQueue<Position> conQue) {
        this.engineL = motorL;
        this.engineR = motorR;
        this.distanceE = distanceE;
        this.position = position;
        this.conQueue = conQue;
    }

    private double trajectorySpeed() {
        return (engineR + engineL) / 2;
    }

    private double angularVelocity() {
        return (engineR - engineL) / distanceE;
    }


    private synchronized void drive() {
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
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            conQueue.offer(position);

        }
        System.out.println(conQueue.poll());
    }

    public void start(int cycles) {
        this.cycles = cycles;
        super.start();
    }
}
