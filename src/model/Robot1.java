package model;

import model.Robot.BaseRobot;
import model.Robot.RobotBuilder;

public class Robot1 extends BaseRobot {

    public Robot1(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
        driveToPosition(new Position(100, 100));
        if (isPositionInRobotArea(new Position(100, 100)))
            toggleStop();
    }

}