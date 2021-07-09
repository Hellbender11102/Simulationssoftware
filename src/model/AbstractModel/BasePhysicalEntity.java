package model.AbstractModel;

import model.*;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

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
    protected final double frictionInPercent;
    /**
     * Describes how elastic the bump is
     * bumpParam = 1 for the elastic bump
     * bumpParam = 0 for the completely inelastic angle
     * The simulation is set up for the elastic bump
     */
    private final int bumpParam = 1;
    protected AtomicReference<Vector2D> movingVec = new AtomicReference<>();


    protected BasePhysicalEntity(Arena arena, Random random, double width, double height, Pose pose, int ticsPerSimulatedSecond) {
        super(arena, random, width, height, pose);
        movingVec.set(Vector2D.zeroVector());
        this.ticsPerSimulatedSecond = ticsPerSimulatedSecond;
        frictionInPercent = 0.01;
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
        pose.setRotation(movingVec.get().angle());
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
            updatePositionMemory();
            try {
                sleep(1000 / ticsPerSimulatedSecond);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

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
        if (arena.isTorus) {
            position = arena.getClosestPositionInTorus(physicalEntity.getPose(), pose);
            positionPe = arena.getClosestPositionInTorus(pose, physicalEntity.getPose());
        }

        double u1Angle = position.getAngleToPosition(positionPe);
        double u2Angle = positionPe.getAngleToPosition(position);

        Vector2D moving1 = movingVec.getAcquire(), moving2 = physicalEntity.getMovingVec().getAcquire();

        double m1 = getWeight(), m2 = physicalEntity.getWeight();
        double v1 = moving1.getLength(), v2 = moving2.getLength();

        if (Wall.class.isAssignableFrom(physicalEntity.getClass()) || Box.class.isAssignableFrom(physicalEntity.getClass())) {
            u1Angle = position.getAngleToPosition(physicalEntity.getClosestPositionInEntity(position));
        }
        if (Wall.class.isAssignableFrom(getClass()) || Box.class.isAssignableFrom(getClass())) {
            u2Angle = positionPe.getAngleToPosition(getClosestPositionInEntity(positionPe));
        }

        double v1x = calcX(v1, v2, m1, m2, moving1.angle(), moving2.angle(), u1Angle);
        double v1y = calcY(v1, v2, m1, m2, moving1.angle(), moving2.angle(), u1Angle);

        double v2x = calcX(v2, v1, m2, m1, moving2.angle(), moving1.angle(), u2Angle);
        double v2y = calcY(v2, v1, m2, m1, moving2.angle(), moving1.angle(), u2Angle);

        Vector2D resultingPe = new Vector2D(v2x, v2y),
                resulting = new Vector2D(v1x, v1y);


        //ensures correct distance ist kept
        if (position.getEuclideanDistance(physicalEntity.getPose()) - (resultingPe.getLength() + resulting.getLength()) <
                position.getEuclideanDistance(getClosestPositionInEntity(positionPe)) +
                        positionPe.getEuclideanDistance(physicalEntity.getClosestPositionInEntity(position))) {

            double distance = position.getEuclideanDistance(getClosestPositionInEntity(positionPe)) +
                    positionPe.getEuclideanDistance(physicalEntity.getClosestPositionInEntity(position)) - position.getEuclideanDistance(positionPe);
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

    private double calcX(double v1, double v2, double m1, double m2, double movingAngle1, double movingAngle2, double contactAngle) {
        return ((v1 * Math.cos(movingAngle1 - contactAngle) * (m1 - m2) + 2 * m2 * v2 * Math.cos(movingAngle2 - contactAngle)) /
                (m1 + m2))
                * Math.cos(contactAngle) + v1 * Math.sin(movingAngle1 - contactAngle) * Math.cos(contactAngle + (Math.PI / 2));
    }

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
            if (arena.isTorus)
                center.addToPosition(arena.getClosestPositionInTorus(center, entity.getPose()));
            else
                center.addToPosition(entity.getPose());
        }
        center.setX(center.getX() / group.size());
        center.setY(center.getY() / group.size());
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
                if (Entity.class == c) {
                    entityInGroup.add(entity);
                }
            }
        }
        return entityInGroup;
    }


    @Override
    public boolean isCollidable() {
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


