package model;

import model.AbstractModel.Entity;
import model.AbstractModel.PhysicalEntity;
import model.AbstractModel.RobotInterface;

import java.util.ArrayList;
import java.util.List;

public class Arena {
    private List<RobotInterface> robotList = new ArrayList<>();
    private List<PhysicalEntity> physicalEntityList = new ArrayList<>();
    private List<Entity> entityList = new ArrayList<>();
    private final int height, width;
    private static Arena singleton;

    /**
     * Constructor
     *
     * @param width  in centemeter
     * @param height in centemeter
     */
    synchronized
    public static Arena getInstance(int width, int height) {

        if (singleton == null) {
            singleton = new Arena(width, height);
        }
        return singleton;
    }

    /**
     * Constructor
     *
     * @param width  in centemeter
     * @param height in centemeter
     */
    synchronized
    public static Arena overWriteInstance(int width, int height) {
        singleton = new Arena(width, height);
        return singleton;
    }

    private Arena(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "width:" + singleton.width + " height:" + singleton.height;
    }

    synchronized public void setRobots(List<RobotInterface> robotList) {
        singleton.robotList = robotList;
    }

    synchronized public void setPhysicalEntities(List<PhysicalEntity> physicalEntityList) {
        singleton.physicalEntityList = physicalEntityList;
    }

    synchronized public List<RobotInterface> getRobots() {
        return singleton.robotList;
    }

    synchronized public List<PhysicalEntity> getPhysicalEntityList() {
        return singleton.physicalEntityList;
    }
    synchronized public List<Entity> getEntityList() {
        return singleton.entityList;
    }

    public int getHeight() {
        return singleton.height;
    }

    public int getWidth() {
        return singleton.width;
    }
}
