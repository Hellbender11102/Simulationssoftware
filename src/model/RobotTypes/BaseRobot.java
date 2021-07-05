package model.RobotTypes;

import controller.Logger;
import model.*;
import model.AbstractModel.BasePhysicalEntity;
import model.AbstractModel.Entity;
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
     * in centimeters
     */
    private final double maxSpeed, minSpeed;
    /**
     * distance between the engines
     */
    private final double distanceE;
    /**
     * The percentage of power transmission
     * from one engine to the other axis
     */
    private final double powerTransmission;
    /**
     * in cm
     */
    private final double diameters;
    /**
     * A memory for an angle to rotate to
     */
    private double rotation;
    /**
     * A memory if the robot currently turns
     */
    boolean isInTurn = false;
    /**
     *
     */
    private double turnsTo = Double.NaN;
    /**
     * Counter to get precises straight moves for a given path length
     */
    private int straightMoves = -1;
    /**
     * Memory for moveRandom
     */
    private int straight = 0;
    /**
     * Memory to count the remaining straight moves
     */
    private double straightMovesRest;
    /**
     * A signal to communicate
     */
    protected boolean signal = false;
    /**
     * A modifier to slowly increase acceleration
     */
    protected double accelerationInPercent = .9 / ticsPerSimulatedSecond;

    private final boolean simulateWithView;

    /**
     * Constructs object via Builder
     *
     * @param builder RobotBuilder
     */
    public BaseRobot(RobotBuilder builder) {
        super(builder.getArena(), builder.getRandom(), builder.getDiameters(), builder.getDiameters(), builder.getPose(), builder.getTicsPerSimulatedSecond());
        poseRingMemory[poseRingMemoryHead] = builder.getPose();
        maxSpeed = builder.getMaxSpeed();
        minSpeed = builder.getMinSpeed();
        setEngineL(builder.getEngineL());
        setEngineR(builder.getEngineR());
        distanceE = builder.getDistanceE();
        diameters = builder.getDiameters();
        powerTransmission = builder.getPowerTransmission();
        logger = builder.getLogger();
        timeToSimulate = builder.getTimeToSimulate() * builder.getTicsPerSimulatedSecond();
        simulateWithView = builder.getSimulateWithView();
    }

    /**
     * Constructs object via Builder
     *
     */
    public BaseRobot(double maxSpeed, double minSpeed, double engineL, double engineR, double distanceE, double diameters,
                     double powerTransmission, Logger logger, int timeToSimulate, boolean simulateWithView,
                     Arena arena,  Random random, Pose pose,int ticsPerSimulatedSecond) {

        super(arena,random, diameters,diameters, pose, ticsPerSimulatedSecond);
        poseRingMemory[poseRingMemoryHead] = pose;
        this.maxSpeed = maxSpeed;
        this.minSpeed = minSpeed;
        setEngineL(engineL);
        setEngineR(engineR);
        this.distanceE = distanceE;
        this.diameters = diameters;
        this.powerTransmission = powerTransmission;
        this.logger = logger;
        this.timeToSimulate = timeToSimulate * ticsPerSimulatedSecond;
        this.simulateWithView = simulateWithView;
    }

    /**
     * Calculates speed for an differential drive
     * Returns resulting speed
     * [minSpeed,maxSpeed]
     *
     * @return double
     */
    public double getTrajectoryMagnitude() {
        return ((engineR + engineL) / 2) / ticsPerSimulatedSecond;
    }

    /**
     * Calculates angular velocity for an differential drive
     * Changes dou to power transmission
     * Returns the angle velocity
     * [-max speed/distance engines, max speed/distance engines]
     *
     * @return double
     */
    public double angularVelocity() {
        return (((engineR * (1 - powerTransmission) + engineL * powerTransmission)
                - (engineL * (1 - powerTransmission) + engineR * powerTransmission))
                / distanceE) / ticsPerSimulatedSecond;
    }

    /**
     * Returns the (last speed + current speed) / 2
     *
     * @return double
     */
    @Override
    public double cmPerSecond() {
        return movingVec.get().getLength() * ticsPerSimulatedSecond;
    }

    /**
     * Calculates and sets the next position
     */
    @Override
    public void setNextPosition() {
        if (!movingVec.get().containsNaN())
            if (getTrajectoryMagnitude() >= 0)
                pose.addToPosition(movingVec.get());
            else pose.subtractFromPosition(movingVec.get());
        pose.incRotation(angularVelocity());
    }

    /**
     * While not paused or simulation time left
     * Calls behavior, collision, next position and ring memory update
     */
    @Override
    public void run() {
        while (!isPaused || (timeToSimulate > 0 && !simulateWithView)) {
            alterMovingVector();
            behavior();
            collisionDetection();
            setNextPosition();
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
     * Calculates and adds the acceleration vector to the moving vector
     * If robot is faster than the engine settings it will slow down by friction
     */
    @Override
    public void alterMovingVector() {
        Vector2D incVec = Vector2D.creatCartesian(getTrajectoryMagnitude() * accelerationInPercent, pose.getRotation());
        Vector2D moving = movingVec.getAcquire();
        if (moving.getLength() < getTrajectoryMagnitude()) {
            moving = moving.add(incVec);
        } else if (moving.getLength() > getTrajectoryMagnitude()) {
            moving = moving.normalize().multiplication(getTrajectoryMagnitude());
        } else if (moving.containsNaN()) {
            moving.set(incVec);
        }

        // rotating again to avoid imprecision
        movingVec.setRelease(moving.rotateTo(pose.getRotation()));
    }

    /**
     * Calculates if position is in robots radius
     *
     * @param position Position
     * @return boolean
     */
    public boolean isPositionInEntity(Position position) {
        return isPositionInEntityCircle(position);
    }

    /**
     * Rotates to Position
     * While sets turning engine to speed and other engine to 0
     * Facing correct sets both to speed
     *
     * @param position          Position
     * @param precisionInDegree double
     * @param speed             double
     */
    public void driveToPosition(Position position, double precisionInDegree, double speed) {
        if (arena.isTorus) position = arena.getClosestPositionInTorus(pose, position);
        if (rotateToAngle(pose.getAngleToPosition(position), Math.toRadians(precisionInDegree), speed, 0)) {
            setEngines(speed, speed);
        }
    }

    /**
     * Rotates to correct angleInRadian +- (precisionInRadian / 2)
     * Sets the outer engine of the circle to rotatingEngine and the inner to secondEngine
     * If facing to the given angle sets the engines both to rotatingEngine
     * Returns true if facing in the correct direction
     * If rotatingEngine <= secondEngine it sets the second engine to 90% original power
     * If rotatingEngine && secondEngine > maxspeed it wont turn
     *
     * @param angleInRadian  double
     * @param rotatingEngine double
     * @param secondEngine   double
     * @return boolean
     */
    boolean rotateToAngle(double angleInRadian, double precisionInRadian, double rotatingEngine, double secondEngine) {
        if (rotatingEngine <= secondEngine) secondEngine = rotatingEngine * 0.9;
        double angleDiff = pose.getAngleDiff(angleInRadian);
        if (angleDiff <= precisionInRadian / 2 || 2 * Math.PI - angleDiff <= precisionInRadian / 2) {
            setEngines(rotatingEngine, rotatingEngine);
            return true;
        } else if (angleDiff <= Math.PI) {
            setEngines(secondEngine, rotatingEngine);
        } else {
            setEngines(rotatingEngine, secondEngine);
        }
        return false;
    }

    /**
     * Follows an given Robot
     *
     * @param robot RobotInterface
     * @param speed double
     */
    public void follow(RobotInterface robot, double precisionInDegree, double speed) {
        driveToPosition(robot.getPose(), precisionInDegree, speed);
    }

    /**
     * Calculates the center of the group as an position
     *
     * @param group List<RobotInterface>
     * @return Position
     */
    public Position centerOfGroupWithRobots(List<RobotInterface> group) {
        List<Entity> entityList = new LinkedList<>();
        entityList.addAll(group);
        return centerOfGroupWithEntities(entityList);
    }

    /**
     * Drives to the center of the group which is created with the class list
     * When any entity from this list is closer than distance to keep the robot will turn and drive in the opposite direction
     *
     * @param distanceToKeep    double
     * @param classList         List<Class>
     * @param speed             double
     * @param precisionInDegree double
     */
    public void stayGroupedWithRobotType(double distanceToKeep, List<Class> classList, double speed, double precisionInDegree) {
        List<RobotInterface> group = robotGroupByClasses(classList);
        Position center = centerOfGroupWithRobots(group);
        Pose dummyPose = new Pose(pose.getX(), pose.getY(), 0);
        boolean isEnoughDistance = true;
        for (RobotInterface robot : group) {
            Position robotPose = robot.getPose();
            if (arena.isTorus) robotPose = arena.getClosestPositionInTorus(pose, robotPose);
            double distance = pose.getEuclideanDistance(robotPose);
            if (!equals(robot) && distance <= distanceToKeep + getRadius() + robot.getRadius()) {
                isEnoughDistance = false;
                double length = distanceToKeep + getRadius() + robot.getRadius() - distance;
                double direction = pose.getAngleFromPosition(robotPose);
                dummyPose.incRotation(dummyPose.getRotation() + direction);
                dummyPose.addToPosition(dummyPose.creatPositionByDecreasing(dummyPose.getPositionInDirection(length)));
            }
        }
        dummyPose.setRotation(dummyPose.getRotation());
        if (isEnoughDistance) {
            driveToPosition(center, precisionInDegree, speed);
        } else {
            driveToPosition(dummyPose, precisionInDegree, speed);
        }
    }

    /**
     * @param classList List<Class>
     * @return LinkedList<RobotInterface>
     */
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

    /**
     * The robots will drive to the center which results from all robot positions
     *
     * @param distanceToClosestRobot double
     * @param speed                  double
     */
    public void stayGroupedWithAllRobots(double distanceToClosestRobot, double speed) {
        stayGroupedWithRobotType(distanceToClosestRobot, List.of(RobotInterface.class), speed, 2);
    }

    /**
     * Returns the distance to the closest entity
     *
     * @return double
     */
    public double distanceToClosestEntity() {
        return distanceToClosestEntityOfClass(List.of(Entity.class));
    }

    /**
     * Calculates the distance to the closest entity of given class
     *
     * @param classList List<Class>
     * @return double
     */
    public double distanceToClosestEntityOfClass(List<Class> classList) {
        LinkedList<Entity> group = entityGroupByClasses(classList);
        double closest = -1;
        for (Entity entity : group) {
            double distance;
            if (arena.isTorus) distance = arena.getEuclideanDistanceToClosestPosition(pose, entity.getPose());
            else distance = pose.getEuclideanDistance(entity.getPose());
            if (!equals(entity)) {
                if (closest == -1) closest = distance;
                else closest = Math.min(closest, distance);
            }
        }
        return closest;
    }

    /**
     * Creates a random movement pattern which can be changed via
     * path length, speed or a standard deviation
     * pathLength is created by an exponential deviation around the given length
     * speed will determine the speed of the robot
     * turnRadiusInDegree is created by an gaussian standard deviation
     *
     * @param pathLength         double
     * @param speed              double
     * @param turnRadiusInDegree int
     */
    public void moveRandom(double pathLength, double speed, int turnRadiusInDegree) {
        double steps = 0;
        ExponentialGenerator exponentialGenerator = new ExponentialGenerator(0, random);
        if (getTrajectoryMagnitude() != 0) {
            steps = pathLength / getTrajectoryMagnitude();
            exponentialGenerator = new ExponentialGenerator(1 / steps, random);
        }
        GaussianGenerator gaussianGenerator = new GaussianGenerator(0, Math.toRadians(turnRadiusInDegree), random);
        double nextDE = exponentialGenerator.nextValue();
        if (isInTurn) {
            if (rotateToAngle(rotation, Math.toRadians(1), speed, 0)) {
                isInTurn = false;
            }
        } else if (straight <= 0) {
            isInTurn = true;
            rotation = (pose.getRotation() + gaussianGenerator.nextValue()) % 2 * Math.PI;
            straight = (int) nextDE;
        } else {
            setEngines(speed, speed);
        }
        straight--;
    }

    /**
     * Increases the speed for booth engines with speed
     * Returns the resulting trajectory speed
     * Useful in an state based agent
     *
     * @param speed double
     * @return double
     */
    public double increaseSpeed(double speed) {
        setEngines(engineL + speed, engineR + speed);
        return getTrajectoryMagnitude();
    }

    /**
     * Turns to an given angle
     * Returns true if facing in the correct direction
     * Useful in an state based agent
     * If engineR = engineL || engineR = engineL = 0 it won't turn
     *
     * @param degree double
     * @return boolean
     */
    boolean turn(double degree) {
        return turn(degree, engineR, engineL);
    }

    //Todo

    /**
     * Turns to an given angle
     * Returns true if turning is complete
     * Useful in an state based agent
     *
     * @param degree  double
     * @param engine1 double
     * @param engine2 double
     * @return boolean
     */
    public boolean turn(double degree, double engine1, double engine2) {
        if (!isInTurn) {
            turnsTo = pose.getRotation() + Math.toRadians(degree) < 0 ?
                    pose.getRotation() + Math.toRadians(degree) + 2 * Math.PI :
                    pose.getRotation() + Math.toRadians(degree) % 2 * Math.PI;
            isInTurn = true;
        } else {
            if (rotateToAngle(turnsTo, Math.toRadians(2), Math.max(engine1, engine2), Math.min(engine1, engine2))) {
                turnsTo = Double.NaN;
                isInTurn = false;
            }
        }
        return !isInTurn;
    }

    /**
     * Moves a given distance
     * resets internal flags when done
     * Returns true if distance is moved
     * Useful in an state based agent
     *
     * @param pathLength double
     * @param speed      double
     * @return boolean
     */
    boolean move(double pathLength, double speed) {
        if (straightMoves == -2) {
            resetFlags();
            return true;
        }
        moveAndStop(pathLength, speed);
        return false;
    }

    /**
     * Moves a given distance and stops with internal flags
     * Useful in an state based agent
     *
     * @param pathLength double
     * @param speed      double
     */
    void moveAndStop(double pathLength, double speed) {
        if (!isInTurn) {
            if (straightMoves == -1 && straightMovesRest == 0) {
                setEngines(speed, speed);
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
    void resetFlags() {
        straightMoves = -1;
        straightMovesRest = 0;
        isInTurn = false;
    }


    private boolean isEngineLowerOrMaxSpeed(double engine) {
        return engine <= maxSpeed;
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

    public String toString() {
        return "Engines: " + engineR + " - " + engineL + "\n" + pose;
    }

    //Getter & Setter

    /**
     * Sets the left engine
     * Cuts at maxSpeed and minSpeed
     *
     * @param leftEngine double
     */
    public void setEngineL(double leftEngine) {
        if (isEngineLowerOrMaxSpeed(leftEngine) && isEngineGreaterOrMinSpeed(leftEngine)) {
            engineL = leftEngine;
        } else if (!isEngineLowerOrMaxSpeed(leftEngine)) {
            engineL = maxSpeed;
        } else {
            engineL = minSpeed;
        }
    }

    /**
     * Sets the right engine
     * Cuts at maxSpeed and minSpeed
     *
     * @param rightEngine double
     */
    public void setEngineR(double rightEngine) {
        if (isEngineLowerOrMaxSpeed(rightEngine) && isEngineGreaterOrMinSpeed(rightEngine)) {
            engineR = rightEngine;
        } else if (!isEngineLowerOrMaxSpeed(rightEngine)) {
            engineR = maxSpeed;
        } else {
            engineR = minSpeed;
        }
    }

    /**
     * Sets both engines
     *
     * @param rightEngine double
     * @param leftEngine  double
     */
    public void setEngines(double leftEngine, double rightEngine) {
        setEngineR(rightEngine);
        setEngineL(leftEngine);
    }

    @Override
    public boolean getSignal() {
        return signal;
    }

    @Override
    public Position getClosestPositionInEntity(Position position) {
        if (pose.getEuclideanDistance(position) < getRadius()) return position;
        return closestPositionInEntityForCircle(position, getRadius());
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
    public double getArea() {
        return getAreaCircle();
    }

    @Override
    public int getTimeToSimulate() {
        return timeToSimulate;
    }

    @Override
    public double getAccelerationInPercent() {
        return accelerationInPercent;
    }
}