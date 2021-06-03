package model.RobotTypes;

import model.Position;
import model.RobotBuilder;

import java.awt.*;

public class Robot4 extends BaseRobot {

    public Robot4(RobotBuilder builder) {
        super(builder);
    }
    Position position=new Position(50,50);
    @Override
    public void behavior() {

        driveToPosition(position,2,8);
    }

    @Override
    public Color getClassColor() {
        return Color.BLUE;
    }
}