package model;

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
    Robot r;

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

    public Robot buildDefault() {
        return new Robot(this) {
            @Override
            public void behavior() {

            }
        };
    }
        public Robot2 buildRobot2 () {
            return new Robot2(this);
        }
    }
