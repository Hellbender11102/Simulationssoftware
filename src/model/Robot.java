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
    private int diameters = 20;
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


    public double trajectorySpeed() {
        return (engineR + engineL) / 2;
    }

    private double angularVelocity() {
        return (engineR - engineL) / distanceE;
    }


    private void drive() {
        pose.incRotation(angularVelocity());

        pose.setxCoordinate(pose.getPositionInDirection(trajectorySpeed()).getxCoordinate());
        pose.setyCoordinate(pose.getPositionInDirection(trajectorySpeed()).getyCoordinate());

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
            drive();
            threadOutputQueue.offer(this);
            try {
                sleep(15);
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
        return position.getPolarAngle() + 360;
    }

    private void driveToPosition(Position position) {
        double angular = calcAngleforPosition(position) % 360;
        System.out.println("Winkel               :" + angular);
        System.out.println("Winkel - Derzeitigem :" + (angular - pose.getRotation()));
        if (angular - pose.getRotation() < 2. &&
                angular - pose.getRotation() > -2.) {
            System.out.println(0);
            engineR = 5;
            engineL = 5;
        } else if ((angular - pose.getRotation() < 0 && angular - pose.getRotation() > -180) || angular - pose.getRotation() > 180) {
            System.out.println(1);
            engineR = -0.1;
            engineL = 0.1;
        } else {
            System.out.println(2);
            engineR = 0.1;
            engineL = -0.1;
        }

        System.out.println();
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

    public String toString() {
        return "Engines: " + engineR + " - " + engineL + "\n" + pose;
    }
}
