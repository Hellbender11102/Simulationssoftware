package helper;

import model.*;
import model.robotTypes.*;

import java.awt.*;
import java.util.Random;

/**
 * Builder class for simple expansions
 */
public class RobotBuilder {
    private double engineL, engineR, distanceE, maxSpeed, minSpeed, powerTransmission, visionRange, visionAngle;
    private Pose pose;
    private int diameters, timeToSimulate;
    private Random random;
    private Arena arena;
    private Logger logger;
    private boolean simulateWithView;
    private int ticsPerSimulatedSecond;


    public double getVisionRange() {
        return visionRange;
    }

    public double getVisionAngle() {
        return visionAngle;
    }

    public double getEngineL() {
        return engineL;
    }

    public Random getRandom() {
        return random;
    }

    public double getEngineR() {
        return engineR;
    }

    public Pose getPose() {
        return pose;
    }

    public int getTicsPerSimulatedSecond() {
        return ticsPerSimulatedSecond;
    }

    public double getDistanceE() {
        return distanceE;
    }

    public double getPowerTransmission() {
        return powerTransmission;
    }

    public int getDiameters() {
        return diameters;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public int getTimeToSimulate() {
        return timeToSimulate;
    }

    public Arena getArena() {
        return arena;
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean getSimulateWithView() {
        return simulateWithView;
    }

    public RobotBuilder engineLeft(double engineL) {
        this.engineL = engineL;
        return this;
    }

    public RobotBuilder engineRight(double engineR) {
        this.engineR = engineR;
        return this;
    }

    public RobotBuilder engineDistance(double distanceE) {
        this.distanceE = distanceE;
        return this;
    }

    public RobotBuilder powerTransmission(double powerTransmission) {
        this.powerTransmission = powerTransmission;
        return this;
    }

    public RobotBuilder diameters(double diameters) {
        this.diameters = (int) diameters;
        return this;
    }

    public RobotBuilder arena(Arena arena) {
        this.arena = arena;
        return this;
    }

    public RobotBuilder pose(Pose pose) {
        this.pose = pose;
        return this;
    }

    public RobotBuilder ticsPerSimulatedSecond(int ticsPerSimulatedSecond) {
        this.ticsPerSimulatedSecond = ticsPerSimulatedSecond;
        return this;
    }

    public RobotBuilder maxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
        return this;
    }

    public RobotBuilder minSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
        return this;
    }

    public RobotBuilder random(Random random) {
        this.random = random;
        return this;
    }

    public RobotBuilder timeToSimulate(int timeToSimulate) {
        this.timeToSimulate = timeToSimulate;
        return this;
    }

    public RobotBuilder simulateWithView(boolean simulateWithView) {
        this.simulateWithView = simulateWithView;
        return this;
    }

    public RobotBuilder logger(Logger logger) {
        this.logger = logger;
        return this;
    }

    public RobotBuilder visionAngle(double visionAngle) {
        this.visionAngle = visionAngle;
        return this;
    }

    public RobotBuilder visionRange(double visionRange) {
        this.visionRange = visionRange;
        return this;
    }

    /**
     * Builds Robot without behavior
     *
     * @return BaseRobot
     */
    public BaseRobot buildDefault() {
        return new BaseRobot(this) {
            @Override
            public void behavior() {
            }
        };
    }

    public BaseVisionConeRobot buildVisionCone() {
        return new BaseVisionConeRobot(this) {
            @Override
            public void behavior() {

            }

            @Override
            public Color getClassColor() {
                return Color.BLUE;
            }
        };
    }

    /**
     * Builds the robot of type grouping
     *
     * @return Grouping
     */
    public Grouping buildGrouping() {
        return new Grouping(this);
    }
    /**
     * Builds the robot of type sheep
     *
     * @return Sheep
     */
    public Sheep buildSheep() {
        return new Sheep(this);
    }
     /**
     * Builds the robot of type GroupingSheep
     *
     * @return GroupingSheep
     */
    public GroupingSheep buildGroupingSheep() {
        return new GroupingSheep(this);
    }

    /**
     * Builds the robot of type SingleDog
     *
     * @return SingleDog
     */
    public SingleDog buildSingleDog() {
        return new SingleDog(this);
    }
    /**
     * Builds the robot of type MultipleDog
     *
     * @return MultipleDog
     */
    public MultipleDog buildMultipleDog() {
        return new MultipleDog(this);
    }

    /* Space to add own robot types */
}
