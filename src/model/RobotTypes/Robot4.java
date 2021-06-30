package model.RobotTypes;

import model.*;
import model.AbstractModel.Entity;
import model.AbstractModel.PhysicalEntity;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class Robot4 extends LightConeRobot {

    public Robot4(RobotBuilder builder) {
        super(builder, 30, 45);
    }

    Position position = new Position(60, 10);

    @Override
    public void behavior() {
        setEngines(4,4);
        signal = listOfWallsInSight().size() > 0;
    }
    @Override
    public Color getClassColor() {
        return Color.BLUE;
    }
}