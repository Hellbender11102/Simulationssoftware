package model.RobotTypes;

import model.RobotBuilder;
import java.awt.*;

public class Robot1 extends BaseRobot {

    public Robot1(RobotBuilder builder) {
        super(builder);
    }

    int i = 0;

    @Override
    public void behavior() {
        turn(10,10,8,1);
    }

    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}