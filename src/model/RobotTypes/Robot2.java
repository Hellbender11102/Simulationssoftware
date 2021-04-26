package model.RobotTypes;

import model.AbstractModel.EntityBuilder;

import java.awt.*;

public class Robot2 extends BaseRobot {

    public Robot2(EntityBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior(){
        moveRandom(10,getRandom().nextDouble(),4);
    }

    @Override
    public Color getClassColor() {
        return Color.YELLOW;
    }

}