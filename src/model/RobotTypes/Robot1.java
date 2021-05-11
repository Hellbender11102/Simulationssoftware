package model.RobotTypes;

import model.AbstractModel.RobotBuilder;

import java.awt.*;
import java.util.List;

public class Robot1 extends BaseRobot {

    public Robot1(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
        moveRandom(10,5,60);

    }

    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}