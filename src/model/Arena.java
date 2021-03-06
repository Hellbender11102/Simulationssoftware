package model;

import model.abstractModel.Entity;
import model.abstractModel.PhysicalEntity;
import model.abstractModel.RobotInterface;
import model.robotTypes.BaseRobot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Arena {
    private final CopyOnWriteArrayList<Entity> entityList = new CopyOnWriteArrayList<>();
    private final int height, width;
    private static Arena singleton;
    public final boolean isTorus;

    /**
     * Constructor
     *
     * @param width   int
     * @param height  int
     * @param isTorus boolean
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
     * Overwrites the singleton
     * This is only to use for restart purposes
     *
     * @param width   int
     * @param height  int
     * @param isTorus boolean
     */
    synchronized
    public static Arena overWriteInstance(int width, int height, boolean isTorus) {
        singleton.entityList.clear();
        singleton = new Arena(width, height, isTorus);
        return singleton;
    }

    private Arena(int width, int height, boolean isTorus) {
        this.width = Math.max(width, 0);
        this.height = Math.max(height, 0);
        this.isTorus = isTorus;
    }

    /**
     * Returns true if position is in arena bounds
     *
     * @param position Position
     * @return boolean
     */
    public boolean inArenaBounds(Position position) {
        if (position.getX() < 0)
            return false;
        else if (position.getX() > singleton.width)
            return false;
        if (position.getY() < 0)
            return false;
        else return position.getY() <= singleton.height;
    }

    /**
     * Returns the position in the arena as if the arena body is an torus
     *
     * @param position Position
     * @return Position
     */
    public Position setPositionInBoundsTorus(Position position) {
        Position buffPosition = position.clone();
        if (singleton.width > 0) {
            buffPosition.xCoordinate = position.getX() % singleton.width;
            while (buffPosition.getX() < 0) buffPosition.addToPosition(singleton.width, 0);
        } else {
            buffPosition.xCoordinate = 0;
        }
        if (singleton.height > 0) {
            buffPosition.yCoordinate = position.getY() % singleton.height;
            while (buffPosition.getY() < 0) buffPosition.addToPosition(0, singleton.height);
        } else {
            buffPosition.yCoordinate = 0;
        }
        return buffPosition;
    }

    /**
     * Sets the position inside the arena
     *
     * @param position Position
     * @return Position
     */
    public Position setPositionInBounds(Position position) {
        Position buffPosition = position.clone();
        if (buffPosition.getX() < 0) buffPosition.setX(0);
        if (buffPosition.getY() < 0) buffPosition.setY(0);
        if (buffPosition.getX() > singleton.width) buffPosition.setX(singleton.width);
        if (buffPosition.getY() > singleton.height) buffPosition.setY(singleton.height);
        return buffPosition;
    }

    /**
     * Sets the entity in the arena as if the arena body is an torus
     *
     * @param entity Entity
     */
    public void setEntityInTorusArena(Entity entity) {
        entity.getPose().set(setPositionInBoundsTorus(entity.getPose()));
    }

    /**
     * Checks if an position outside of the walls is closer due to the torus arena
     *
     * @param measuringPosition Position
     * @param position          Position
     * @return position
     */
    public Position getClosestPositionInTorus(Position measuringPosition, Position position) {
        double x = position.getX(), y = position.getY();
        if (position.getX() >= singleton.width / 2. && measuringPosition.getX() < singleton.width / 2.)
            x = measuringPosition.getEuclideanDistance(position.creatPositionByIncreasing(-singleton.width, 0))
                    < measuringPosition.getEuclideanDistance(position)
                    ? position.getX() - singleton.width : position.getX();
        else if (position.getX() <= singleton.width / 2. && measuringPosition.getX() > singleton.width / 2.) {
            x = measuringPosition.getEuclideanDistance(position.creatPositionByIncreasing(singleton.width, 0))
                    < measuringPosition.getEuclideanDistance(position)
                    ? position.getX() + singleton.width : position.getX();
        }
        if (position.getY() >= singleton.height / 2. && measuringPosition.getY() < singleton.height / 2.)
            y = measuringPosition.getEuclideanDistance(position.creatPositionByIncreasing(0, -singleton.height))
                    < measuringPosition.getEuclideanDistance(position)
                    ?  position.getY()- singleton.height : position.getY();
        else if (position.getY() <= singleton.height / 2. && measuringPosition.getY() > singleton.height / 2.) {
            y = measuringPosition.getEuclideanDistance(position.creatPositionByIncreasing(0, singleton.height))
                    < measuringPosition.getEuclideanDistance(position)
                    ? position.getY()+ singleton.height : position.getY();
        }
        return new Position(x, y);
    }

    /**
     * Returns the distance between two positions in an torus arena
     *
     * @param position1 Position
     * @param position2 Position
     * @return double
     */
    public double getEuclideanDistanceToClosestPosition(Position position1, Position position2) {
        if (singleton.isTorus)
            return position1.getEuclideanDistance(getClosestPositionInTorus(position1, position2));
        else return position1.getEuclideanDistance(position2);
    }

    /**
     * Returns the distance between two positions in an torus arena
     *
     * @param position1 Position
     * @param position2 Position
     * @return double
     */
    public double getAngleToPosition(Position position1, Position position2) {
        if (singleton.isTorus)
            return position1.getAngleToPosition(getClosestPositionInTorus(position1, position2));
        else return position1.getAngleToPosition(position2);
    }

    @Override
    public String toString() {
        return "Arena width:" + singleton.width + " height:" + singleton.height + " is torus " + isTorus;
    }

     public void addEntities(List<Entity> entities) {
        singleton.entityList.addAll(entities);
    }

     public List<RobotInterface> getRobots() {
        return singleton.entityList.stream().filter(x -> RobotInterface.class.isAssignableFrom(x.getClass())).map(x -> (RobotInterface) x).collect(Collectors.toList());
    }

     public List<PhysicalEntity> getPhysicalEntityList() {
        return singleton.entityList.stream().filter(x -> PhysicalEntity.class.isAssignableFrom(x.getClass())).map(x -> (PhysicalEntity) x).collect(Collectors.toList());
    }

     public List<PhysicalEntity> getPhysicalEntitiesWithoutRobots() {
        return singleton.entityList.stream().filter(
                x -> PhysicalEntity.class.isAssignableFrom(x.getClass()) &&
                        !BaseRobot.class.isAssignableFrom(x.getClass())
        ).map(x -> (PhysicalEntity) x).collect(Collectors.toList());
    }

     public List<Entity> getNonPhysicalEntityList() {
        return singleton.entityList.stream().filter(x -> !x.hasPhysicalBody()).collect(Collectors.toList());
    }

     public void clearEntityList() {
        singleton.entityList.clear();
    }

     public List<Area> getAreaList() {
        return singleton.entityList.stream().filter(x -> Area.class.isAssignableFrom(x.getClass())).map(x -> (Area) x).collect(Collectors.toList());
    }

     public List<Box> getBoxList() {
        return singleton.entityList.stream().filter(x -> Box.class.isAssignableFrom(x.getClass())).map(x -> (Box) x).collect(Collectors.toList());
    }

     public List<Wall> getWallList() {
        return singleton.entityList.stream().filter(x -> Wall.class.isAssignableFrom(x.getClass())).map(x -> (Wall) x).collect(Collectors.toList());
    }

     public List<Entity> getEntityList() {
        return singleton.entityList;
    }

    public int getHeight() {
        return singleton.height;
    }

    public int getWidth() {
        return singleton.width;
    }

    /**
     * Adds an entity to the entity list
     *
     * @param entity Entity
     */
     public void addEntity(Entity entity) {
        if (!singleton.entityList.contains(entity)) {
            singleton.entityList.add(entity);
        }
    }

}
