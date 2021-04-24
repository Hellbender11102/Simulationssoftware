package model.RobotTypes;

import model.AbstractModel.EntityBuilder;

import java.awt.*;
import java.util.List;

public class Robot3 extends BaseRobot {

    public Robot3(EntityBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
    stayGroupedWithType(10, List.of(Robot3.class),1);
    }
    @Override
    public Color getClassColor() {
        return Color.RED;
    }
}