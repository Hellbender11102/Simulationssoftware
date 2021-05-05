package model.RobotTypes;

import controller.Logger;
import model.Arena;
import model.Pose;
import model.Position;
import model.AbstractModel.RobotBuilder;
import model.AbstractModel.RobotInterface;
import org.uncommons.maths.random.ExponentialGenerator;
import org.uncommons.maths.random.GaussianGenerator;

import java.awt.*;
import java.util.*;
import java.util.List;

abstract public class BaseRobot extends Thread implements RobotInterface {
    private double engineL, engineR;
    private int timeToSimulate;
    private final double maxSpeed = 8.0, minSpeed = 0.0;
    private Pose pose;
    /**
     * distance between the engines
     */
    private final double distanceE;
    private boolean isStop = true;
    private double powerTransmission = 0;
    /**
     * in cm
     */
    private double diameters;
    private final Random random;
    private final Color color;
    private final Arena arena;
    private final Logger logger;
    private double rotation;
    private int ringMemorySize = 100;
    private Pose[] poseRingMemory;
    private Pose afterTurn;
    /**
     * flag for moveRandom()
     */
    private boolean isInTurn = false;
    /**
     * counts how many straight moves have been made until changing direction
     * moveRandom()
     */
    private int straight;
    private int poseRingMemoryHead = 0;
    private int poseRingMemoryPointer = 0;
    /**
     * slows the turn speed
     */
    private int turnModification = 10;

    /**
     * Constructs object via Builder
     *
     * @param builder
     */
    public BaseRobot(RobotBuilder builder) {
        poseRingMemory = new Pose[ringMemorySize];
        engineL = builder.getEngineL();
        engineR = builder.getEngineR();
        distanceE = builder.getDistanceE();
        diameters = builder.getDiameters();
        random = builder.getRandom();
        pose = builder.getPose();
        poseRingMemory[poseRingMemoryHead] = builder.getPose();
        arena = builder.getArena();
        powerTransmission = builder.getPowerTransmission();
        color = new Color(random.nextInt());
        logger = builder.getLogger();
        timeToSimulate = builder.getTimeToSimulate();
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
    public double angularVelocity() {
        return ((engineR * (1 - powerTransmission) + engineL * powerTransmission) -
                (engineL * (1 - powerTransmission) + engineR * powerTransmission)) / distanceE;
    }

    /**
     * calculates the next position and sets itself
     */
    public void setNextPosition() {
        pose.incRotation(angularVelocity() / turnModification);
        pose = pose.getPoseInDirection(trajectorySpeed());
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
        while (!isStop || timeToSimulate >= 0) {
            behavior();
            setNextPosition();
            inArenaBounds();
            collisionDetection();
            poseRingMemory[poseRingMemoryHead] = pose.clone();
            poseRingMemoryHead = (poseRingMemoryHead + 1) % (ringMemorySize - 1);
            if(timeToSimulate !=0) {
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            timeToSimulate--;
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
     * @param position
     * @param precision [0,360]
     * @param speed
     */
    void driveToPosition(Position position, double precision, double speed) {
        if (rotateToAngle(pose.calcAngleForPosition(position), Math.toRadians(precision), speed, speed / 2)) {
            setEngines(speed, speed);
        }
    }

    /**
     * Rotates with to correct angle +- (precision / 2)°
     *
     * @param angle         double heading angle
     * @param rotationSpeed double > 0 && < 1
     * @return boolean
     */
    boolean rotateToAngle(double angle, double precision, double rotationSpeed, double secondEngine) {
        double angleDiff = getAngleDiff(angle);
        if (angleDiff <= precision / 2 || 2 * Math.PI - angleDiff <= precision / 2) {
            return true;
        } else if (angleDiff <= Math.PI) {
            setEngines(secondEngine, rotationSpeed);
            return false;
        } else {
            setEngines(rotationSpeed, secondEngine);
            return false;
        }
    }

    double getAngleDiff(double angle) {
        double angleDiff = (pose.getRotation() - angle) % (2 * Math.PI);
        return angleDiff < 0 ? angleDiff + (2 * Math.PI) : angleDiff;
    }

    public void follow(RobotInterface robot, double speed) {
        driveToPosition(robot.getPose(), 2, speed);
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
            driveToPosition(center, 2, speed);
        } else {
            driveToPosition(dummyPose, 2, speed);
        }
    }

    public Position centerOfGroupWithClasses(List<Class> classList) {
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

    /**
     * @param pathLength        distance the robot will take in average
     * @param speed             settings for both engines
     * @param standardDeviation [0,360]° the robot shall turn
     */
    public void moveRandom(double pathLength, double speed, int standardDeviation) {
        double steps = pathLength / trajectorySpeed();
        ExponentialGenerator exponentialGenerator = new ExponentialGenerator(1 / steps, random);
        GaussianGenerator gaussianGenerator = new GaussianGenerator(0, Math.toRadians(standardDeviation), random);
        double nextD = random.nextDouble();
        double nextDE = exponentialGenerator.nextValue();
        if (isInTurn) {
            if (rotateToAngle(rotation, 2, speed, speed / 2)) {
                isInTurn = false;
                afterTurn = pose;
            }
        } else if (nextD < 1 / (steps)) {
            isInTurn = true;
            if (afterTurn != null)
                logger.logDouble(color.getBlue() + " Distance", pose.euclideanDistance(afterTurn), 3);
            logger.log(color.getBlue() + " straight moves", straight + "");
            logger.logDouble(color.getBlue() + " speed", speed, 3);
            rotation = pose.getRotation() + gaussianGenerator.nextValue();
            straight = 0;;
        } else {
            setEngines(speed, speed);
            straight += 1;
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

//Getter & Setter


    public double getDiameters() {
        return diameters;
    }

    public double getRadius() {
        return diameters / 2.0;
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

    public void setEngineL(double leftEngine) {
        if (isEngineLowerOrMaxSpeed(leftEngine) && isEngineGreaterOrMinSpeed(leftEngine)) {
            engineL = leftEngine;
        } else if (!isEngineLowerOrMaxSpeed(leftEngine)) {
            engineL = maxSpeed;
        } else {
            engineL = minSpeed;
        }
    }

    public void setEngineR(double rightEngine) {
        if (isEngineLowerOrMaxSpeed(rightEngine) && isEngineGreaterOrMinSpeed(rightEngine)) {
            engineR = rightEngine;
        } else if (!isEngineLowerOrMaxSpeed(rightEngine)) {
            engineR = maxSpeed;
        } else {
            engineR = minSpeed;
        }
    }

    public void setEngines(double rightEngine, double leftEngine) {
        if (isEngineLowerOrMaxSpeed(rightEngine) && isEngineGreaterOrMinSpeed(rightEngine)) {
            engineR = rightEngine;
        } else if (!isEngineLowerOrMaxSpeed(rightEngine)) {
            engineR = maxSpeed;
        } else {
            engineR = minSpeed;
        }
        if (isEngineLowerOrMaxSpeed(leftEngine) && isEngineGreaterOrMinSpeed(leftEngine)) {
            engineL = leftEngine;
        } else if (!isEngineLowerOrMaxSpeed(leftEngine)) {
            engineL = maxSpeed;
        } else {
            engineL = minSpeed;
        }
    }

    private boolean isEngineLowerOrMaxSpeed(double engine) {
        return engine <= maxSpeed / 2;
    }

    private boolean isEngineGreaterOrMinSpeed(double engine) {
        return minSpeed <= engine;
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

    // Ring memory logic
    private List<Pose> getPosesFromMemory() {
        List<Pose> poseList = new LinkedList<>();
        for (int i = poseRingMemoryHead - 1; 0 <= i; i--) {
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
    }

    @Override
    public void setNextPose() {
        List<Pose> positions = getPosesFromMemory();
        if (0 < poseRingMemoryPointer) {
            if (poseRingMemoryPointer < positions.size())
                pose = positions.get(poseRingMemoryPointer);
            poseRingMemoryPointer -= 1;
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
    public void setToLatestPose() {
        if (poseRingMemoryHead - 1 < poseRingMemory.length && 0 <= poseRingMemoryHead - 1)
            pose = poseRingMemory[poseRingMemoryHead - 1];
        poseRingMemoryPointer = 0;
    }
}