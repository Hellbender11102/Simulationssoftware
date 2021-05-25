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
        stayGroupedWithAll(10, 8);
        Position groupCenter = centerOfGroupWithClasses(List.of(getClass()));
        //logger.logDouble(getId() + " Distant closest", distanceToClosestEntityOfClass(List.of(getClass())), 2);
        //  logger.logDouble(getId() + " center X", groupCenter.getXCoordinate(), 1);
        //logger.logDouble(getId() + " center Y", groupCenter.getYCoordinate(), 1);
        logger.log("one", 1 + "");
        logger.log("two", 2 + "");
        logger.log("three", 3 + "");
        logger.log("four", 4 + "");
        logger.log("five", 5 + "");
        logger.log("six", 6 + "");
    }

    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}