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
        slateElasticShock(this, physicalEntity);
    }

    private void slateElasticShock(PhysicalEntity pushing, PhysicalEntity target) {
        Vector2D normalized = new Vector2D(pushing.getPose().creatPositionByDecreasing(target.getPose())).normalize();

        Vector2D velocityPushing = new Vector2D(pushing.getPose().creatPositionByDecreasing(pushing.getPose().getPositionInDirection(pushing.trajectorySpeed())));
        Vector2D velocityTarget = new Vector2D(target.getPose().creatPositionByDecreasing(target.getPose().getPositionInDirection(target.trajectorySpeed())));

        Vector2D v1o = velocityPushing.subtract(normalized.multiplication(normalized.scalarProdukt(velocityPushing)));
        Vector2D v2p = normalized.multiplication(normalized.scalarProdukt(velocityTarget));
        Vector2D result = v1o.add(v2p);

        if (!result.containsNaN()) {
            if (pushing.getPose().getY() > target.getPose().getY() && result.getY() > 0) {
                target.getPose().incPosition(0, -result.getY());
            } else if (pushing.getPose().getY() < target.getPose().getY() && result.getY() > 0) {
                target.getPose().incPosition(0, result.getY());
            }
            if (pushing.getPose().getX() > target.getPose().getX() && result.getX() > 0) {
                target.getPose().incPosition(-result.getX(), 0);
            } else if (pushing.getPose().getX() < target.getPose().getX() && result.getX() > 0) {
                target.getPose().incPosition(result.getX(), 0);
            } else target.getPose().decPosition(result);
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

    public Position centerOfGroupWithEntities(List<Entity> group) {
        Position center = new Position(0, 0);
        for (Entity entity : group) {
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


