package model.RobotTypes;

import model.Arena;
import model.Pose;
import model.Position;
import model.RobotModel.RobotBuilder;
import model.RobotModel.RobotInterface;

import java.awt.*;
import java.util.Random;

abstract public class BaseRobot extends Thread implements RobotInterface {
    private double engineL;
    private double engineR;
    private Pose pose;
    private final double distanceE;
    private boolean isStop = false;
    private double powerTransmission = 0;
    private int diameters = 20;
    private final Random random;
    private final Color color;
    private final Arena arena;

    /**
     * Constructs object via Builder
     *
     * @param builder
     */
    public BaseRobot(RobotBuilder builder) {
        this.engineL = builder.getEngineL();
        this.engineR = builder.getEngineR();
        this.distanceE = builder.getDistanceE();
        this.diameters = builder.getDiameters();
        this.random = builder.getRandom();
        this.pose = builder.getPose();
        this.arena = builder.getArena();
        this.powerTransmission = builder.getPowerTransmission();
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
            inArenaBounds();
            collisionDetection();
            try {
                sleep(25);
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
    double calcAngleForPosition(Position position) {
        position.decPosition(pose);
        return position.getAngle();
    }

    /**
     * Rotates to Position and if heading in the correct direction it drives full speed
     *
     * @param position Position
     */
    void driveToPosition(Position position, double speed) {
        if (rotateToAngle(calcAngleForPosition(position), speed)) {
            engineR = speed;
            engineL = speed;
        }
    }

    /**
     * Rotates with to correct angle +- 2°
     *
     * @param angle         double heading angle
     * @param rotationSpeed double > 0 && < 1
     * @return boolean
     */
    boolean rotateToAngle(double angle, double rotationSpeed) {
        double angleDiff = angle - pose.getRotation() <= -360 ? angle - pose.getRotation() +360 :angle - pose.getRotation();
        if (angleDiff < 1. &&
               angleDiff > -1.) {
            return true;
        } else if ((angleDiff < 0 && angleDiff > -180) || angleDiff > 180) {
            engineR = 0;
            engineL = rotationSpeed;
            return false;
        } else {
            engineR = rotationSpeed;
            engineL = 0;
            return false;
        }
    }

    public void follow(RobotInterface robot, double speed) {
        driveToPosition(robot.getPose(), speed);
    }

    public void stayGroupedWithType(double distanceToKeep, Class c) {
        Position center = centerOfGroup(c);
        driveToPosition(center, 1);
    }

    public Position centerOfGroup(Class c) {
        Position center = new Position(0, 0);
        int groupSize = 0;
        for (RobotInterface robot : arena.getRobots()) {
            if (c.isAssignableFrom(robot.getClass()) || robot.getClass().isInstance(c)) {
                center.incPosition(robot.getPose());
                groupSize++;
            }
        }
        center.setXCoordinate(center.getXCoordinate() / groupSize);
        center.setYCoordinate(center.getYCoordinate() / groupSize);
        return center;
    }

    public void stayGrouped(double distanceToClosestRobot) {
        stayGroupedWithType(distanceToClosestRobot, RobotInterface.class);
    }

    public void moveRandom(double pathLength) {

    }

    /**
     * Checks if robots are in the arena bounds
     */
    private void inArenaBounds() {
        if (getPose().getXCoordinate() < getRadius())
            getPose().setXCoordinate(getRadius());
        else if (getPose().getXCoordinate() > arena.getWidth() - getRadius())
            getPose().setXCoordinate(arena.getWidth() - getRadius());
        if (getPose().getYCoordinate() < getRadius())
            getPose().setYCoordinate(getRadius());
        else if (getPose().getYCoordinate() > arena.getHeight() - getRadius())
            getPose().setYCoordinate(arena.getHeight() - getRadius());
    }

    /**
     * Checks for collision between robots
     */
    private void collisionDetection() {
        arena.getRobots().forEach((r2) -> {
            if (getPose().euclideanDistance(r2.getPose()) < getRadius() + r2.getRadius()) {
                if (r2.isPositionInRobotArea(getPose().getPositionInDirection(getRadius() + 0.01))) {
                    //r2 gets bumped
                    bump(this, r2, getPose().getPositionInDirection(trajectorySpeed()));

                } else if (isPositionInRobotArea(r2.getPose().getPositionInDirection(r2.getRadius() + 0.01))) {
                    //this gets pumped
                    bump(r2, this, r2.getPose().getPositionInDirection(r2.trajectorySpeed()));

                } else if (!isPositionInRobotArea(r2.getPose().getPositionInDirection(r2.getRadius() + 0.01))) {
                    //both are bumping cause no one drives directly in each other
                    if (getPose().getXCoordinate() < r2.getPose().getXCoordinate()) {
                        bump(this, r2, new Position(getPose().getXCoordinate() + trajectorySpeed(), getPose().getYCoordinate()));
                        bump(r2, this, new Position(r2.getPose().getXCoordinate() - r2.trajectorySpeed(), r2.getPose().getYCoordinate()));
                    } else {
                        bump(this, r2, new Position(getPose().getXCoordinate() - trajectorySpeed(), getPose().getYCoordinate()));
                        bump(r2, this, new Position(r2.getPose().getXCoordinate() + r2.trajectorySpeed(), r2.getPose().getYCoordinate()));
                    }
                    if (getPose().getYCoordinate() < r2.getPose().getYCoordinate()) {
                        bump(this, r2, new Position(getPose().getXCoordinate(), getPose().getYCoordinate() + trajectorySpeed()));
                        bump(r2, this, new Position(r2.getPose().getXCoordinate(), r2.getPose().getYCoordinate() - r2.trajectorySpeed()));
                    } else {
                        bump(this, r2, new Position(getPose().getXCoordinate(), getPose().getYCoordinate() - trajectorySpeed()));
                        bump(r2, this, new Position(r2.getPose().getXCoordinate(), r2.getPose().getYCoordinate() + r2.trajectorySpeed()));
                    }
                } else System.out.println("Alles doof");
            }
        });
    }

    /**
     * @param bumping                 Robot that bumps
     * @param getsBumped              Robot that gets bumped
     * @param positionInBumpDirection Position in which the bump directs
     */
    private void bump(RobotInterface bumping, RobotInterface getsBumped, Position positionInBumpDirection) {
        Position vector = bumping.getPose().creatPositionByDecreasing(positionInBumpDirection);
        getsBumped.getPose().decPosition(vector);

        if (getsBumped.getPose().getXCoordinate() <= getsBumped.getRadius()) {
            bumping.getPose().incPosition(vector.getXCoordinate(), 0);
        } else if (getsBumped.getPose().getXCoordinate() >= arena.getWidth() - getsBumped.getRadius()) {
            bumping.getPose().incPosition(vector.getXCoordinate(), 0);
        }
        if (getsBumped.getPose().getYCoordinate() <= bumping.getRadius()) {
            bumping.getPose().incPosition(0, vector.getYCoordinate());
        } else if (getsBumped.getPose().getYCoordinate() >= arena.getHeight() - getsBumped.getRadius()) {
            bumping.getPose().incPosition(0, vector.getYCoordinate());
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

    public void setEngineR(double engineR) {
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