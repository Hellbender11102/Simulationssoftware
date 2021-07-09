package model.RobotTypes;

import model.Position;
import model.RobotBuilder;

import java.awt.*;

public class Robot3 extends BaseRobot {

    public Robot3(RobotBuilder builder) {
        super(builder);
    }
    @Override
    public void behavior() {
     driveToPosition(new Position(50,50),10,10);
    }

    @Override
    public Color getClassColor() {
        return Color.RED;
    }
}