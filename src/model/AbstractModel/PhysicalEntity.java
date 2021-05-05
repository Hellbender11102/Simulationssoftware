package model.AbstractModel;

import model.Pose;
import model.Position;

import java.awt.*;
import java.util.LinkedList;

public interface PhysicalEntity extends Entity {

    abstract public double trajectorySpeed();

    abstract public Color getClassColor();

    abstract public void setPrevPose();

    abstract public void setNextPose();

    abstract public void setToLatestPose();

    abstract public Color getColor();

    abstract public Pose getPose();

    abstract public boolean inArenaBounds();

    abstract public void setInArenaBounds();

    abstract public boolean isPositionInEntity(Position position);

    abstract public LinkedList<PhysicalEntity> isCollidingWith();

    abstract public void updatePositionMemory();




    abstract public void collisionDetection();

    abstract public void recursiveCollision(PhysicalEntity physicalEntity);

    abstract public Position getClosestPositionInBody(Position position);

    abstract public boolean isMovable();
}
