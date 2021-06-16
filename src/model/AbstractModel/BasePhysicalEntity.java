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
        if (getPose().getXCoordinate() < width / 2)
            return false;
        else if (getPose().getXCoordinate() > arena.getWidth() - width / 2)
            return false;
        if (getPose().getYCoordinate() < height / 2)
            return false;
        else return !(getPose().getYCoordinate() > arena.getHeight() - height / 2);
    }

    /**
     * Checks if robots are in the arena bounds
     */
    @Override
    public void setInArenaBounds() {
        if (pose.getXCoordinate() < width / 2)
            pose.setXCoordinate(width / 2);
        else if (pose.getXCoordinate() > arena.getWidth() - width / 2)
            pose.setXCoordinate(arena.getWidth() - width / 2);
        if (pose.getYCoordinate() < height / 2)
            pose.setYCoordinate(height / 2);
        else if (pose.getYCoordinate() > arena.getHeight() - height / 2)
            pose.setYCoordinate(arena.getHeight() - height / 2);
    }

    //TODO
    @Override
    public void collisionDetection() {
        for (PhysicalEntity physicalEntity : isCollidingWith()) {
            if (!physicalEntity.isMovable() && isMovable())
                notMovableCollision(physicalEntity, this);
            else if (physicalEntity.isMovable() && !isMovable())
                notMovableCollision(this, physicalEntity);
            else if (physicalEntity.isMovable() && isMovable())
                collision(physicalEntity);
            if (!physicalEntity.inArenaBounds() && !arena.isTorus) {
                physicalEntity.setInArenaBounds();
            } else if (arena.isTorus) {
                arena.setEntityInTorusArena(physicalEntity);
            }
        }
        if (!inArenaBounds() && !arena.isTorus) {
            setInArenaBounds();
        } else if (arena.isTorus) {
            arena.setEntityInTorusArena(this);
        }
    }

    public void collision(PhysicalEntity physicalEntity) {
        Position vector = pose.clone();
        if (pose.getXCoordinate() < physicalEntity.getPose().getXCoordinate()) {
            vector.incPosition(trajectorySpeed(),0);
        } else {
          vector.incPosition(-trajectorySpeed(),0);
        }
        if (pose.getYCoordinate() < physicalEntity.getPose().getYCoordinate()) {
        vector.incPosition(0,trajectorySpeed());
        } else {
           vector.incPosition(0,-trajectorySpeed());
        }
        bump(this,physicalEntity,vector);
    }

    /**
     * @param bumping                 Robot that bumps
     * @param getsBumped              Robot that gets bumped
     * @param positionInBumpDirection Position in which the bump directs
     */
    private void bump(PhysicalEntity bumping, PhysicalEntity getsBumped, Position positionInBumpDirection) {
        Position vector = bumping.getPose().creatPositionByDecreasing(positionInBumpDirection);
        while (isPositionInEntity(bumping.getClosestPositionInEntity(getsBumped.getPose()))) {
            getsBumped.getPose().decPosition(vector);
            bumping.getPose().incPosition(vector);
        }
    }

    /**
     * @param notMovable
     * @param other
     */
    private void notMovableCollision(PhysicalEntity notMovable, PhysicalEntity other) {
        double notMovableX = notMovable.getPose().getXCoordinate(), notMovableY = notMovable.getPose().getYCoordinate(),
                movableX = other.getPose().getXCoordinate(), movableY = other.getPose().getYCoordinate();
        double notMovableWidth = notMovable.getWidth(), notMovableHeight = notMovable.getHeight(),
                movableWidth = other.getWidth(), movableHeight = other.getHeight();
        boolean leftOrRight = (movableX > notMovableX + notMovableWidth / 2 || movableX < notMovableX - notMovableWidth / 2);
        boolean aboveOrBelow = (movableY > notMovableY + notMovableHeight / 2 || movableY < notMovableY - notMovableHeight / 2);
        if (notMovableX <= movableX && !aboveOrBelow)
            other.getPose().setXCoordinate(notMovableX + notMovableWidth / 2 + movableWidth / 2);
        else if (notMovableX > movableX && !aboveOrBelow)
            other.getPose().setXCoordinate(notMovableX - notMovableWidth / 2 - movableWidth / 2);
        if (notMovableY <= movableY && !leftOrRight)
            other.getPose().setYCoordinate(notMovableY + notMovableHeight / 2 + movableHeight / 2);
        else if (notMovableY > movableY && !leftOrRight)
            other.getPose().setYCoordinate(notMovableY - notMovableHeight / 2 - movableHeight / 2);
    }

    public LinkedList<PhysicalEntity> isCollidingWith() {
        LinkedList<PhysicalEntity> physicalEntities = new LinkedList<>();
        for (PhysicalEntity physicalEntity : arena.getPhysicalEntityList()) {
            if (isPositionInEntity(physicalEntity.getClosestPositionInEntity(pose)) && !equals(physicalEntity)) {
                physicalEntities.add(physicalEntity);
            }
        }
        return physicalEntities;
    }

    public Position centerOfGroupWithEntities(List<Entity> group) {
        Position center = new Position(0, 0);
        for (Entity entity : group) {
            center.incPosition(entity.getPose());
        }
        center.setXCoordinate(center.getXCoordinate() / group.size());
        center.setYCoordinate(center.getYCoordinate() / group.size());
        return center;
    }

    public Position centerOfGroupWithClasses(List<Class> classList) {
        LinkedList<Entity> group = entityGroupByClasses(classList);
        return centerOfGroupWithEntities(group);
    }

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
    public boolean hasAnBody() {
        return true;
    }

    @Override
    public boolean isMovable() {
        return true;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }
}


