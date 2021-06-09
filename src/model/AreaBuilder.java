package model;

import controller.Logger;
import model.RobotTypes.*;

import java.awt.*;
import java.util.Random;

public class AreaBuilder {
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

    public AreaBuilder arena(Arena arena) {
        this.arena = arena;
        return this;
    }

    public AreaBuilder pose(Pose pose) {
        this.pose = pose;
        return this;
    }
    
    public AreaBuilder random(Random random) {
        this.random = random;
        return this;
    }
    

    public AreaBuilder logger(Logger logger) {
        this.logger = logger;
        return this;
    }

    /**
     * Builds Robot without behavior
     *
     * @return BaseRobot
     */
    public Area buildArea() {
        // return new Box(arena,random,);
        return null;
    }
}
