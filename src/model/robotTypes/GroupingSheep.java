package model.robotTypes;

import helper.RobotBuilder;
import model.Position;
import model.Vector2D;
import model.abstractModel.Entity;

import java.awt.*;
import java.util.List;

public class GroupingSheep extends BaseRobot {

    public GroupingSheep(RobotBuilder builder) {
        super(builder);
    }

    Entity nextDog;
    final int fleeingDistance = 10;
    final int distanceToGroupEntities = 2;
    final int fleeingSpeed = 5;
    final int speedInGroup = 2;
    int logging =0;

    int i = 0;

    @Override
    public void behavior() {
        stayGroupedWithAllRobots(10, 8);
        Position groupCenter = centerOfGroupWithClasses(List.of(getClass()));
        if(i% (ticsPerSimulatedSecond / 20) == 0) {
            logger.logDouble(getId() + " Distant closest", distanceToClosestEntityOfClass(List.of(getClass())), 2);
            if(getId() == 19) {
                logger.logDouble(" center X", groupCenter.getX(), 1);
                logger.logDouble(" center Y", groupCenter.getY(), 1);
            }
        }
        i++;
    }


    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}