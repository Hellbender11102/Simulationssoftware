package model.RobotTypes;

import model.Position;
import model.RobotBuilder;

import java.awt.*;

public class Robot4 extends LightConeRobot {

    public Robot4(RobotBuilder builder) {
        super(builder,15,45);
    }
    Position position=new Position(60,10);
    @Override
    public void behavior() {
        stayGroupedWithAllRobots(15,8);
        if(isArenaBoundsInVision()) signal = true;
        else signal = false;
    }

    @Override
    public Color getClassColor() {
        return Color.BLUE;
    }
}