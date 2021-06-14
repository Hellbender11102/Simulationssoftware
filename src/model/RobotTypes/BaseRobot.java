package model.RobotTypes;

import model.AbstractModel.BasePhysicalEntity;
import model.AbstractModel.Entity;
import model.Pose;
import model.Position;
import model.RobotBuilder;
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
    final int ticsPerSimulatedSecond = 1000;
    /**
     * in centimeters
     */
    private final double maxSpeed, minSpeed;
    /**
     * distance between the engines
     */
    private final double distanceE;

    private double powerTransmission;
    /**
     * in cm
     */
    private double diameters;
    private final boolean simulateWithView;
    private double rotation;
    private Pose afterTurn;
    /**
     * flag for moveRandom()
     */
    boolean isInTurn = false;
    private double turnsTo = Double.NaN;
    /**
     * Counter to get precises straight moves for a given path length
     */
    private int straightMoves = -1;
    private int straight = 0;
    private double straightMovesRest;
    /**
     * Can be used for distinct logging via Random number
     */
    int identifier = 0;
    boolean signal = false;


    /**
     * Constructs object via Builder
     *
     * @param builder
     */
    public BaseRobot(RobotBuilder builder) {
        super(builder.getArena(), builder.getRandom(), builder.getDiameters(), builder.getDiameters(),builder.getPose());
        poseRingMemory[poseRingMemoryHead] = builder.getPose();
        engineL = builder.getEngineL();
        engineR = builder.getEngineR();
        distanceE = builder.getDistanceE();
        diameters = builder.getDiameters();
        powerTransmission = builder.getPowerTransmission();
        logger = builder.getLogger();
        maxSpeed = builder.getMaxSpeed();
        minSpeed = builder.getMinSpeed();
        timeToSimulate = builder.getTimeToSimulate() * ticsPerSimulatedSecond;
        simulateWithView = builder.getSimulateWithView();
    }

    /**
     * Calculates speed
     *
     * @return doublen
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
        while (!isPaused || (timeToSimulate > 0 && !simulateWithView)) {
            behavior();
            setNextPosition();
            collisionDetection();
            updatePositionMemory();
            if (timeToSimulate <= 0 || simulateWithView) {
                try {
                    sleep(1000 / ticsPerSimulatedSecond);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!simulateWithView) timeToSimulate--;
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
     * @param position          Position
     * @param precisionInDegree double[0,360]
     * @param speed             double[minSpeed,maxSpeed]
     */
    void driveToPosition(Position position, double precisionInDegree, double speed) {
        speed = speed < maxSpeed ? speed / 2 : maxSpeed / 2;
        if (rotateToAngle(pose.calcAngleForPosition(position), Math.toRadians(precisionInDegree), speed, 0)) {
            setEngines(speed, speed);
        }
    }

    /**
     * Rotates to correct angleInRadian +- (precisionInRadian / 2)
     *
     * @param angleInRadian  double heading angleInRadian
     * @param rotatingEngine double [minSpeed,maxSpeed/2]
     * @param secondEngine   double [minSpeed,maxSpeed/2]
     * @return boolean is orientation set to given angle
     */
    boolean rotateToAngle(double angleInRadian, double precisionInRadian, double rotatingEngine, double secondEngine) {
        if (rotatingEngine == secondEngine) secondEngine *= 0.99;
        double angleDiff = getAngleDiff(angleInRadian);
        if (angleDiff <= precisionInRadian / 2 || 2 * Math.PI - angleDiff <= precisionInRadian / 2) {
            return true;
        } else if (angleDiff <= Math.PI) {
            setEngines(secondEngine, rotatingEngine);
        } else {
            setEngines(rotatingEngine, secondEngine);
        }
        return false;
    }

    /**
     * Calculates the difference between current orientation and angleInRadians
     *
     * @param angleInRadians double
     * @return double difference between angles[0,2*PI]
     */
    double getAngleDiff(double angleInRadians) {
        double angleDiff = (pose.getRotation() - angleInRadians) % (2 * Math.PI);
        return angleDiff < 0 ? angleDiff + (2 * Math.PI) : angleDiff;
    }

    /**
     * Follows an given Robot
     *
     * @param robot RobotInterface
     * @param speed double
     */
    public void follow(RobotInterface robot, double percisionInDegree, double speed) {
        driveToPosition(robot.getPose(), percisionInDegree, speed);
    }

    public Position centerOfGroupWithRobots(List<RobotInterface> group) {
        List<Entity> entityList = new LinkedList<>();
        entityList.addAll(group);
        return centerOfGroupWithEntities(entityList);
    }

    public void stayGroupedWithRobotType(double distanceToKeep, List<Class> classList, double speed, double percisionInDegree) {
        List<RobotInterface> group = robotGroupByClasses(classList);
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
            driveToPosition(center, percisionInDegree, speed);
        } else {
            driveToPosition(dummyPose, percisionInDegree, speed);
        }
    }

    public LinkedList<RobotInterface> robotGroupByClasses(List<Class> classList) {
        LinkedList<RobotInterface> entityInGroup = new LinkedList<>();
        for (RobotInterface robot : arena.getRobots()) {
            for (Class c : classList) {
                if (c.isAssignableFrom(robot.getClass()) || robot.getClass().isInstance(c)) {
                    entityInGroup.add(robot);
                }
            }
        }
        return entityInGroup;
    }

    public void stayGroupedWithAll(double distanceToClosestRobot, double speed) {
        stayGroupedWithRobotType(distanceToClosestRobot, List.of(RobotInterface.class), speed, 2);
    }

    public double distanceToClosestEntity() {
        double closest = -1;
        for (Entity entity : arena.getEntityList()) {
            double distance = pose.euclideanDistance(entity.getPose());
            if (!equals(entity)) {
                if (closest == -1) closest = distance;
                else closest = Math.min(closest, distance);
            }
        }
        //distanceToClosestEntityOfClass(List.of(Entity.class))
        return closest;
    }

    public double distanceToClosestEntityOfClass(List<Class> classList) {
        LinkedList<Entity> group = entityGroupByClasses(classList);
        double closest = -1;
        for (Entity entity : group) {
            double distance = pose.euclideanDistance(entity.getPose());
            if (!equals(entity)) {
                if (closest == -1) closest = distance;
                else closest = Math.min(closest, distance);
            }
        }
        return closest;
    }

    /**
     * @param pathLength                distance the robot will take in average
     * @param speed                     settings for both engines
     * @param standardDeviationInDegree [0,360]° the robot shall turn
     */
    public void moveRandom(double pathLength, double speed, int standardDeviationInDegree) {
        double steps = pathLength / trajectorySpeed();
        ExponentialGenerator exponentialGenerator = new ExponentialGenerator(1 / steps, random);
        GaussianGenerator gaussianGenerator = new GaussianGenerator(0, Math.toRadians(standardDeviationInDegree), random);
        double nextDE = exponentialGenerator.nextValue();
        if (isInTurn) {
            if (rotateToAngle(rotation, Math.toRadians(2), speed, 0)) {
                isInTurn = false;
                afterTurn = pose;
            }
        } else if (straight <= 0) {
            isInTurn = true;
            rotation = (pose.getRotation() + gaussianGenerator.nextValue()) % 2 * Math.PI;
            straight = (int) nextDE;
        } else {
            setEngines(speed / 2, speed / 2);
        }
        straight--;
    }


    double increaseSpeed(double speed) {
        setEngines(engineR + speed / 2, engineL + speed / 2);
        return trajectorySpeed();
    }

    boolean turn(double degree) {
        return turn(degree, engineR, engineL);
    }

    //Todo
    boolean turn(double degree, double engine1, double engine2) {
        if (!isInTurn) {
            turnsTo = pose.getRotation() + Math.toRadians(degree) % 2 * Math.PI;
            isInTurn = true;
        } else {
            if (rotateToAngle(turnsTo, Math.toRadians(2), engine1, engine2)) {
                turnsTo = Double.NaN;
                isInTurn = false;
            }
        }
        return !isInTurn;
    }

    /**
     * Moves a given distance
     * resets internal flags when done
     *
     * @param pathLength double length of the path
     * @param speed      double
     * @return true if the path is
     */
    boolean move(double pathLength, double speed) {
        if (straightMoves == -2) {
            reset();
            return true;
        }
        moveAndStop(pathLength, speed);
        return false;
    }

    /**
     * Moves a given distance and stops
     * due to internal flags
     *
     * @param pathLength double
     * @param speed      double
     */
    void moveAndStop(double pathLength, double speed) {
        if (!isInTurn) {
            if (straightMoves == -1 && straightMovesRest == 0) {
                setEngines(speed / 2, speed / 2);
                int moves = (int) Math.round(pathLength / (speed / ticsPerSimulatedSecond));
                straightMoves = moves;
                straightMovesRest = moves - (pathLength / (speed / ticsPerSimulatedSecond));
            } else {
                if (straightMoves == 0 && 0 < straightMovesRest) {
                    setEngines(straightMovesRest, straightMovesRest);
                    straightMovesRest = 0;
                    straightMoves = 1;
                } else if (straightMoves == 0 && 0 == straightMovesRest) {
                    setEngines(0, 0);
                    straightMoves = -1;
                }
                if (-1 <= straightMoves) straightMoves--;
            }
        }
    }

    /**
     * resets all intern flags
     */
    void reset() {
        straightMoves = -1;
        straightMovesRest = 0;
        isInTurn = false;
    }

    /**
     * Sets the left engine
     *
     * @param leftEngine double
     */
    public void setEngineL(double leftEngine) {
        if (isEngineLowerOrMaxSpeed(leftEngine) && isEngineGreaterOrMinSpeed(leftEngine)) {
            engineL = leftEngine;
        } else if (!isEngineLowerOrMaxSpeed(leftEngine)) {
            engineL = maxSpeed / 2;
        } else {
            engineL = minSpeed;
        }
    }

    /**
     * Sets the right engine
     *
     * @param rightEngine double
     */
    public void setEngineR(double rightEngine) {
        if (isEngineLowerOrMaxSpeed(rightEngine) && isEngineGreaterOrMinSpeed(rightEngine)) {
            engineR = rightEngine;
        } else if (!isEngineLowerOrMaxSpeed(rightEngine)) {
            engineR = maxSpeed / 2;
        } else {
            engineR = minSpeed;
        }
    }

    public void setEngines(double rightEngine, double leftEngine) {
        setEngineR(rightEngine);
        setEngineL(leftEngine);
    }

    private boolean isEngineLowerOrMaxSpeed(double engine) {
        return engine <= maxSpeed / 2;
    }

    private boolean isEngineGreaterOrMinSpeed(double engine) {
        return minSpeed <= engine;
    }

    /**
     * When paused sets the robot on the next available position
     * saved in the ring memory.
     * If no further position is existing it will creat a next position
     * and adds it to the memory.
     */
    @Override
    public void setNextPose() {
        if (isPaused) {
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
    }


    //Getter & Setter
    @Override
    public boolean getSignal() {
        return signal;
    }

    @Override
    public Position getClosestPositionInEntity(Position position) {
        return closestPositionInEntityForCircle(position,getRadius());
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

    @Override
    public int getTimeToSimulate() {
        return timeToSimulate;
    }

    // default functions

    public String toString() {
        return "Engines: " + engineR + " - " + engineL + "\n" + pose;
    }

}