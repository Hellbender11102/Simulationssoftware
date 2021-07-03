package model.RobotTypes;

import model.*;
import model.AbstractModel.BaseEntity;
import model.AbstractModel.Entity;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Robot4 extends BaseVisionConeRobot {

    public Robot4(RobotBuilder builder) {
        super(builder);
    }

    LinkedList<Entity> entities= entityGroupByClasses(List.of(Entity.class));

    @Override
    public void behavior() {
        stayGroupedWithRobotType(5, List.of(Entity.class), 10, 1);
        if(isArenaBoundsInVision()) signal = true;
        else signal = false;
    }

    @Override
    public Color getClassColor() {
        return Color.BLUE;
    }
}