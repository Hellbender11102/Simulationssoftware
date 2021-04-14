package model;

public class Robot2 extends Robot {

    public Robot2(Robot robot) {
        super(robot);
    }
    public Robot2(RobotBuilder builder) {
        super(builder);
    }



    @Override
     void behavior() {
        driveToPosition(new Position(250, 250));
        if (isPositionInRobotArea(new Position(250, 250)))
            toggleStop();
    };

}