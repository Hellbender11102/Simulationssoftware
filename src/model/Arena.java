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
     * @param width     int
     * @param height    int
     * @param isTorus   boolean
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
     * @param width     int
     * @param height    int
     * @param isTorus   boolean
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
     * Returns true if position is in arena bounds
     * @param position Position
     * @return boolean
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

    /**
     * Returns the position in the arena as if the arena body is an torus
     * @param position Position
     * @return Position
     */
    public Position setPositionInBoundsTorus(Position position) {
        Position buffPosition = new Position(position);
        buffPosition.setX(position.getX() % width);
        buffPosition.setY(position.getY() % height);
        if (buffPosition.getX() < 0) buffPosition.addToPosition(width, 0);
        if (buffPosition.getY() < 0) buffPosition.addToPosition(0, height);
        return buffPosition;
    }

    /**
     * Sets the position inside the arena
     * @param position Position
     * @return Position
     */
    public Position setPositionInBounds(Position position) {
            Position buffPosition = new Position(position);
            if (buffPosition.getX() < 0) buffPosition.setX(0);
            if (buffPosition.getY() < 0) buffPosition.setY(0);
            if (buffPosition.getX() > width) buffPosition.setX(width);
            if (buffPosition.getY() > height) buffPosition.setY(height);
            return buffPosition;
    }

    /**
     * Sets the entity in the arena as if the arena body is an torus
     * @param entity Entity
     */
    public void setEntityInTorusArena(Entity entity) {
        entity.getPose().set(setPositionInBoundsTorus(entity.getPose()));
    }

    /**
     * Checks if an position outside of the walls is closer due to the torus arena
     *
     * @param position1 Position
     * @param position2 Position
     * @return position
     */
    public Position getClosestPositionInTorus(Position position1, Position position2) {
        if (position2.getX() > width / 2. && position1.getX() < width / 2.)
            position2 = position1.getEuclideanDistance(position2.creatPositionByDecreasing(width, 0))
                    < position1.getEuclideanDistance(position2) ? position2.creatPositionByDecreasing(width, 0) :
                    position2;
        else if (position2.getX() < width / 2. && position1.getX() > width / 2.) {
            position2 = position1.getEuclideanDistance(position2.creatPositionByDecreasing(-width, 0))
                    < position1.getEuclideanDistance(position2) ? position2.creatPositionByDecreasing(-width, 0) :
                    position2;
        }
        if (position2.getY() > height / 2. && position1.getY() < height / 2.)
            position2 = position1.getEuclideanDistance(position2.creatPositionByDecreasing(0, height))
                    < position1.getEuclideanDistance(position2) ? position2.creatPositionByDecreasing(0, height) :
                    position2;
        else if (position2.getY() < height / 2. && position1.getY() > height / 2.) {
            position2 = position1.getEuclideanDistance(position2.creatPositionByDecreasing(0, -height))
                    < position1.getEuclideanDistance(position2) ? position2.creatPositionByDecreasing(0, -height) :
                    position2;
        }
        return position2;
    }

    /**
     * Returns the distance between two positions in an torus arena
     * @param position1 Position
     * @param position2 Position
     * @return double
     */
    public double getEuclideanDistanceToClosestPosition(Position position1, Position position2) {
        return isTorus ?
                position1.getEuclideanDistance(getClosestPositionInTorus(position1, position2)) :
                position1.getEuclideanDistance(position2);
    }

    @Override
    public String toString() {
        return "width:" + singleton.width + " height:" + singleton.height + " is torus " + isTorus;
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
        return singleton.entityList.stream().filter(x -> !x.isCollidable()).map(x -> (Entity) x).collect(Collectors.toList());
    }
    synchronized public void clearEntityList() {
         singleton.entityList.clear();
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
