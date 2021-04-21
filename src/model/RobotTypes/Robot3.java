package model.RobotTypes;

import model.Position;
import model.RobotModel.RobotBuilder;
import model.RobotModel.RobotInterface;

public class Robot3 extends BaseRobot {

    public Robot3(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
    stayGroupedWithType(15,Robot3.class);
    }

}