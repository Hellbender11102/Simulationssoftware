package model.RobotTypes;

import model.AbstractModel.RobotInterface;
import model.Position;
import model.RobotBuilder;

import java.awt.*;
import java.util.List;

public class Dog extends BaseRobot {

    public Dog(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
       driveToPosition(new Position(50,50));
    }

    @Override
    public Color getClassColor() {
        return Color.YELLOW;
    }

}