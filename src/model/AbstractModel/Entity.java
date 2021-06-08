package model.AbstractModel;

import model.Pose;
import model.Position;

import java.awt.*;
import java.util.List;
import java.util.Random;

public interface Entity {

    abstract public Pose getPose();

    abstract public void togglePause();

    abstract public boolean getPaused();

    abstract public boolean equals(PhysicalEntity robot);

    abstract public Random getRandom();

    abstract public void setPrevPose();

    abstract public void setNextPose();

    abstract public void setToLatestPose();

    abstract public Color getColor();

    abstract public void updatePositionMemory();

    abstract public boolean isMovable();

    abstract public boolean draw(Graphics g);
}
