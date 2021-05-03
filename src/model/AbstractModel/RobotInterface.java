package model.AbstractModel;

import model.Pose;
import model.Position;

import java.awt.*;
import java.util.List;

public interface RobotInterface extends Runnable, PhysicalEntity {

    abstract void behavior();

    abstract public boolean isPositionInRobotArea(Position position);

    abstract public int getDiameters();

    abstract public int getRadius();

    abstract public double getEngineL();

    abstract public double getEngineR();
}