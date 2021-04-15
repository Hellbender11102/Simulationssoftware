package model.RobotTypes;

import model.Position;
import model.RobotModel.RobotBuilder;

public class Robot3 extends BaseRobot {

    public Robot3(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
        driveToPosition(new Position(400, 400));
        if (isPositionInRobotArea(new Position(400, 400)))
            toggleStop();
    }

}