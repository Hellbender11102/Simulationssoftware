package model.RobotTypes;

import model.AbstractModel.RobotBuilder;

import java.awt.*;

public class Robot2 extends BaseRobot {

    public Robot2(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior(){
        moveRandom(10,getRandom().nextDouble(),90);
    }

    @Override
    public Color getClassColor() {
        return Color.YELLOW;
    }

}