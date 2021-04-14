package model;

import java.awt.*;

public interface RobotInterface extends Runnable{

    abstract public Pose getPose();

    abstract public boolean isPositionInRobotArea(Position position);

    abstract public Color getColor();

    abstract public int getDiameters();

    abstract
    public int getRadius();

    abstract
    public double getEngineL();

    abstract
    public double getEngineR();
    abstract
    public double trajectorySpeed();

    abstract
    public void toggleStop();

    abstract
    public boolean getStop();

}