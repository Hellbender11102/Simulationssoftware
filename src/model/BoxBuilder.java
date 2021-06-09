package model;

import controller.Logger;
import model.RobotTypes.*;

import java.awt.*;
import java.util.Random;

public class BoxBuilder {
    private Pose pose;
    private Random random;
    private Arena arena;
    private Logger logger;


    public Random getRandom() {
        return random;
    }


    public Pose getPose() {
        return pose;
    }

    public Arena getArena() {
        return arena;
    }

    public Logger getLogger() {
        return logger;
    }

    public BoxBuilder arena(Arena arena) {
        this.arena = arena;
        return this;
    }

    public BoxBuilder pose(Pose pose) {
        this.pose = pose;
        return this;
    }

    public BoxBuilder random(Random random) {
        this.random = random;
        return this;
    }


    public BoxBuilder logger(Logger logger) {
        this.logger = logger;
        return this;
    }

    /**
     * Builds Robot without behavior
     *
     * @return BaseRobot
     */
    public Box buildBox() {
        // return new Box(arena,random,);
        return null;
    }
}
