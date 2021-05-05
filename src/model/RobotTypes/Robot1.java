package model.RobotTypes;

import model.AbstractModel.RobotBuilder;

import java.awt.*;
import java.util.List;

public class Robot1 extends BaseRobot {
    boolean driveToCenter = true;

    public Robot1(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
                 stayGroupedWithType(50, List.of(Robot1.class),8);
    }
    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}