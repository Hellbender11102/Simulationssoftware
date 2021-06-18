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
    public final boolean isTorus; //TODO closest position distance and collision through "jumps"

    /**
     * Constructor
     *
     * @param width  in centemeter
     * @param height in centemeter
     */
    synchronized
    public static Arena getInstance(int width, int height, boolean isTorus) {

        if (singleton == null) {
            singleton = new Arena(width, height, isTorus);
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
    public static Arena overWriteInstance(int width, int height, boolean isTorus) {
        singleton = new Arena(width, height, isTorus);
        return singleton;
    }

    private Arena(int width, int height, boolean isTorus) {
        this.width = width;
        this.height = height;
        this.isTorus = isTorus;
    }

    /**
     * Checks if robots are in the arena bounds
     */
    public boolean inArenaBounds(Position position) {
        if (position.getX() < 0)
            return false;
        else if (position.getX() > width)
            return false;
        if (position.getY() < 0)
            return false;
        else return !(position.getY() > height);
    }

    public Position setPositionInBounds(Position position) {
        Position buffPosition = position;
        buffPosition.setX(position.getX() % width);
        buffPosition.setY(position.getY() % height);
        if (buffPosition.getX() < 0) buffPosition.incPosition(width, 0);
        if (buffPosition.getY() < 0) buffPosition.incPosition(0, height);
        return buffPosition;
    }

    public void setEntityInTorusArena(Entity entity) {
        entity.getPose().setX(setPositionInBounds(entity.getPose()).getX());
        entity.getPose().setY(setPositionInBounds(entity.getPose()).getY());
    }

    /**
     * Checks if an Position outside of the Walls is closer due to the Torus Arena Transformation
     *
     * @param position1 Position start
     * @param position2 Position target
     * @return position
     */
    public Position getClosestPositionInTorus(Position position1, Position position2) {
        if (position2.getX() > width / 2. && position1.getX() < width / 2.)
            position2 = position1.euclideanDistance(position2.creatPositionByDecreasing(width, 0))
                    < position1.euclideanDistance(position2) ? position2.creatPositionByDecreasing(width, 0) :
                    position2;
        else if (position2.getX() < width / 2. && position1.getX() > width / 2.) {
            position2 = position1.euclideanDistance(position2.creatPositionByDecreasing(-width, 0))
                    < position1.euclideanDistance(position2) ? position2.creatPositionByDecreasing(-width, 0) :
                    position2;
        }
        if (position2.getY() > height / 2. && position1.getY() < height / 2.)
            position2 = position1.euclideanDistance(position2.creatPositionByDecreasing(0, height))
                    < position1.euclideanDistance(position2) ? position2.creatPositionByDecreasing(0, height) :
                    position2;
        else if (position2.getY() < height / 2. && position1.getY() > height / 2.) {
            position2 = position1.euclideanDistance(position2.creatPositionByDecreasing(0, -height))
                    < position1.euclideanDistance(position2) ? position2.creatPositionByDecreasing(0, -height) :
                    position2;
        }
        return position2;
    }

    public double getEuclideanDistanceToClosestPosition(Position position1, Position position2) {
        return  isTorus ?
                position1.euclideanDistance(getClosestPositionInTorus(position1,position2)) :
                position1.euclideanDistance(position2);
    }

    @Override
    public String toString() {
        return "width:" + singleton.width + " height:" + singleton.height + " is torus "+ isTorus;
    }

    synchronized public void addEntities(List<Entity> entities) {
        singleton.entityList.addAll(entities);
    }

    synchronized public List<RobotInterface> getRobots() {
        return singleton.entityList.stream().filter(x -> RobotInterface.class.isAssignableFrom(x.getClass())).map(x -> (RobotInterface) x).collect(Collectors.toList());
    }

    synchronized public List<PhysicalEntity> getPhysicalEntityList() {
        return singleton.entityList.stream().filter(x -> PhysicalEntity.class.isAssignableFrom(x.getClass())).map(x -> (PhysicalEntity) x).collect(Collectors.toList());
    }

    synchronized public List<PhysicalEntity> getPhysicalEntitiesWithoutRobots() {
        return singleton.entityList.stream().filter(
                x -> PhysicalEntity.class.isAssignableFrom(x.getClass()) &&
                        !BaseRobot.class.isAssignableFrom(x.getClass())
        ).map(x -> (PhysicalEntity) x).collect(Collectors.toList());
    }

    synchronized public List<Entity> getNonPhysicalEntityList() {
        return singleton.entityList.stream().filter(x -> !x.hasAnBody()).map(x -> (Entity) x).collect(Collectors.toList());
    }

    synchronized public List<Area> getAreaList() {
        return singleton.entityList.stream().filter(x -> Area.class.isAssignableFrom(x.getClass())).map(x -> (Area) x).collect(Collectors.toList());
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
