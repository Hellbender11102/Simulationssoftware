package model;

import model.AbstractModel.Entity;
import model.AbstractModel.PhysicalEntity;
import model.AbstractModel.RobotInterface;
import model.RobotTypes.BaseRobot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Arena {
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

    synchronized public void addEntities(List<Entity> entities) {
        singleton.entityList.addAll(entities);
    }

    synchronized public List<RobotInterface> getRobots() {
        return singleton.entityList.stream().filter(x->RobotInterface.class.isAssignableFrom(x.getClass())).map(x->(RobotInterface) x).collect(Collectors.toList());
    }

    synchronized public List<PhysicalEntity> getPhysicalEntityList() {
        return singleton.entityList.stream().filter(x->PhysicalEntity.class.isAssignableFrom(x.getClass())).map(x->(PhysicalEntity) x).collect(Collectors.toList());
    }
    synchronized public List<PhysicalEntity> getPhysicalEntitiesWithoutRobots() {
        return singleton.entityList.stream().filter(
                x->PhysicalEntity.class.isAssignableFrom(x.getClass()) &&
                        !BaseRobot.class.isAssignableFrom(x.getClass())
        ).map(x->(PhysicalEntity) x).collect(Collectors.toList());
    }

    synchronized public List<Entity> getNonPhysicalEntityList() {
        return singleton.entityList.stream().filter(x->!x.hasAnBody()).map(x->(Entity) x).collect(Collectors.toList());
    }
    synchronized public List<Area> getAreaList() {
        return singleton.entityList.stream().filter(x->Area.class.isAssignableFrom(x.getClass())).map(x->(Area) x).collect(Collectors.toList());
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
