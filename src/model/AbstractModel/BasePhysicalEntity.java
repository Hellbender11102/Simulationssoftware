package model.AbstractModel;

import model.Arena;
import model.Position;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

abstract public class BasePhysicalEntity extends BaseEntity implements PhysicalEntity {

    protected BasePhysicalEntity(Arena arena, Random random, double width, double height) {
        super(arena,random,width,height);
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

    @Override
    public void collisionDetection() {
        if (!inArenaBounds()) {
            setInArenaBounds();
        }
        for (PhysicalEntity physicalEntity : isCollidingWith()) {
            recursiveCollision(physicalEntity);
        }
    }

    public void recursiveCollision(PhysicalEntity physicalEntity) {
        if (!physicalEntity.inArenaBounds()) {
            setInArenaBounds();
        }
        if (physicalEntity.hasAnBody()) {
            //r2 gets bumped
            if (physicalEntity.isPositionInEntity(pose.getPositionInDirection(getClosestPositionInBody(physicalEntity.getPose()).euclideanDistance(pose)))) {
                bump(this, physicalEntity, pose.getPositionInDirection(trajectorySpeed()));
            } else if (isPositionInEntity(physicalEntity.getPose().getPositionInDirection(physicalEntity.getClosestPositionInBody(pose).euclideanDistance(physicalEntity.getPose())))) {   //this gets pumped
                bump(physicalEntity, this, physicalEntity.getPose().getPositionInDirection(physicalEntity.trajectorySpeed()));
            } else {
                //both are bumping cause no one drives directly in each other
                if (pose.getXCoordinate() < physicalEntity.getPose().getXCoordinate()) {
                    bump(this, physicalEntity, new Position(pose.getXCoordinate() + trajectorySpeed(), pose.getYCoordinate()));
                    bump(physicalEntity, this, new Position(physicalEntity.getPose().getXCoordinate() - physicalEntity.trajectorySpeed(), physicalEntity.getPose().getYCoordinate()));
                } else {
                    bump(this, physicalEntity, new Position(pose.getXCoordinate() - trajectorySpeed(), pose.getYCoordinate()));
                    bump(physicalEntity, this, new Position(physicalEntity.getPose().getXCoordinate() + physicalEntity.trajectorySpeed(), physicalEntity.getPose().getYCoordinate()));
                }
                if (pose.getYCoordinate() < physicalEntity.getPose().getYCoordinate()) {
                    bump(this, physicalEntity, new Position(pose.getXCoordinate(), pose.getYCoordinate() + trajectorySpeed()));
                    bump(physicalEntity, this, new Position(physicalEntity.getPose().getXCoordinate(), physicalEntity.getPose().getYCoordinate() - physicalEntity.trajectorySpeed()));
                } else {
                    bump(this, physicalEntity, new Position(pose.getXCoordinate(), pose.getYCoordinate() - trajectorySpeed()));
                    bump(physicalEntity, this, new Position(physicalEntity.getPose().getXCoordinate(), physicalEntity.getPose().getYCoordinate() + physicalEntity.trajectorySpeed()));
                }
            }
        }
    }

    /**
     * @param bumping                 Robot that bumps
     * @param getsBumped              Robot that gets bumped
     * @param positionInBumpDirection Position in which the bump directs
     */
    private void bump(PhysicalEntity bumping, PhysicalEntity getsBumped, Position positionInBumpDirection) {
        Position vector = bumping.getPose().creatPositionByDecreasing(positionInBumpDirection);
        getsBumped.getPose().decPosition(vector);

        if (getPose().getXCoordinate() < width / 2)
            bumping.getPose().incPosition(vector.getXCoordinate(), 0);
        else if (getPose().getXCoordinate() > arena.getWidth() - width / 2)
            bumping.getPose().incPosition(vector.getXCoordinate(), 0);
        if (getPose().getYCoordinate() < height / 2)
            bumping.getPose().incPosition(0, vector.getYCoordinate());
        else if (getPose().getYCoordinate() > arena.getHeight() - height / 2)
            bumping.getPose().incPosition(0, vector.getYCoordinate());
    }

    public LinkedList<PhysicalEntity> isCollidingWith() {
        LinkedList<PhysicalEntity> physicalEntities = new LinkedList<>();
        for (PhysicalEntity physicalEntity : arena.getPhysicalEntityList()) {
            if (isPositionInEntity(physicalEntity.getClosestPositionInBody(pose)))
                physicalEntities.add(physicalEntity);
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
}


