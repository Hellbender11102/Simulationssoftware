package model.RobotTypes;

import model.Position;
import model.RobotModel.RobotBuilder;

public class Robot2 extends BaseRobot {

    public Robot2(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
        if (isPositionInRobotArea(new Position(250, 400))){
            setEngines(0,0);
        } else{
                    driveToPosition(new Position(250, 400));
        }
    }

}