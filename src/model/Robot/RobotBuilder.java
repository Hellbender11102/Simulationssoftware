package model.Robot;

import model.Pose;
import model.Robot1;
import model.Robot2;
import model.Robot3;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RobotBuilder {
    double engineL;
    double engineR;
    Pose pose;
    double distanceE;
    double powerTransmission = 0;
    int diameters = 20;
    ConcurrentLinkedQueue<RobotInterface> threadOutputQueue;
    Random random;
    BaseRobot r;

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

    public BaseRobot buildDefault() {
        return new BaseRobot(this) {
            @Override
            public void behavior() {
            }
        };
    }

    public Robot1 buildRobot1() {
        return new Robot1(this);
    }

    public Robot2 buildRobot2() {
        return new Robot2(this);
    }
    public Robot3 buildRobot3() {
        return new Robot3(this);
    }
}
