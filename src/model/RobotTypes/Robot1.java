package model.RobotTypes;

import model.Position;
import model.RobotModel.RobotBuilder;

public class Robot1 extends BaseRobot {
    boolean driveToCenter = true;

    public Robot1(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
        if (driveToCenter) {
            Position center = centerOfGroup(Robot1.class);
                 stayGroupedWithType(15, Robot1.class);
            if (isPositionInRobotArea(center))
                driveToCenter = false;
        } else {
            setEngineL(0);
            setEngineR(0);
        }
    }

}