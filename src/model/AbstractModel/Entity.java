package model.AbstractModel;

import model.Pose;
import model.Position;

import java.awt.*;
import java.util.Random;

/**
 * Entity interface declares all necessary functions
 */

public interface Entity {

    Color getClassColor();

    Pose getPose();

    void togglePause();

    boolean getPaused();

    boolean equals(Entity entity);

    Random getRandom();

    void setPrevPose();

    void setNextPose();

    void setToLatestPose();

    Color getColor();

    void updatePositionMemory();

    boolean hasPhysicalBody();

    boolean isPositionInEntity(Position position);

    Position getClosestPositionInEntity(Position position);

    double getWidth();

    double getHeight();

    double getArea();

    String toString();

}
