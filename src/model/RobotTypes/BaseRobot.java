package model.RobotTypes;

import model.AbstractModel.BasePhysicalEntity;
import model.AbstractModel.PhysicalEntity;
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

abstract public class BaseRobot extends BasePhysicalEntity implements RobotInterface {
    private double engineL, engineR;
    /**
     * If the simulation is startet without view this will be set on how many seconds will be simulated.
     */
    private int timeToSimulate;
    /**
     * Since all values implemented are working in seconds
     * this ensures the atomic actions of the robot will be changed to the action result / ticsPerSimulatedSecond
     * thus 1000 will mean a single robot run() call will simulate 1 ms of time.
     * to simulate coarser time intervals reduce this number.
     * 2000 = 0.5 ms
     * 1000 = 1 ms
     * 100 = 10ms
     * 1 = 1 second
     */
    private final int ticsPerSimulatedSecond = 500;
    /**
     * in centimeters
     */
    private final double maxSpeed = 8.0, minSpeed = 0.0;
    /**
     * distance between the engines
     */
    private final double distanceE;
    private boolean isPaused = true;
    private double powerTransmission = 0;
    /**
     * in cm
     */
    private double diameters;
    private final Color color;
    private double rotation;
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

    /**
     * Constructs object via Builder
     *
     * @param builder
     */
    public BaseRobot(RobotBuilder builder) {
        super(builder.getArena(),builder.getRandom(), builder.getDiameters(), builder.getDiameters());
        poseRingMemory[poseRingMemoryHead] = builder.getPose();
        System.out.println(builder.getRandom().nextInt());
        pose = builder.getPose();
        engineL = builder.getEngineL();
        engineR = builder.getEngineR();
        distanceE = builder.getDistanceE();
        diameters = builder.getDiameters();
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
        return ((engineR + engineL) / 2) / ticsPerSimulatedSecond;
    }

    /**
     * Calculates angular velocity
     * changes dou to power transmission
     *
     * @return double angle velocity [-MaxSpeed/Distance , MaxSpeed/Distance]
     */
    public double angularVelocity() {
        return (((engineR * (1 - powerTransmission) + engineL * powerTransmission)
                - (engineL * (1 - powerTransmission) + engineR * powerTransmission))
                / distanceE)
                / ticsPerSimulatedSecond;
    }

    /**
     * calculates the next position and sets itself
     */
    public void setNextPosition() {
        pose.incRotation(angularVelocity());
        pose = pose.getPoseInDirection(trajectorySpeed());
    }

    /**
     * while robot is not stop calls behavior and sets to it's next position
     */
    @Override
    public void run() {
        while (!isPaused || timeToSimulate >= 0) {
            behavior();
            setNextPosition();
            collisionDetection();
            updatePositionMemory();
            if (timeToSimulate <= 0) {
                try {
                    sleep(1000 / ticsPerSimulatedSecond);
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
    public boolean isPositionInEntity(Position position) {
        return pose.euclideanDistance(position) <= getRadius();
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
            straight = 0;
            ;
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

    double increaseSpeed(double speed) {
        setEngines(engineR + speed / 2, engineL + speed / 2);
        return trajectorySpeed();
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

    // Ring memory logic

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
            updatePositionMemory();
        }
    }


//Getter & Setter

    @Override
    public Position getClosestPositionInBody(Position position) {
        return pose.getPositionInDirection(getRadius(),pose.calcAngleForPosition(position));
    }

    public Color getColor() {
        return color;
    }

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

    public void togglePause() {
        isPaused = !isPaused;
    }

    @Override
    public boolean getPaused() {
        return isPaused;
    }

    public String toString() {
        return "Engines: " + engineR + " - " + engineL + "\n" + pose;
    }

    @Override
    public boolean equals(PhysicalEntity entity) {
        return pose.equals(entity.getPose()) && color == entity.getColor()
                && entity.isMovable() ==isMovable() && entity.getClass().equals(getClass());
    }
}