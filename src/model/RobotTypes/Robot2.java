package model.RobotTypes;

import model.Position;
import model.RobotBuilder;

import java.awt.*;

public class Robot2 extends BaseRobot {

    public Robot2(RobotBuilder builder) {
        super(builder);
    }

    int toggle = 0;

    @Override
    public void behavior() {

        driveToPosition(new Position(1,1),1,8);
    }

    @Override
    public Color getClassColor() {
        return Color.YELLOW;
    }

}