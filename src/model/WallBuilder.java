package model;

import controller.Logger;
import model.RobotTypes.*;

import java.awt.*;
import java.util.Random;

public class WallBuilder {
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

    public WallBuilder arena(Arena arena) {
        this.arena = arena;
        return this;
    }

    public WallBuilder pose(Pose pose) {
        this.pose = pose;
        return this;
    }
    
    public WallBuilder random(Random random) {
        this.random = random;
        return this;
    }
    

    public WallBuilder logger(Logger logger) {
        this.logger = logger;
        return this;
    }

    /**
     * Builds Robot without behavior
     *
     * @return BaseRobot
     */
    public Wall buildWall() {
        // return new Wall(arena,random,);
        return null;
    }
}
