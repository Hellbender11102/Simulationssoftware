package model.RobotTypes;

import model.Position;
import model.RobotBuilder;

import java.awt.*;

public class Robot3 extends BaseRobot {

    public Robot3(RobotBuilder builder) {
        super(builder);
    }
int i =0;
    @Override
    public void behavior() {
     driveToPosition(new Position(1,1),1,10);
    }

    @Override
    public Color getClassColor() {
        return Color.RED;
    }
}