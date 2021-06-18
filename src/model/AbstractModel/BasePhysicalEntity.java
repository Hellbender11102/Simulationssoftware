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

    @Override
    public boolean collisionDetection() {
        boolean returnValue = false;
        for (PhysicalEntity physicalEntity : isCollidingWith()) {
            returnValue = true;
            if (!physicalEntity.isMovable() && isMovable())
                notMovableCollision(physicalEntity, this);
            else if (physicalEntity.isMovable() && !isMovable())
                notMovableCollision(this, physicalEntity);
            else if (physicalEntity.isMovable() && isMovable())
                recursiveCollision(physicalEntity);
            //collision(physicalEntity);
        }
        if (!inArenaBounds() && !arena.isTorus) {
            setInArenaBounds();
        } else if (!inArenaBounds() && arena.isTorus) {
            arena.setEntityInTorusArena(this);
        }
        return returnValue;
    }

    public void recursiveCollision(PhysicalEntity physicalEntity) {
        if (!physicalEntity.inArenaBounds()) {
            setInArenaBounds();
        }
        if (physicalEntity.isMovable()) {
            //r2 gets bumped
            if (physicalEntity.isPositionInEntity(pose.getPositionInDirection(getClosestPositionInEntity(physicalEntity.getPose()).euclideanDistance(pose)))) {
                bump(this, physicalEntity, pose.getPositionInDirection(trajectorySpeed()));
            } else if (isPositionInEntity(physicalEntity.getPose().getPositionInDirection(physicalEntity.getClosestPositionInEntity(pose).euclideanDistance(physicalEntity.getPose())))) {   //this gets pumped
                bump(physicalEntity, this, physicalEntity.getPose().getPositionInDirection(physicalEntity.trajectorySpeed()));
            } else {
                //both are bumping cause no one drives directly in each other
                if (pose.getX() < physicalEntity.getPose().getX()) {
                    bump(this, physicalEntity, new Position(pose.getX() + trajectorySpeed(), pose.getY()));
                    bump(physicalEntity, this, new Position(physicalEntity.getPose().getX() - physicalEntity.trajectorySpeed(), physicalEntity.getPose().getY()));
                } else {
                    bump(this, physicalEntity, new Position(pose.getX() - trajectorySpeed(), pose.getY()));
                    bump(physicalEntity, this, new Position(physicalEntity.getPose().getX() + physicalEntity.trajectorySpeed(), physicalEntity.getPose().getY()));
                }
                if (pose.getY() < physicalEntity.getPose().getY()) {
                    bump(this, physicalEntity, new Position(pose.getX(), pose.getY() + trajectorySpeed()));
                    bump(physicalEntity, this, new Position(physicalEntity.getPose().getX(), physicalEntity.getPose().getY() - physicalEntity.trajectorySpeed()));
                } else {
                    bump(this, physicalEntity, new Position(pose.getX(), pose.getY() - trajectorySpeed()));
                    bump(physicalEntity, this, new Position(physicalEntity.getPose().getX(), physicalEntity.getPose().getY() + physicalEntity.trajectorySpeed()));
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

        if (getPose().getX() < width / 2)
            bumping.getPose().incPosition(vector.getX(), 0);
        else if (getPose().getX() > arena.getWidth() - width / 2)
            bumping.getPose().incPosition(vector.getX(), 0);
        if (getPose().getY() < height / 2)
            bumping.getPose().incPosition(0, vector.getY());
        else if (getPose().getY() > arena.getHeight() - height / 2)
            bumping.getPose().incPosition(0, vector.getY());
    }


    /**
     * @param physicalEntity
     */
    public void collision(PhysicalEntity physicalEntity) {
        Vector2D normalized = new Vector2D(getPose().creatPositionByDecreasing(physicalEntity.getPose())).normalize();
        double speedPushing = trajectorySpeed();
        double speedPEntity = physicalEntity.trajectorySpeed();
        if (speedPushing == 0 && speedPEntity == 0) {
            speedPushing = 0.1;
            speedPEntity = 0.1;
        }
        Vector2D velocityPushing = new Vector2D(getPose().creatPositionByDecreasing(getPose().getPositionInDirection(speedPushing)));
        Vector2D velocityPEntity = new Vector2D(physicalEntity.getPose().creatPositionByDecreasing(physicalEntity.getPose().getPositionInDirection(speedPEntity)));

        Vector2D v1o = velocityPushing.subtract(normalized.multiplication(normalized.scalarProdukt(velocityPushing)));
        Vector2D v2p = normalized.multiplication(normalized.scalarProdukt(velocityPEntity));

        Vector2D result = v1o.add(v2p);

        if (!result.containsNaN()) {
            if (getPose().getY() > physicalEntity.getPose().getY() && result.getY() > 0) {
                physicalEntity.getPose().incPosition(0, -result.getY());
                getPose().incPosition(0, result.getY());
            } else if (getPose().getY() < physicalEntity.getPose().getY() && result.getY() > 0) {
                physicalEntity.getPose().incPosition(0, result.getY());
                getPose().incPosition(0, -result.getY());
            }
            if (getPose().getX() > physicalEntity.getPose().getX() && result.getX() > 0) {
                physicalEntity.getPose().incPosition(-result.getX(), 0);
                getPose().incPosition(result.getX(), 0);
            } else if (getPose().getX() < physicalEntity.getPose().getX() && result.getX() > 0) {
                physicalEntity.getPose().incPosition(result.getX(), 0);
                getPose().incPosition(-result.getX(), 0);
            } else physicalEntity.getPose().decPosition(result);
        }
    }


    /**
     * @param notMovable
     * @param other
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

    public LinkedList<PhysicalEntity> isCollidingWith() {
        LinkedList<PhysicalEntity> physicalEntities = new LinkedList<>();
        for (PhysicalEntity physicalEntity : arena.getPhysicalEntityList()) {
            if (isPositionInEntity(physicalEntity.getClosestPositionInEntity(pose)) && !equals(physicalEntity)) {
                physicalEntities.add(physicalEntity);
            }
        }
        return physicalEntities;
    }

    //TODO FOR TORUS
    public Position centerOfGroupWithEntities(List<Entity> group) {
        Position center = new Position(0, 0);
        for (Entity entity : group) {
            if (arena.isTorus)
                 center.incPosition(arena.getClosestPositionInTorus(center,entity.getPose()));
            else
                center.incPosition(entity.getPose());
        }
        center.setX(center.getX() / group.size());
        center.setY(center.getY() / group.size());
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

}


