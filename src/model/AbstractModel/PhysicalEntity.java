package model.AbstractModel;

import model.Pose;
import model.Position;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public interface PhysicalEntity extends Entity {

     abstract Position centerOfGroupWithClasses(List<Class> classList);

    abstract public double trajectorySpeed();

    abstract public Color getClassColor();

    abstract public boolean inArenaBounds();

    abstract public void setInArenaBounds();

    abstract public LinkedList<PhysicalEntity> isCollidingWith();

    abstract public void collisionDetection();

    abstract public void recursiveCollision(PhysicalEntity physicalEntity);

    abstract public Position getClosestPositionInBody(Position position);

    abstract public int getTimeToSimulate();

}
