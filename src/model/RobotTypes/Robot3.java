package model.RobotTypes;

import model.AbstractModel.EntityBuilder;
import model.Position;

import java.awt.*;
import java.util.List;

public class Robot3 extends BaseRobot {

    public Robot3(EntityBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
        moveRandom(5, 0.5, 60);

    }

    @Override
    public Color getClassColor() {
        return Color.RED;
    }
}