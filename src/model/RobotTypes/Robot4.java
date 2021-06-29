package model.RobotTypes;

import model.AbstractModel.Entity;
import model.Area;
import model.Position;
import model.RobotBuilder;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class Robot4 extends LightConeRobot {

    public Robot4(RobotBuilder builder) {
        super(builder, 15, 45);
    }

    Position position = new Position(60, 10);

    @Override
    public void behavior() {
        setEngines(4,4);
        List<Entity> entities = listOfEntityInVision();
        List<Area> areas = entities.stream().filter(x -> Area.class.isAssignableFrom(x.getClass())).map(x -> (Area) x).collect(Collectors.toList());
        if (areas.size() > 0) {
            signal = isAreaVisionRangeInSight(areas.get(0));
        }
    }
    @Override
    public Color getClassColor() {
        return Color.BLUE;
    }
}