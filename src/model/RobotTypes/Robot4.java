package model.RobotTypes;

import model.Position;
import model.RobotBuilder;

import java.awt.*;

public class Robot4 extends LightConeRobot {

    public Robot4(RobotBuilder builder) {
        super(builder,20,45);
    }
    Position position=new Position(40,10);
    @Override
    public void behavior() {

        driveToPosition(position,2,8);
        System.out.println(listOfEntitysInVision().size());
    }

    @Override
    public Color getClassColor() {
        return Color.BLUE;
    }
}