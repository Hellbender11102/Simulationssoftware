package model.RobotTypes;

import model.Position;
import model.RobotModel.RobotBuilder;

public class Robot2 extends BaseRobot {

    public Robot2(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
     stayGroupedWithType(15,Robot2.class);
    }

}