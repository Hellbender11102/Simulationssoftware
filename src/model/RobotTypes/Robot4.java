package model.RobotTypes;

import model.*;

import java.awt.*;

public class Robot4 extends BaseVisionConeRobot {

    public Robot4(RobotBuilder builder) {
        super(builder, 30, 45);
    }
Position position = new Position(1,1);
    @Override
    public void behavior() {
        setEngines(5,5);
        if(isArenaBoundsInVision()){
            signal=true;
        }
    }
    @Override
    public Color getClassColor() {
        return Color.BLUE;
    }
}