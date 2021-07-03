package model.AbstractModel;

import model.*;

import java.awt.geom.Line2D;
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
    protected AtomicReference<Vector2D> movingVec = new AtomicReference<Vector2D>();

    protected BasePhysicalEntity(Arena arena, Random random, double width, double height, Pose pose, int ticsPerSimulatedSecond) {
        super(arena, random, width, height, pose);
        movingVec.set(Vector2D.zeroVector());
        this.ticsPerSimulatedSecond = ticsPerSimulatedSecond;
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
    public void setNextPosition() {
        pose.addToPosition(movingVec.get());
        movingVec.get().setToZeroVector();
    }

    /**
     * The code will be run by threaded physical entities
     */
    @Override
    public void run() {
        while (!isPaused) {
            movingVec.getAndSet(Vector2D.zeroVector());
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
        for (PhysicalEntity physicalEntity : collidingWith()) {
            returnValue = true;
            collision(physicalEntity);
        }
        if (!inArenaBounds() && !arena.isTorus) {
            setInArenaBounds();
        } else if (!inArenaBounds() && arena.isTorus) {
            arena.setEntityInTorusArena(this);
        }
        return returnValue;
    }


    /**
     * Calculates the collision with an elastic shock
     *
     * @param physicalEntity PhysicalEntity
     *                       source =
     *                       https://www.physik.tu-darmstadt.de/media/fachbereich_physik/phys_studium/phys_studium_bachelor/phys_studium_bsc_praktika/phys_studium_bsc_praktika_gp/phys_studium_bsc_praktika_gp_mechanik/m4/m4bilder/m4_neuSS15.pdf
     */
    public void collision(PhysicalEntity physicalEntity) {
        double u1Angle = pose.getAngleFromPosition(physicalEntity.getPose());
        double u2Angle = physicalEntity.getPose().getAngleToPosition(pose);

        // if squared entity use other position to calculate the pushing angle
        if (Wall.class.isAssignableFrom(physicalEntity.getClass()) || Box.class.isAssignableFrom(physicalEntity.getClass())) {
             u2Angle = physicalEntity.getClosestPositionInEntity(pose).getAngleToPosition(pose);
             u1Angle = pose.getAngleFromPosition(physicalEntity.getClosestPositionInEntity(pose));
        }
        if (Wall.class.isAssignableFrom(getClass()) || Box.class.isAssignableFrom(getClass())) {
            u2Angle = physicalEntity.getPose().getAngleToPosition(getClosestPositionInEntity(physicalEntity.getPose()));
            u1Angle = getClosestPositionInEntity(physicalEntity.getPose()).getAngleFromPosition(physicalEntity.getPose());
        }
        Vector2D moving1 = movingVec.getAcquire(), moving2 = physicalEntity.getMovingVec().getAcquire();
        double m1 = getWeight(), m2 = physicalEntity.getWeight();
        double v1 = moving1.getLength(),
                v2 = moving2.getLength();
        double u2New = ((2 * m1) / (m1 + m2)) * v1 * Math.cos(u2Angle);
        double u1New = ((2 * m2) / (m1 + m2)) * v2 * Math.cos(u1Angle);

        Vector2D bIncrease = Vector2D.creatCartesian(u2New, u2Angle),
                aIncrease = Vector2D.creatCartesian(u1New, u1Angle);

        Vector2D resulting = aIncrease.subtract(moving1);
         resulting = aIncrease;
        Vector2D resultingPe = bIncrease.subtract(moving2);
         resultingPe = bIncrease;

        if (isMovable()) {
            movingVec.setRelease(resulting);
        }
        if (physicalEntity.isMovable()) {
            physicalEntity.getMovingVec().setRelease(resultingPe);
        }

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
        if (pose.getX() < width / 2)
            pose.setX(width / 2);
        else if (pose.getX() > arena.getWidth() - width / 2)
            pose.setX(arena.getWidth() - width / 2);
        if (pose.getY() < height / 2)
            pose.setY(height / 2);
        else if (pose.getY() > arena.getHeight() - height / 2)
            pose.setY(arena.getHeight() - height / 2);
    }
    //TODO FOR TORUS

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
        for (Entity entity : arena.getPhysicalEntityList()) {
            for (Class c : classList) {
                if (c.isAssignableFrom(entity.getClass()) || entity.getClass().isInstance(c)) {
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

    @Override
    public boolean isMovable() {
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
    public AtomicReference<Vector2D> getMovingVec() {
        return movingVec;
    }

}


