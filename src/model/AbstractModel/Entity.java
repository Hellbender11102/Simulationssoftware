package model.AbstractModel;

import model.Pose;
import model.Position;

import java.util.List;

public interface Entity {

    abstract public Pose getPose();

    abstract public void togglePause();

    abstract public boolean getPaused();

    abstract Position centerOfGroupWithClasses(List<Class> classList);

    abstract public boolean equals(RobotInterface robot);

}
