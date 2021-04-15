package model.RobotTypes;

import model.Pose;
import model.Position;
import model.RobotModel.RobotBuilder;
import model.RobotModel.RobotInterface;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

abstract public class BaseRobot extends Thread implements RobotInterface {
    private double engineL;
    private double engineR;
    private Pose pose;
    private final double distanceE;
    private boolean isStop = false;
    private double powerTransmission = 0;
    private int diameters = 20;
    private ConcurrentLinkedQueue<RobotInterface> threadOutputQueue;
    private final Random random;
    private final Color color;

    /**
     * Constructs object via Builder
     *
     * @param builder
     */
    public BaseRobot(RobotBuilder builder) {
        this.engineL = builder.getEngineL();
        this.engineR = builder.getEngineR();
        this.distanceE = builder.getDistanceE();
        this.random = builder.getRandom();
        this.pose = builder.getPose();
        this.threadOutputQueue = builder.getThreadOutputQueue();
        this.color = new Color(random.nextInt());
    }

    /**
     * Calculates speed
     *
     * @return double
     */
    public double trajectorySpeed() {
        return (engineR + engineL) / 2;
    }

    /**
     * Calculates angular velocity
     * changes dou to power transmission
     *
     * @return double angle velocity in degree
     */
    double angularVelocity() {
        return ((engineR * (1 - powerTransmission) + engineL * powerTransmission) -
                (engineL * (1 - powerTransmission) + engineR * powerTransmission)) / distanceE;
    }

    /**
     * calculates the next position and sets itself
     */
    void setNextPosition() {
        pose.incRotation(angularVelocity());

        pose.setXCoordinate(pose.getPositionInDirection(trajectorySpeed()).getXCoordinate());
        pose.setYCoordinate(pose.getPositionInDirection(trajectorySpeed()).getYCoordinate());

        // pose.decPosition(pose.getDiffrence(pose.getPositionInDirection(trajectorySpeed())));
    }

    /**
     * @return Pose
     */
    public Pose getPose() {
        return pose;
    }

    /**
     * while robot is not stop calls behavior and sets to it's next position
     */
    @Override
    public void run() {
        while (!isStop) {
            behavior();
            setNextPosition();
            threadOutputQueue.offer(this);
            try {
                sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Calculates if position is in robots radius
     *
     * @param position Position
     * @return boolean
     */
    public boolean isPositionInRobotArea(Position position) {
        return pose.euclideanDistance(position) <= getRadius();
    }

    /**
     * @return Color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Calculates the angle which the robot's pose must head to hit position
     *
     * @param position Posiition
     * @return double angle in degree
     */
    double calcAngleforPosition(Position position) {
        position.decPosition(pose);
        return position.getPolarAngle() < 0 ? position.getPolarAngle() + 360 : position.getPolarAngle();
    }

    /**
     * Rotates to Position and if heading in the correct direction it drives full speed
     *
     * @param position Position
     */
    void driveToPosition(Position position) {
        if (rotateToAngle(calcAngleforPosition(position), 0.1)) {
            engineR = 1;
            engineL = 1;
        }
    }

    /**
     * Rotates with one engine set 0 and the other one set to rotationspeed until heading to correct angle +- 2°
     *
     * @param angle         double heading angle
     * @param rotationSpeed double > 0 && < 1
     * @return boolean
     */
    boolean rotateToAngle(double angle, double rotationSpeed) {
        rotationSpeed = rotationSpeed < 0 ? Math.abs(rotationSpeed) : rotationSpeed;
        if (angle - pose.getRotation() < 1. &&
                angle - pose.getRotation() > -1.) {
            return true;
        } else if ((angle - pose.getRotation() < 0 && angle - pose.getRotation() > -180) || angle - pose.getRotation() > 180) {
            engineR = 0;
            engineL = rotationSpeed;
            return false;
        } else {
            engineR = rotationSpeed;
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

    public void setEngineL(double engineL) {
        this.engineL = engineL;
    }

    public void settEngineR(double engineR) {
        this.engineR = engineR;
    }

    public void setEngines(double rightEngine, double leftEngine) {
        engineR = rightEngine;
        engineL = leftEngine;
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