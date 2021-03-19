package model;

import java.awt.*;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Robot extends Thread {
    private double engineL;
    private double engineR;
    private Position position;
    private final double distanceE;
    double powerTransmission = 0;
    private int cycles = 10000;
    private ConcurrentLinkedQueue<Robot> threadOutputQueue;
    private final Random random;

    final Color color;

    public Robot(double motorR, double motorL, double distanceE,
                 Position position, ConcurrentLinkedQueue<Robot> threadOutputQueue,Random random) {
        this.engineL = motorL;
        this.engineR = motorR;
        this.distanceE = distanceE;
        this.position = position;
        this.random = random;
        this.threadOutputQueue = threadOutputQueue;
        this.color = new Color(random.nextInt());
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


    @Override
    public void run() {
        while (cycles-- > 0) {
            drive();
            threadOutputQueue.offer(this);
            try {
                sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

        public Color getColor() {
        return color;
    }

    public void start(int cycles) {
        this.cycles = cycles;
        super.start();
    }
}
