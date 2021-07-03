package model.AbstractModel;

import model.Pose;
import model.Position;
import model.Vector2D;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public interface PhysicalEntity extends Entity, Runnable {

    abstract Position centerOfGroupWithClasses(List<Class> classList);

    abstract public double getTrajectoryMagnitude();

    abstract public boolean inArenaBounds();

    abstract public void setInArenaBounds();

    abstract public LinkedList<PhysicalEntity> collidingWith();

    abstract public boolean collisionDetection();

    abstract public void collision(PhysicalEntity physicalEntity);

    abstract public boolean isMovable();

    abstract public double getWeight();

    abstract public AtomicReference<Vector2D> getMovingVec();
}
