package model.RobotTypes;

import model.AbstractModel.Entity;
import model.AbstractModel.RobotInterface;
import model.RobotBuilder;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public abstract class LightConeRobot extends BaseRobot {

    double visionRange, visionAngle;

    /**
     * Constructs object via Builder
     *
     * @param builder
     */
    public LightConeRobot(RobotBuilder builder) {
        super(builder);
    }

    boolean isArenaBoundsInVision() {
     return false;
    }

    double distanceToArenaBounds() {
     return 0;
    }

    List<Entity> listOfEntitysInVision() {
     return new LinkedList<>();
    }

    List<RobotInterface> listOfRobotsInVision() {
     return new LinkedList<>();
    }

    List<RobotInterface> listOfRobotsInVisionByCLass() {
        return new LinkedList<>();
    }

}
