package model.AbstractModel;

import model.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

abstract public class BasePhysicalEntity extends BaseEntity implements PhysicalEntity {

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
        double angleToPhysicalEntity = pose.getAngleForPosition(physicalEntity.getPose());
        double angleToCurrent = physicalEntity.getPose().getAngleForPosition(pose);

       double epsilon =1;
        double u2 = ((1+epsilon)*getWeight())
                /(getWeight()+physicalEntity.getWeight())
                *trajectorySpeed()*Math.cos(angleToPhysicalEntity);
        physicalEntity.getPose().set(physicalEntity.getPose().getPoseInDirection(u2,angleToPhysicalEntity));
    }


    /**
     * Sets the colliding entity next to the Body
     *
     * @param notMovable PhysicalEntity
     * @param other      PhysicalEntity
     */
    private void notMovableCollision(PhysicalEntity notMovable, PhysicalEntity other) {
        double notMovableX = notMovable.getPose().getX(), notMovableY = notMovable.getPose().getY(),
                movableX = other.getPose().getX(), movableY = other.getPose().getY();
        double notMovableWidth = notMovable.getWidth(), notMovableHeight = notMovable.getHeight(),
                movableWidth = other.getWidth(), movableHeight = other.getHeight();
        boolean leftOrRight = (movableX > notMovableX + notMovableWidth / 2 || movableX < notMovableX - notMovableWidth / 2);
        boolean aboveOrBelow = (movableY > notMovableY + notMovableHeight / 2 || movableY < notMovableY - notMovableHeight / 2);
        if (notMovableX <= movableX && !aboveOrBelow)
            other.getPose().setX(notMovableX + notMovableWidth / 2 + movableWidth / 2);
        else if (notMovableX > movableX && !aboveOrBelow)
            other.getPose().setX(notMovableX - notMovableWidth / 2 - movableWidth / 2);
        if (notMovableY <= movableY && !leftOrRight)
            other.getPose().setY(notMovableY + notMovableHeight / 2 + movableHeight / 2);
        else if (notMovableY > movableY && !leftOrRight)
            other.getPose().setY(notMovableY - notMovableHeight / 2 - movableHeight / 2);
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

    /**
     * The code will be run by threaded physical entities
     */
    @Override
    public void run() {
        while (!isPaused) {
            collisionDetection();
            updatePositionMemory();
            try {
                sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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


}


