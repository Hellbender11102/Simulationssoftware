package model.RobotTypes;

import model.Position;
import model.RobotModel.RobotBuilder;

public class Robot1 extends BaseRobot {

    public Robot1(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
        if (isPositionInRobotArea(new Position(400, 400))) {
            setEngines(0, 0);
        } else {
            driveToPosition(new Position(400, 400));
        }
    }

}