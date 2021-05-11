package model.RobotTypes;

import model.AbstractModel.RobotBuilder;

import java.awt.*;

public class Robot3 extends BaseRobot {

    public Robot3(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
        moveRandom(10,5,60);
    }

    @Override
    public Color getClassColor() {
        return Color.RED;
    }
}