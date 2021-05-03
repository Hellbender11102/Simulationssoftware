package model.AbstractModel;

import model.Pose;

import java.awt.*;
import java.util.List;

public interface PhysicalEntity extends Entity {

    abstract public double trajectorySpeed();

    abstract public Color getClassColor();

    abstract public void setPrevPose();

    abstract public void setNextPose();

    abstract public void setToLatestPose();

    abstract public Color getColor();

    abstract public Pose getPose();
}
