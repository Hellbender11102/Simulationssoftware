package model.RobotTypes;

import model.Position;
import model.RobotBuilder;

import java.awt.*;

public class Robot4 extends BaseRobot {

    public Robot4(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
        Position position=new Position(50,50);
        if (rotateToAngle(pose.calcAngleForPosition(position),Math.toRadians(2),4,2))
            setEngines(8,8);
        else   setEngines(1,2);
    }

    @Override
    public Color getClassColor() {
        return Color.RED;
    }
}