package model.abstractModel;

import java.util.List;
/**
 * RobotInterface interface declares all necessary functions
 */
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

    double getAccelerationInPercent();

}