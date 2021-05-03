package model.AbstractModel;

import controller.Logger;
import model.Arena;
import model.Pose;
import model.RobotTypes.BaseRobot;
import model.RobotTypes.Robot1;
import model.RobotTypes.Robot2;
import model.RobotTypes.Robot3;

import java.awt.*;
import java.util.Random;

public class EntityBuilder {
    private double engineL;

    private double engineR;

    private Pose pose;
    private double distanceE;
    private double powerTransmission = 0;
    private int diameters = 20;
    private Random random;
    private Arena arena;
    private Logger logger;

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

    public Arena getArena() {
        return arena;
    }

    public Logger getLogger() {
        return logger;
    }


    public EntityBuilder engineLeft(double engineL) {
        this.engineL = engineL;
        return this;
    }

    public EntityBuilder engineRight(double engineR) {
        this.engineR = engineR;
        return this;
    }

    public EntityBuilder engineDistnace(double distanceE) {
        this.distanceE = distanceE;
        return this;
    }

    public EntityBuilder powerTransmission(double powerTransmission) {
        this.powerTransmission = powerTransmission;
        return this;
    }

    public EntityBuilder diameters(double diameters) {
        this.diameters = (int) diameters;
        return this;
    }

    public EntityBuilder arena(Arena arena) {
        this.arena = arena;
        return this;
    }

    public EntityBuilder pose(Pose pose) {
        this.pose = pose;
        return this;
    }

    public EntityBuilder random(Random random) {
        this.random = random;
        return this;
    }

    public EntityBuilder logger(Logger logger) {
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
