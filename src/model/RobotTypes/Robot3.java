package model.RobotTypes;

import model.AbstractModel.RobotBuilder;

import java.awt.*;

public class Robot3 extends BaseRobot {

    public Robot3(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
       // moveRandom(5, 0.5, 60);
        stayGroupedWithAll(50,.75);
    }

    @Override
    public Color getClassColor() {
        return Color.RED;
    }
}