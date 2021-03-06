package model.abstractModel;

import model.Position;
import model.Vector2D;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * PhysicalEntity interface declares all necessary functions
 */
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

    void setNextPosition();

    double getFriction();

    boolean isMovable();
}
