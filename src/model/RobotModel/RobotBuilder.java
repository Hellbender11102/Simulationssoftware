package model.RobotModel;

import model.Pose;
import model.RobotTypes.BaseRobot;
import model.RobotTypes.Robot1;
import model.RobotTypes.Robot2;
import model.RobotTypes.Robot3;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RobotBuilder {
    double engineL;

    public double getEngineL() {
        return engineL;
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

    public ConcurrentLinkedQueue<RobotInterface> getThreadOutputQueue() {
        return threadOutputQueue;
    }

    public Random getRandom() {
        return random;
    }

    double engineR;
    Pose pose;
    double distanceE;
    double powerTransmission = 0;
    int diameters = 20;
    ConcurrentLinkedQueue<RobotInterface> threadOutputQueue;
    Random random;

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

    public RobotBuilder threadOutputQueue(ConcurrentLinkedQueue<RobotInterface> threadOutputQueue) {
        this.threadOutputQueue = threadOutputQueue;
        return this;
    }

    public RobotBuilder pose(Pose pose) {
        this.pose = pose;
        return this;
    }

    public RobotBuilder random(Random random) {
        this.random = random;
        return this;
    }

    /**
     * Builds Robot without behavior
     * @return BaseRobot
     */
    public BaseRobot buildDefault() {
        return new BaseRobot(this) {
            @Override
            public void behavior() {
            }
        };
    }

    /**
     * Builds the robot of type 1
     * @return Robot1
     */
    public Robot1 buildRobot1() {
        return new Robot1(this);
    }
    /**
     * Builds the robot of type 2
     * @return Robot2
     */
    public Robot2 buildRobot2() {
        return new Robot2(this);
    }
        /**
     * Builds the robot of type 3
     * @return Robot3
     */
    public Robot3 buildRobot3() {
        return new Robot3(this);
    }
}
