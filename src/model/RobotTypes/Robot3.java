package model.RobotTypes;

import model.RobotBuilder;

import java.awt.*;

public class Robot3 extends BaseRobot {

    public Robot3(RobotBuilder builder) {
        super(builder);
    }
int i =0;
    @Override
    public void behavior() {
     // moveAndStop(100,8);
       if (move(100,8))i++;
       // System.out.println(i);
    }

    @Override
    public Color getClassColor() {
        return Color.RED;
    }
}