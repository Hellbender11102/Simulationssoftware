package model.abstractModel;

import model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

abstract public class BasePhysicalEntity extends BaseEntity implements PhysicalEntity {


    /**
     * Since all values implemented are working in seconds
     * this ensures the atomic actions of the robot will be changed to the action result / ticsPerSimulatedSecond
     * thus 1000 will mean a single robot run() call will simulate 1 ms of time.
     * to simulate coarser time intervals reduce this number.
     * 1000 = 1 ms
     * 100 = 10ms
     * 1 = 1 second
     * the lowest time scale is 1 ms
     */
    protected final int ticsPerSimulatedSecond;
    /**
     * Entities will be slowed down by this each step
     * If below .25 objects will increase speed what so ever
     * Best is around .35
     */
    protected final double frictionInPercent = 0.1;
    /**
     * Toggles a different angle at which objects of type wall and box collide
     */
    protected final boolean gettingDifferentAngleToSquares = true;
    protected final boolean simulateWithView;
    /**
     * The moving vector
     */
    protected AtomicReference<Vector2D> movingVec = new AtomicReference<>();

    protected BasePhysicalEntity(Arena arena, Random random, double width, double height, boolean simulateWithView, Pose pose, int ticsPerSimulatedSecond) {
        super(arena, random, width, height, pose);
        movingVec.set(Vector2D.zeroVector());
        this.ticsPerSimulatedSecond = ticsPerSimulatedSecond;
        this.simulateWithView = simulateWithView;
    }

    /**
     * Checks if robots are in the arena bounds
     */
    @Override
    public boolean inArenaBounds() {
        if (getPose().getX() < width / 2)
            return false;
        else if (getPose().getX() > arena.getWidth() - width / 2)
            return false;
        if (getPose().getY() < height / 2)
            return false;
        else return !(getPose().getY() > arena.getHeight() - height / 2);
    }

    /**
     * Calculates and sets the next position
     */
    @Override
    public void setNextPosition() {
        pose.setRotation(movingVec.get().getAngle());
        pose.addToPosition(movingVec.get());
        movingVec.set(movingVec.get());
    }

    /**
     * The code will be run by threaded physical entities
     */
    @Override
    public void run() {
        while (!isPaused) {
            alterMovingVector();
            collisionDetection();
            setNextPosition();
            if (simulateWithView) updatePositionMemory();
            try {
                sleep(1000 / ticsPerSimulatedSecond);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Slows the current velocity by frictionInPercent
     * Current velocity * 1 - frictionInPercent
     */
    @Override
    public void alterMovingVector() {
        movingVec.set(movingVec.get().multiplication(1. - frictionInPercent));
    }


    /**
     * Determines with which entity a collision takes place
     * Calculates the collision and the resulting position
     * Returns if any collision happens
     *
     * @return boolean
     */
    @Override
    public boolean collisionDetection() {
        boolean returnValue = false;
        if (!inArenaBounds() && !arena.isTorus) {
            setInArenaBounds();
        } else if (!inArenaBounds() && arena.isTorus) {
            arena.setEntityInTorusArena(this);
        }
        for (PhysicalEntity physicalEntity : collidingWith()) {
            collision(physicalEntity);
            returnValue = true;
        }
        return returnValue;
    }

    /**
     * Calculates the collision with an elastic shock
     *
     * @param physicalEntity PhysicalEntity
     */
    synchronized public void collision(PhysicalEntity physicalEntity) {
        Position position = pose, positionPe = physicalEntity.getPose();

        //calculate the minimal distance for a collision
        double distance = arena.getEuclideanDistanceToClosestPosition(position, getClosestPositionInEntity(positionPe)) +
                arena.getEuclideanDistanceToClosestPosition(positionPe, physicalEntity.getClosestPositionInEntity(position))
                - arena.getEuclideanDistanceToClosestPosition(position, positionPe);

        double u1Angle = arena.getAngleToPosition(position, positionPe);
        double u2Angle = arena.getAngleToPosition(positionPe, position);

        Vector2D moving1 = movingVec.getAcquire(), moving2 = physicalEntity.getMovingVec().getAcquire();

        double m1 = getWeight(), m2 = physicalEntity.getWeight();
        double v1 = moving1.getLength(), v2 = moving2.getLength();
        if (gettingDifferentAngleToSquares) {
            if (Wall.class.isAssignableFrom(physicalEntity.getClass()) || Box.class.isAssignableFrom(physicalEntity.getClass())) {
                u1Angle = position.getAngleToPosition(physicalEntity.getClosestPositionInEntity(position));
                u2Angle = physicalEntity.getClosestPositionInEntity(position).getAngleToPosition(position);
            }
            if (Wall.class.isAssignableFrom(getClass()) || Box.class.isAssignableFrom(getClass())) {
                u2Angle = positionPe.getAngleToPosition(getClosestPositionInEntity(positionPe));
                u1Angle = getClosestPositionInEntity(positionPe).getAngleToPosition(positionPe);
            }
        }

        double v1x = calcX(v1, v2, m1, m2, moving1.getAngle(), moving2.getAngle(), u1Angle);
        double v1y = calcY(v1, v2, m1, m2, moving1.getAngle(), moving2.getAngle(), u1Angle);

        double v2x = calcX(v2, v1, m2, m1, moving2.getAngle(), moving1.getAngle(), u2Angle);
        double v2y = calcY(v2, v1, m2, m1, moving2.getAngle(), moving1.getAngle(), u2Angle);

        Vector2D resultingPe = new Vector2D(v2x, v2y),
                resulting = new Vector2D(v1x, v1y);

        //ensures correct distance ist kept
        //if entity is to close set them apart
        //also checks for the calculated distance it can cause errors on the edge of the torus
        if (arena.getEuclideanDistanceToClosestPosition(position, physicalEntity.getPose()) - (resultingPe.getLength() + resulting.getLength()) <
                arena.getEuclideanDistanceToClosestPosition(position, getClosestPositionInEntity(positionPe)) +
                        arena.getEuclideanDistanceToClosestPosition(positionPe, physicalEntity.getClosestPositionInEntity(position)) &&
                distance < (physicalEntity.getWeight() + physicalEntity.getHeight()) / 2 + (getWeight() + getHeight()) / 2) {
            if (isMovable() && physicalEntity.isMovable()) {
                pose.addToPosition(Vector2D.creatCartesian(distance / 2, u2Angle));
                physicalEntity.getPose().addToPosition(Vector2D.creatCartesian(distance / 2, u1Angle));
            } else if (!isMovable()) {
                physicalEntity.getPose().addToPosition(Vector2D.creatCartesian(distance, u1Angle));
            } else if (!physicalEntity.isMovable())
                pose.addToPosition(Vector2D.creatCartesian(distance, u2Angle));
        }

        physicalEntity.getMovingVec().setRelease(resultingPe);
        movingVec.setRelease(resulting);

    }

    /**
     * Calculates the x value for an elastic collision
     * v1 vector magnitude of object one
     * v2 vector magnitude of object two
     * m1 mass of object one
     * m2 mass of object two
     * movingAngle1 angle of current moving direction for object one
     * movingAngle2 angle of current moving direction for object two
     *
     * @param v1           double
     * @param v2           double
     * @param m1           double
     * @param m2           double
     * @param movingAngle1 double
     * @param movingAngle2 double
     * @param contactAngle double
     * @return double
     */
    private double calcX(double v1, double v2, double m1, double m2, double movingAngle1, double movingAngle2, double contactAngle) {
        return ((v1 * Math.cos(movingAngle1 - contactAngle) * (m1 - m2) + 2 * m2 * v2 * Math.cos(movingAngle2 - contactAngle)) /
                (m1 + m2))
                * Math.cos(contactAngle) + v1 * Math.sin(movingAngle1 - contactAngle) * Math.cos(contactAngle + (Math.PI / 2));
    }

    /**
     * Calculates the y value for an elastic collision
     * v1 vector magnitude of object one
     * v2 vector magnitude of object two
     * m1 mass of object one
     * m2 mass of object two
     * movingAngle1 angle of current moving direction for object one
     * movingAngle2 angle of current moving direction for object two
     *
     * @param v1           double
     * @param v2           double
     * @param m1           double
     * @param m2           double
     * @param movingAngle1 double
     * @param movingAngle2 double
     * @param contactAngle double
     * @return double
     */
    private double calcY(double v1, double v2, double m1, double m2, double movingAngle1, double movingAngle2, double contactAngle) {
        return ((v1 * Math.cos(movingAngle1 - contactAngle) * (m1 - m2) + 2 * m2 * v2 * Math.cos(movingAngle2 - contactAngle)) /
                (m1 + m2))
                * Math.sin(contactAngle) + v1 * Math.sin(movingAngle1 - contactAngle) * Math.sin(contactAngle + (Math.PI / 2));
    }


    /**
     * Returns all entities where an collision is occurring
     *
     * @return LinkedList<PhysicalEntity>
     */
    public LinkedList<PhysicalEntity> collidingWith() {
        LinkedList<PhysicalEntity> physicalEntities = new LinkedList<>();
        for (PhysicalEntity physicalEntity : arena.getPhysicalEntityList()) {
            if (isPositionInEntity(physicalEntity.getClosestPositionInEntity(pose)) && !equals(physicalEntity)) {
                physicalEntities.add(physicalEntity);
            }
        }
        return physicalEntities;
    }

    /**
     * Checks if robots are in the arena bounds
     */
    @Override
    public void setInArenaBounds() {
        Vector2D vec = movingVec.getAcquire();
        if (pose.getX() < width / 2) {
            pose.setX(width / 2);
            if (vec.getX() + pose.getX() < pose.getX())
                vec.set(new Vector2D(-vec.getX(), vec.getY()));
        } else if (pose.getX() > arena.getWidth() - width / 2) {
            pose.setX(arena.getWidth() - width / 2);
            if (vec.getX() + pose.getX() > pose.getX())
                vec.set(new Vector2D(-vec.getX(), vec.getY()));
        }
        if (pose.getY() < height / 2) {
            pose.setY(height / 2);
            if (vec.getY() + pose.getY() < pose.getY())
                vec.set(new Vector2D(vec.getX(), -vec.getY()));
        } else if (pose.getY() > arena.getHeight() - height / 2) {
            pose.setY(arena.getHeight() - height / 2);
            if (vec.getY() + pose.getY() > pose.getY())
                vec.set(new Vector2D(vec.getX(), -vec.getY()));
        }
        movingVec.setRelease(vec);
    }

    /**
     * Returns the center of a group
     *
     * @param group List<Entity>
     * @return Position
     */
    public Position centerOfGroupWithEntities(List<Entity> group) {
        Position center = new Position(0, 0);
        for (Entity entity : group) {
            Position position = entity.getPose().clone();
            if (arena.isTorus) {
                int groupSize = group.size();
                //checks if the distance is greater to torus positions for each entity in the group

                double distanceToEachEntityInGroup = group.stream().map(Entity::getPose)
                        .map(pose -> pose.getEuclideanDistance(position))
                        .reduce(Double::sum).get();
                double distanceToEachEntityInGroupTorus = group.stream().map(Entity::getPose)
                        .map(pose -> arena.getEuclideanDistanceToClosestPosition(position, pose))
                        .reduce(Double::sum).get();

                Position buffPos = entity.getPose().clone();
                double eucDist;
                double eucDistTorus;
                if (distanceToEachEntityInGroup > distanceToEachEntityInGroupTorus) {
                    List<Position> positionsGreaterWidthHalf = group.stream().map(Entity::getPose)
                            .filter(pose -> pose.getX() >= arena.getWidth() / 2.).collect(Collectors.toList());
                    List<Position> positionsLowerWidthHalf = group.stream().map(Entity::getPose)
                            .filter(pose -> pose.getX() < arena.getWidth() / 2.).collect(Collectors.toList());
                    List<Position> positionsGreaterHeightHalf = group.stream().map(Entity::getPose)
                            .filter(pose -> pose.getY() >= arena.getHeight() / 2.).collect(Collectors.toList());
                    List<Position> positionsLowerHeightHalf = group.stream().map(Entity::getPose)
                            .filter(pose -> pose.getY() < arena.getHeight() / 2.).collect(Collectors.toList());
                    double positionX = position.getX();
                    double positionY = position.getY();

                    // if group is even it will determine which side will be transformed
                    int entityCount = positionsGreaterWidthHalf.size();
                    if (entityCount == positionsLowerWidthHalf.size() && entityCount == groupSize / 2)
                        entityCount -= 1;

                    if (positionX < arena.getWidth() / 2. && entityCount > positionsLowerWidthHalf.size()) {
                        eucDist = positionsGreaterWidthHalf.stream()
                                .map(pos -> Math.abs(pos.getX()- positionX)).reduce(Double::sum).get();
                        eucDistTorus = positionsGreaterWidthHalf.stream()
                                .map(pos -> arena.getEuclideanDistanceToClosestPosition(new Position(pos.getX(),0),
                                        new Position(positionX, 0))).reduce(Double::sum).get();
                        if (eucDistTorus < eucDist - 0.01) { // - 0.01 cause of double rounding issues
                            buffPos.addToPosition(arena.getWidth(), 0);
                        }
                    } else if (positionX > arena.getWidth() / 2. && positionsLowerWidthHalf.size() >= entityCount) {
                        eucDist = positionsLowerWidthHalf.stream()
                                .map(pos -> Math.abs(pos.getX()- positionX)).reduce(Double::sum).get();
                        eucDistTorus = positionsLowerWidthHalf.stream()
                                .map(pos -> arena.getEuclideanDistanceToClosestPosition(new Position(pos.getX(),0),
                                        new Position(positionX, 0))).reduce(Double::sum).get();
                        if (eucDistTorus <= eucDist - 0.01) {
                            buffPos.addToPosition(-arena.getWidth(), 0);
                        }
                    }
                    entityCount = positionsGreaterHeightHalf.size();
                    if (entityCount == positionsLowerHeightHalf.size() && entityCount == groupSize / 2)
                        entityCount -= 1;

                    if (positionY < arena.getHeight() / 2. && entityCount > positionsLowerHeightHalf.size()) {
                        eucDist = positionsGreaterHeightHalf.stream()
                                .map(pos -> Math.abs(pos.getY()- positionY)).reduce(Double::sum).get();
                        eucDistTorus = positionsGreaterHeightHalf.stream()
                                .map(pos -> arena.getEuclideanDistanceToClosestPosition(new Position(0, pos.getY()),
                                        new Position(0, positionY))).reduce(Double::sum).get();
                        if (eucDistTorus < eucDist - 0.01) {
                            buffPos.addToPosition(0, arena.getHeight());
                        }
                    } else if (positionY > arena.getHeight() / 2. && positionsLowerHeightHalf.size() >= entityCount) {
                        eucDist = positionsLowerHeightHalf.stream()
                                .map(pos -> Math.abs(pos.getY()- positionY)).reduce(Double::sum).get();
                        eucDistTorus = positionsLowerHeightHalf.stream()
                                .map(pos -> arena.getEuclideanDistanceToClosestPosition(new Position(0, pos.getY()),
                                        new Position(0, positionY))).reduce(Double::sum).get();
                        if (eucDistTorus <= eucDist - 0.01) {
                            buffPos.addToPosition(0, -arena.getHeight());
                        }
                    }
                }
                position.set(buffPos);
            }
            center.addToPosition(position);
        }
        center.setX(center.getX() / group.size());
        center.setY(center.getY() / group.size());
        if (arena.isTorus)
            center = arena.setPositionInBoundsTorus(center);
        return center;
    }

    /**
     * Returns the center of a group composed of all entities which are assignable from any given class
     *
     * @param classList List<Class>
     * @return Position
     */
    public Position centerOfGroupWithClasses(List<Class> classList) {
        LinkedList<Entity> group = entityGroupByClasses(classList);
        return centerOfGroupWithEntities(group);
    }

    /**
     * Returns a list of all entities which are assignable from any given class
     *
     * @param classList List<Class>
     * @return LinkedList<Entity>
     */
    public LinkedList<Entity> entityGroupByClasses(List<Class> classList) {
        LinkedList<Entity> entityInGroup = new LinkedList<>();
        for (Entity entity : arena.getEntityList()) {
            for (Class c : classList) {
                if (c.isAssignableFrom(entity.getClass()) || c == entity.getClass()) {
                    entityInGroup.add(entity);
                }
            }
        }
        return entityInGroup;
    }

    @Override
    public boolean hasPhysicalBody() {
        return true;
    }

    // getter
    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public double getWeight() {
        return getArea();
    }

    @Override
    public boolean isMovable() {
        return true;
    }

    @Override
    public AtomicReference<Vector2D> getMovingVec() {
        return movingVec;
    }

    @Override
    public double getFriction() {
        return frictionInPercent;
    }

    @Override
    public String toString() {
        return "Base physical entity with " + pose + " width:" + width + " height:" + height;
    }
}


