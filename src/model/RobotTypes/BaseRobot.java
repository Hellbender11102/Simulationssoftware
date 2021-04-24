package model.RobotTypes;

import model.Arena;
import model.Pose;
import model.Position;
import model.AbstractModel.EntityBuilder;
import model.AbstractModel.RobotInterface;
import org.uncommons.maths.random.GaussianGenerator;

import java.awt.*;
import java.util.*;
import java.util.List;

abstract public class BaseRobot extends Thread implements RobotInterface {
    private double engineL;
    private double engineR;
    private Pose pose;
    private final double distanceE;
    private boolean isStop = true;
    private double powerTransmission = 0;
    private int diameters = 20;
    private final Random random;
    private final Color color;
    private final Arena arena;
    private boolean isInTurn = false;
    private double rotation;
    private int ringMemorySize = 100;
    private Pose[] poseRingMemory;
    private int poseRingMemoryHead = 0;
    private int poseRingMemoryPointer = 0;

    /**
     * Constructs object via Builder
     *
     * @param builder
     */
    public BaseRobot(EntityBuilder builder) {
        poseRingMemory = new Pose[ringMemorySize];
        this.engineL = builder.getEngineL();
        this.engineR = builder.getEngineR();
        this.distanceE = builder.getDistanceE();
        this.diameters = builder.getDiameters();
        this.random = builder.getRandom();
        this.pose = builder.getPose();
        poseRingMemory[poseRingMemoryHead] = builder.getPose();
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
    }

    /**
     * @return Pose
     */
    synchronized
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
            poseRingMemory[poseRingMemoryHead] = pose.clone();
            poseRingMemoryHead = (poseRingMemoryHead + 1) % (ringMemorySize - 1);
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
     * Rotates to Position and if heading in the correct direction it drives full speed
     *
     * @param position Position
     */
    void driveToPosition(Position position, double speed) {
        if (rotateToAngle(pose.calcAngleForPosition(position), 2, speed, 0)) {
            engineR = speed;
            engineL = speed;
        }
    }

    /**
     * Rotates with to correct angle +- 2°
     *
     * @param angle         double heading angle
     *                      Radians angle in degree [0.0°,360.0°]
     * @param rotationSpeed double > 0 && < 1
     * @return boolean
     */
    boolean rotateToAngle(double angle, double precision, double rotationSpeed, double secondEngine) {
        double angleDiff = (angle - pose.getRotation() + 720) % 360;
        if (angleDiff < precision / 2 || 360 - precision / 2 < angleDiff) {
            return true;
        } else if (180 < angleDiff) {
            engineR = secondEngine;
            engineL = rotationSpeed;
            return false;
        } else {
            engineR = rotationSpeed;
            engineL = secondEngine;
            return false;
        }
    }


    public void follow(RobotInterface robot, double speed) {
        driveToPosition(robot.getPose(), speed);
    }


    public void stayGroupedWithType(double distanceToKeep, List<Class> classList, double speed) {
        List<RobotInterface> group = robotGroupbyClasses(classList);
        Position center = centerOfGroupWithRobots(group);
        Pose dummyPose = new Pose(pose.getXCoordinate(), pose.getYCoordinate(), 0);
        boolean isEnoughDistance = true;
        for (RobotInterface robot : group) {
            Pose robotPose = robot.getPose();
            double distance = pose.euclideanDistance(robotPose);
            if (!equals(robot) && distance <= distanceToKeep + getRadius() + robot.getRadius()) {
                isEnoughDistance = false;
                double length = distanceToKeep + getRadius() + robot.getRadius() - distance;
                double direction = pose.calcAngleForPosition(robotPose);
                dummyPose.incRotation(dummyPose.getRotation() + direction);
                dummyPose.incPosition(dummyPose.creatPositionByDecreasing(dummyPose.getPositionInDirection(length)));
            }
        }
        dummyPose.setRotation(dummyPose.getRotation());
        if (isEnoughDistance) {
            driveToPosition(center, speed);
        } else {
            driveToPosition(dummyPose, speed);
        }
    }

    public Position centerOfGroupWithClasses(List<Class> classList) {
        Position center = new Position(0, 0);
        LinkedList<RobotInterface> group = robotGroupbyClasses(classList);
        return centerOfGroupWithRobots(group);
    }

    public Position centerOfGroupWithRobots(List<RobotInterface> group) {
        Position center = new Position(0, 0);
        for (RobotInterface robot : group) {
            center.incPosition(robot.getPose());
        }
        center.setXCoordinate(center.getXCoordinate() / group.size());
        center.setYCoordinate(center.getYCoordinate() / group.size());
        return center;
    }

    public void stayGroupedWithAll(double distanceToClosestRobot, double speed) {
        stayGroupedWithType(distanceToClosestRobot, List.of(RobotInterface.class), speed);
    }

    public void moveRandom(double pathLength, double speed, int standardDeviation) {
        GaussianGenerator gaussianGenerator = new GaussianGenerator(0, standardDeviation, random);
        double nextD = random.nextDouble();
        if (isInTurn) {
            if (rotateToAngle(rotation, 2, speed, 0)) {
                isInTurn = false;
            }
        } else if (nextD < 1 / (pathLength / trajectorySpeed())) {
            isInTurn = true;
            rotation = pose.getRotation() + gaussianGenerator.nextValue();
        } else {
            setEngines(speed, speed);
        }
    }

    public LinkedList<RobotInterface> robotGroupbyClasses(List<Class> classList) {
        LinkedList<RobotInterface> robotsInGroup = new LinkedList<>();
        for (RobotInterface robot : arena.getRobots()) {
            for (Class c : classList) {
                if (c.isAssignableFrom(robot.getClass()) || robot.getClass().isInstance(c)) {
                    robotsInGroup.add(robot);
                }
            }
        }
        return robotsInGroup;
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

                } else {
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
                }
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

    public Random getRandom() {
        return random;
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

    @Override
    public boolean equals(RobotInterface robot) {
        return pose.equals(robot.getPose()) && engineL == robot.getEngineL() && engineR == robot.getEngineR()
                && diameters == robot.getDiameters() && color == robot.getColor();
    }


    List<Pose> getPosesFromMemory() {
        List<Pose> poseList = new LinkedList<>();
        for (int i = poseRingMemoryHead -1; 0 <= i; i--) {
            if (poseRingMemory[i] != null) poseList.add(poseRingMemory[i]);
        }
        for (int i = ringMemorySize - 1; poseRingMemoryHead < i; i--) {
            if (poseRingMemory[i] != null) poseList.add(poseRingMemory[i]);
        }
        return poseList;
    }

    @Override
    public void setPrevPose() {
        List<Pose> positions = getPosesFromMemory();
        if (poseRingMemoryPointer < positions.size())
            pose = positions.get(poseRingMemoryPointer);
        poseRingMemoryPointer += poseRingMemoryPointer < positions.size() - 1 ? 1 : 0;
        System.out.println("pointer " + poseRingMemoryPointer);
    }

    @Override
    public void setNextPose() {
        List<Pose> positions = getPosesFromMemory();
        if (0 < poseRingMemoryPointer) {
            if (poseRingMemoryPointer < positions.size())
                pose = positions.get(poseRingMemoryPointer);
            poseRingMemoryPointer -= 1;
            System.out.println("pointer " + poseRingMemoryPointer);
        } else {
            behavior();
            setNextPosition();
            inArenaBounds();
            collisionDetection();
            poseRingMemory[poseRingMemoryHead] = pose.clone();
            poseRingMemoryHead = (poseRingMemoryHead + 1) % (ringMemorySize - 1);
        }
    }

    @Override
    public void resetToOrigin() {
        if (poseRingMemoryHead - 1 < poseRingMemory.length && 0 <= poseRingMemoryHead - 1)
            pose = poseRingMemory[poseRingMemoryHead - 1];
        poseRingMemoryPointer = 0;
    }
}