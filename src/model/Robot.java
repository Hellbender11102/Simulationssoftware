package model;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Robot extends Thread {
    private double engineL;
    private double engineR;
    private Pose pose;
    private final double distanceE;
    private boolean isStop = false;
    double powerTransmission = 0;
    private int diameters = 20;
    private ConcurrentLinkedQueue<Robot> threadOutputQueue;
    private final Random random;
    final Color color;

    public Robot(double motorR, double motorL, double distanceE, ConcurrentLinkedQueue<Robot> threadOutputQueue,
                 Random random, Pose pose) {
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


    public double trajectorySpeed() {
        return (engineR + engineL) / 2;
    }

    private double angularVelocity() {
        return ((engineR * (1 - powerTransmission) + engineL * powerTransmission) -
                (engineL * (1 - powerTransmission) + engineR * powerTransmission)) / distanceE;
    }


    private void setNextPosition() {
        pose.incRotation(angularVelocity());

        pose.setXCoordinate(pose.getPositionInDirection(trajectorySpeed()).getXCoordinate());
        pose.setYCoordinate(pose.getPositionInDirection(trajectorySpeed()).getYCoordinate());

        // pose.decPosition(pose.getDiffrence(pose.getPositionInDirection(trajectorySpeed())));
    }

    public Pose getPose() {
        return pose;
    }

    // abstract void behavior();

    @Override
    public void run() {
        while (!isStop) {
            driveToPosition(new Position(250, 250));
            if (isPositionInRobotArea(new Position(250, 250)))
                toggleStop();
            setNextPosition();
            threadOutputQueue.offer(this);
            try {
                sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPositionInRobotArea(Position position) {
        return pose.euclideanDistance(position) <= getRadius();
    }

    public Color getColor() {
        return color;
    }

    private double calcAngleforPosition(Position position) {
        position.decPosition(this.pose);
        return position.getPolarAngle() < 0 ? position.getPolarAngle() + 360 : position.getPolarAngle();
    }

    private void driveToPosition(Position position) {
        double angular = calcAngleforPosition(position);
        if (rotateToAngle(angular)) {
            engineR = 1;
            engineL = 1;
        }
    }

    private boolean rotateToAngle(double angle) {
        if (angle - pose.getRotation() < 1. &&
                angle - pose.getRotation() > -1.) {
            return true;
        } else if ((angle - pose.getRotation() < 0 && angle - pose.getRotation() > -180) || angle - pose.getRotation() > 180) {
            engineR = 0;
            engineL = 0.1;
            return false;
        } else {
            engineR = 0.1;
            engineL = 0;
            return false;
        }
    }

    public int getDiameters() {
        return diameters;
    }

    public int getRadius() {
        return diameters / 2;
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

    public boolean getStop() {
        return isStop;
    }

    public String toString() {
        return "Engines: " + engineR + " - " + engineL + "\n" + pose;
    }
}
