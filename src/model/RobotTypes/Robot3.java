package model.RobotTypes;

import model.Position;
import model.RobotModel.RobotBuilder;

public class Robot3 extends BaseRobot {

    public Robot3(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
        if (isPositionInRobotArea(new Position(100, 400))) {
            setEngines(0, 0);
        } else {
            driveToPosition(new Position(100, 400));
        }
    }

}