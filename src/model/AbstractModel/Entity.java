package model.AbstractModel;

import model.Pose;
import model.Position;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

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

    boolean isCollidable();

    boolean isPositionInEntity(Position position);

    Position getClosestPositionInEntity(Position position);

    double getWidth();

    double getHeight();

    double getArea();

    String toString();

}
