package model.RobotTypes;

import model.Position;
import model.RobotModel.RobotBuilder;

import java.awt.*;
import java.util.List;

public class Robot2 extends BaseRobot {

    public Robot2(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior(){
        moveRandom(10,getRandom().nextDouble(),20);
    }

    @Override
    public Color getClassColor() {
        return Color.YELLOW;
    }

}