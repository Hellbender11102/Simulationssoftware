package model.RobotTypes;

import model.AbstractModel.EntityBuilder;

import java.awt.*;
import java.util.List;

public class Robot1 extends BaseRobot {
    boolean driveToCenter = true;

    public Robot1(EntityBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
                 stayGroupedWithType(50, List.of(Robot1.class),0.5);
    }
    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}