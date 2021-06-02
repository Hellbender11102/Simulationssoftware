package model.RobotTypes;

import model.AbstractModel.BasePhysicalEntity;
import model.AbstractModel.Entity;
import model.AbstractModel.PhysicalEntity;
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
    private final int ticsPerSimulatedSecond = 1000;
    /**
     * in centimeters
     */
    private final double maxSpeed, minSpeed;
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
    private double straightMovesRest;

    /**
     * Constructs object via Builder
     *
     * @param builder
     */
    public BaseRobot(RobotBuilder builder) {
        super(builder.getArena(), builder.getRandom(), builder.getDiameters(), builder.getDiameters());
        poseRingMemory[poseRingMemoryHead] = builder.getPose();
        pose = builder.getPose();
        engineL = builder.getEngineL();
        engineR = builder.getEngineR();
        distanceE = builder.getDistanceE();
        diameters = builder.getDiameters();
        powerTransmission = builder.getPowerTransmission();
        color = new Color(random.nextInt());
        logger = builder.getLogger();
        maxSpeed = builder.getMaxSpeed();
        minSpeed = builder.getMinSpeed();
        timeToSimulate = builder.getTimeToSimulate() * ticsPerSimulatedSecond;
        simulateWithView = builder.getSimulateWithView();
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
        if (rotateToAngle(pose.calcAngleForPosition(position), Math.toRadians(precisionInDegree), speed, speed / 2)) {
            setEngines(speed, speed);
        }
    }

    /**
     * Rotates to correct angleInRadian +- (precisionInRadian / 2)
     *
     * @param angleInRadian  double heading angleInRadian
     * @param rotatingEngine double
     * @param secondEngine   double
     * @return boolean is orientation set to given angle
     */
    boolean rotateToAngle(double angleInRadian, double precisionInRadian, double rotatingEngine, double secondEngine) {
        double second = secondEngine;
        rotatingEngine = Math.max(rotatingEngine, secondEngine);
        secondEngine = second < rotatingEngine ? second:Math.min(rotatingEngine, secondEngine)/2;
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
    public void follow(RobotInterface robot, double speed) {
        driveToPosition(robot.getPose(), 2, speed);
    }

    public void stayGroupedWithRobotType(double distanceToKeep, List<Class> classList, double speed) {
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
            driveToPosition(center, 2, speed);
        } else {
            driveToPosition(dummyPose, 2, speed);
        }
    }

    public Position centerOfGroupWithRobots(List<RobotInterface> group) {
        List<Entity> entityList = new LinkedList<>();
        entityList.addAll(group);
        return centerOfGroupWithEntities(entityList);
    }

    public Position centerOfGroupWithClasses(List<Class> classList) {
        LinkedList<Entity> group = entityGroupByClasses(classList);
        return centerOfGroupWithEntities(group);
    }

    public Position centerOfGroupWithEntities(List<Entity> group) {
        Position center = new Position(0, 0);
        for (Entity entity : group) {
            center.incPosition(entity.getPose());
        }
        center.setXCoordinate(center.getXCoordinate() / group.size());
        center.setYCoordinate(center.getYCoordinate() / group.size());
        return center;
    }

    public void stayGroupedWithAll(double distanceToClosestRobot, double speed) {
        stayGroupedWithRobotType(distanceToClosestRobot, List.of(RobotInterface.class), speed);
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
        double nextD = random.nextDouble();
        double nextDE = exponentialGenerator.nextValue();
        if (isInTurn) {
            if (rotateToAngle(rotation, Math.toRadians(2), speed, speed / 2)) {
                isInTurn = false;
                afterTurn = pose;
            }
        } else if (nextD < 1 / (steps)) {
            isInTurn = true;
            //    if (afterTurn != null)
            //         logger.logDouble(getId() + " Distance", pose.euclideanDistance(afterTurn), 3);
            //     logger.log(getId() + " straight moves", straight + "");
            //     logger.logDouble(getId() + " speed", speed, 3);
            rotation = pose.getRotation() + gaussianGenerator.nextValue();
        } else {
            setEngines(speed, speed);
        }
    }

    public LinkedList<Entity> entityGroupByClasses(List<Class> classList) {
        LinkedList<Entity> entityInGroup = new LinkedList<>();
        for (Entity entity : arena.getPhysicalEntityList()) {
            for (Class c : classList) {
                if (c.isAssignableFrom(entity.getClass()) || entity.getClass().isInstance(c)) {
                    entityInGroup.add(entity);
                }
            }
        }
        return entityInGroup;
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
            System.out.println(pose.getRotation());
            System.out.println(degree);
            System.out.println(turnsTo);
            System.out.println(isInTurn);
        } else {
            if (rotateToAngle(turnsTo, Math.toRadians(2), engine1, engine2)) {
                System.out.println(Math.toDegrees(turnsTo));
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
    public Position getClosestPositionInBody(Position position) {
        return pose.getPositionInDirection(getRadius(), pose.calcAngleForPosition(position));
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
    public int getTimeToSimulate() {
        return timeToSimulate;
    }

    @Override
    public boolean getPaused() {
        return isPaused;
    }

    public String toString() {
        return "Engines: " + engineR + " - " + engineL + "\n" + pose;
    }

    @Override
    public boolean equals(PhysicalEntity physicalEntity) {
        return pose.equals(physicalEntity.getPose()) && color == physicalEntity.getColor()
                && physicalEntity.isMovable() == isMovable() && physicalEntity.getClass().equals(getClass());
    }
}