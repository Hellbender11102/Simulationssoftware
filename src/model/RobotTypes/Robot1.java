package model.RobotTypes;

import model.Position;
import model.RobotBuilder;

import java.awt.*;
import java.util.List;

public class Robot1 extends BaseRobot {

    public Robot1(RobotBuilder builder) {
        super(builder);
    }

    int i = 0;
    private boolean loggingCenter = false;

    @Override
    public void behavior() {
        stayGroupedWithAll(5, 8);
        Position groupCenter = centerOfGroupWithClasses(List.of(getClass()));
        //logger.logDouble(getId() + " Distant closest", distanceToClosestEntityOfClass(List.of(getClass())), 2);
        //logger.logDouble(getId() + " center X", groupCenter.getXCoordinate(), 1);
        //logger.logDouble(getId() + " center Y", groupCenter.getYCoordinate(), 1);
    }

    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}