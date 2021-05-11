package model.AbstractModel;

import model.Pose;
import model.Position;

import java.util.List;
import java.util.Random;

public interface Entity {

    abstract public Pose getPose();

    abstract public void togglePause();

    abstract public boolean getPaused();

    abstract Position centerOfGroupWithClasses(List<Class> classList);

    abstract public boolean equals(PhysicalEntity robot);

    abstract public Random getRandom();
}
