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
    driveToPosition(new Position(250,250),1,0.01);
    if(isPositionInRobotArea(new Position(250,250)))
     toggleStop();
    }
    @Override
    public Color getClassColor() {
        return Color.RED;
    }
}