package model.RobotModel;

import model.Pose;
import model.Position;

import java.awt.*;
import java.util.List;

public interface RobotInterface extends Runnable {

    abstract void behavior();

    abstract public Pose getPose();

    abstract public boolean isPositionInRobotArea(Position position);

    abstract public Color getColor();

    abstract public int getDiameters();

    abstract public int getRadius();

    abstract public double getEngineL();

    abstract public double getEngineR();

    abstract public double trajectorySpeed();

    abstract public void toggleStop();

    abstract public boolean getStop();

    abstract Position centerOfGroupWithClasses(List<Class> classList);

    abstract public boolean equals(RobotInterface robot);

    abstract public Color getClassColor();

    abstract public List<Pose> getPosesFromMemmory();

    abstract public void setPrevPose();

    abstract public void setNextPose();

    abstract public void resetToOrigin();
}