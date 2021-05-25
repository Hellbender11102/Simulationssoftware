package model;

import controller.Logger;
import model.RobotTypes.BaseRobot;
import model.RobotTypes.Robot1;
import model.RobotTypes.Robot2;
import model.RobotTypes.Robot3;

import java.awt.*;
import java.util.Random;

public class RobotBuilder {
    private double engineL, engineR, distanceE, maxSpeed, minSpeed, powerTransmission;
    private Pose pose;
    private int diameters = 20, timeToSimulate;
    private Random random;
    private Arena arena;
    private Logger logger;
    private boolean simulateWithView;

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

    public RobotBuilder engineDistnace(double distanceE) {
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

            @Override
            public Color getClassColor() {
                return Color.BLUE;
            }
        };
    }

    /**
     * Builds the robot of type 1
     *
     * @return Robot1
     */
    public Robot1 buildRobot1() {
        return new Robot1(this);
    }

    /**
     * Builds the robot of type 2
     *
     * @return Robot2
     */
    public Robot2 buildRobot2() {
        return new Robot2(this);
    }

    /**
     * Builds the robot of type 3
     *
     * @return Robot3
     */
    public Robot3 buildRobot3() {
        return new Robot3(this);
    }
}
