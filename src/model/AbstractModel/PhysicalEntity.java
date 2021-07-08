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

    Position centerOfGroupWithClasses(List<Class> classList);

    double getTrajectoryMagnitude();

    boolean inArenaBounds();

    void setInArenaBounds();

    LinkedList<PhysicalEntity> collidingWith();

    boolean collisionDetection();

    void collision(PhysicalEntity physicalEntity);

    double getWeight();

    AtomicReference<Vector2D> getMovingVec();

    void alterMovingVector();

    double getFriction();

    boolean isMovable();
}
