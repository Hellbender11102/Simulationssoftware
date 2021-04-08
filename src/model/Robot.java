package model;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Robot extends Thread {
    private double engineL;
    private double engineR;
    private Pose pose;
    private final double distanceE;
    double powerTransmission = 0;
    private int width = 10, height = 10;
    private ConcurrentLinkedQueue<Robot> threadOutputQueue;
    private final Random random;
    private boolean isStop = false;
    final Color color;

    public Robot(double motorR, double motorL, double distanceE,
                 ConcurrentLinkedQueue<Robot> threadOutputQueue, Random random, Pose pose) {
        this.engineL = motorL;
        this.engineR = motorR;
        this.distanceE = distanceE;
        this.random = random;
        this.pose = pose;
        this.threadOutputQueue = threadOutputQueue;
        this.color = new Color(random.nextInt());
        setDaemon(true);
    }


    public Robot(Robot robot) {
        setDaemon(true);
        this.engineL = robot.engineL;
        this.engineR = robot.engineR;
        this.distanceE = robot.distanceE;
        this.random = robot.random;
        this.threadOutputQueue = robot.threadOutputQueue;
        this.color = robot.color;
        this.pose = robot.pose;
    }


    private double trajectorySpeed() {
        return (engineR + engineL) / 2;
    }

    private double angularVelocity() {
        return (engineR - engineL) / distanceE;
    }


    private synchronized void drive() {
        pose.setRotation(pose.getRotation() + angularVelocity());
        double rotation = pose.getRotation() % 90;
        if (pose.getRotation() == 90) {
            pose.setyCoordinate(pose.getyCoordinate() + (trajectorySpeed()));
        } else if (pose.getRotation() == 180) {
            pose.setxCoordinate(pose.getxCoordinate() + (trajectorySpeed() ));
        } else if (pose.getRotation() == 270) {
            pose.setyCoordinate(pose.getyCoordinate() + (trajectorySpeed() ));
        } else if (pose.getRotation() == 0) {
            pose.setxCoordinate(pose.getxCoordinate() + (trajectorySpeed()));
        } else if (pose.getRotation() < 90.0) {
            pose.setxCoordinate(pose.getxCoordinate() + (trajectorySpeed() * (1 - rotation / 90)));
            pose.setyCoordinate(pose.getyCoordinate() + (trajectorySpeed() * (rotation / 90)));
        } else if (pose.getRotation() < 180.0) {
            pose.setxCoordinate(pose.getxCoordinate() - (trajectorySpeed() * (rotation / 90)));
            pose.setyCoordinate(pose.getyCoordinate() + (trajectorySpeed() * (1 - rotation / 90)));
        } else if (pose.getRotation() < 270.0) {
            pose.setxCoordinate(pose.getxCoordinate() - (trajectorySpeed() * (1 - rotation / 90)));
            pose.setyCoordinate(pose.getyCoordinate() - (trajectorySpeed() * (rotation / 90)));
        } else if (pose.getRotation() < 360.0) {
            pose.setxCoordinate(pose.getxCoordinate() + (trajectorySpeed() * (rotation / 90)));
            pose.setyCoordinate(pose.getyCoordinate() - (trajectorySpeed() * (1 - rotation / 90)));
        }
    }

    public String toString() {
        return pose.toString();
    }

    public Pose getLocalPose() {
        return pose;
    }

    // abstract void behavior();

    @Override
    public void run() {
        while (!isStop) {
            drive();
            threadOutputQueue.offer(this);
            try {
                sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Color getColor() {
        return color;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public double getEngineL() {
        return engineL;
    }

    public double getEngineR() {
        return engineR;
    }

    public void toggleStop() {
        isStop = !isStop;
    }
}
