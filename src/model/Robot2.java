package model;

import model.Robot.BaseRobot;
import model.Robot.RobotBuilder;

public class Robot2 extends BaseRobot {

    public Robot2(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
        driveToPosition(new Position(250, 250));
        if (isPositionInRobotArea(new Position(250, 250)))
            toggleStop();
    }

}