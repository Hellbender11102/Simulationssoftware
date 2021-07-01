package model.AbstractModel;

import model.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

abstract public class BasePhysicalEntity extends BaseEntity implements PhysicalEntity {

    protected Vector2D movingVec = Vector2D.zeroVector();

    protected BasePhysicalEntity(Arena arena, Random random, double width, double height, Pose pose) {
        super(arena, random, width, height, pose);
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
        pose.addToPosition(movingVec);
        movingVec.setToZeroVector();
    }

    /**
     * The code will be run by threaded physical entities
     */
    @Override
    public void run() {
        while (!isPaused) {
            movingVec = pose.getVectorInDirection(getTrajectoryMagnitude(), pose.getRotation());
            collisionDetection();
            setNextPosition();
            updatePositionMemory();
            try {
                sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        double weight = getWeight(), weightPe = physicalEntity.getWeight();
        double velocity = getTrajectoryMagnitude(), velocityPe = physicalEntity.getTrajectoryMagnitude();

        Position closesToThis = physicalEntity.getClosestPositionInEntity(pose).creatPositionByDecreasing(physicalEntity.getPose());
        Position closesToPe = getClosestPositionInEntity(physicalEntity.getPose()).creatPositionByDecreasing(pose);

        Vector2D normalizedPe = new Vector2D(closesToThis).normalize();
        Vector2D normalized = new Vector2D(closesToPe).normalize();

        Vector2D velocityPushing = movingVec;

        Vector2D velocityPEntity = physicalEntity.getMovingVec();

        //orthogonal
        Vector2D v1o = velocityPushing.subtract(normalizedPe.multiplication(normalizedPe.scalarProduct(velocityPushing)));
        Vector2D v2o = velocityPEntity.subtract(normalized.multiplication(normalized.scalarProduct(velocityPEntity)));

        Vector2D v1p = normalized.multiplication(normalized.scalarProduct(velocityPushing));
        Vector2D v2p = normalizedPe.multiplication(normalizedPe.scalarProduct(velocityPEntity));


        //resulting vector from adding the
        Vector2D result = v2o.add(v1p);

        Vector2D resultPe = v1o.add(v2p);
        resultPe = resultPe.normalize().multiplication((2 * weightPe + (weight - weightPe)
                * movingVec.getLength()) / (weight + weightPe));
        result = result.normalize().multiplication((2 * weight + (weightPe - weight)
                * velocityPushing.getLength() / (weightPe + weight)));

        if (!resultPe.containsNaN() && physicalEntity.isMovable()) {
            physicalEntity.getMovingVec().set(physicalEntity.getMovingVec().add(resultPe));
        }
        if (!resultPe.containsNaN() && isMovable()) {
            movingVec.set(resultPe.reverse());
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
    public Vector2D getMovingVec() {
        return movingVec;
    }

}


