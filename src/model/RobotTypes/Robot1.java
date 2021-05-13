package model.RobotTypes;

import model.RobotBuilder;

import java.awt.*;
import java.util.List;

public class Robot1 extends BaseRobot {

    public Robot1(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
        stayGroupedWithAll(10, 8);
        synchronized (this) {
            logger.logDouble(getId() + " Distant closest", distanceToClosestEntityOfClass(List.of(getClass())), 3);
            logger.log(getId() + " center X", centerOfGroupWithClasses(List.of(getClass())).getXCoordinate() + "");
            logger.log(getId() + " center Y", centerOfGroupWithClasses(List.of(getClass())).getYCoordinate() + "");
        }
    }

    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}