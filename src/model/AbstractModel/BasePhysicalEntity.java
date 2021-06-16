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
        }
        if (!inArenaBounds() && !arena.isTorus) {
            setInArenaBounds();
        } else if (!inArenaBounds() && arena.isTorus) {
            arena.setEntityInTorusArena(this);
        }
    }

    /**
     * @param physicalEntity
     */
    public void collision(PhysicalEntity physicalEntity) {
        Position inTrajectoryPath = pose.getPositionInDirection(trajectorySpeed(), pose.getRotation());
        double direction = pose.calcAngleForPosition(physicalEntity.getPose());
        inTrajectoryPath = pose.creatPositionByDecreasing(inTrajectoryPath);
        if (!Double.isNaN(direction)) {
            direction = (Math.PI + direction) % Math.PI * 2;
            Position inOppositeDirections = pose.getPositionInDirection(trajectorySpeed() / 2, direction);
            inTrajectoryPath.decPosition(inOppositeDirections);
        }

        physicalEntity.getPose().decPosition(inTrajectoryPath);
        pose.incPosition(inTrajectoryPath);
    }

    private void slateElasticShock(PhysicalEntity entity1, PhysicalEntity entity2) {
        double x=entity1.getPose().getXCoordinate(),y=entity1.getPose().getYCoordinate();
        Position v1 = entity1.getPose().creatPositionByDecreasing(entity1.getPose().getPositionInDirection(entity1.trajectorySpeed()));
        Position v2 = entity1.getPose().creatPositionByDecreasing(entity2.getPose().getPositionInDirection(entity2.trajectorySpeed()));
   /*     Position orientationOneNormalized = new Position(1/Math.sqrt(x*x+y*y *entity1.getPose().creatPositionByDecreasing().getXCoordinate());
     v1o = v1 - p * (p*v1);
        v2p = p * (p*v2);

        vNew = v1o + v2p;
*/
        double xValueForNormalizedVektor;
        double yValueForNormalizedVektor;
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


