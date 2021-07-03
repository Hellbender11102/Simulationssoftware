package model.AbstractModel;

import java.util.List;

public interface RobotInterface extends PhysicalEntity {

    void behavior();

    double getDiameters();

    double getRadius();

    double getEngineL();

    double getEngineR();

    boolean getSignal();

    double distanceToClosestEntityOfClass(List<Class> classList);

    int getTimeToSimulate();

    double cmPerSecond();

}