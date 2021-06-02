package model.RobotTypes;

import model.RobotBuilder;

import java.awt.*;

public class Robot2 extends BaseRobot {

    public Robot2(RobotBuilder builder) {
        super(builder);
    }

    int toggle = 0;

    @Override
    public void behavior() {

        if (toggle == 0 && turn(-45)) {
            System.out.println("case1");
            toggle = 1;
        } else if (toggle == 1 && turn(45)) {
            System.out.println("case2");
            toggle = 0;
        }
    }

    @Override
    public Color getClassColor() {
        return Color.YELLOW;
    }

}