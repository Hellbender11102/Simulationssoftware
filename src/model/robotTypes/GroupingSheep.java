package model.robotTypes;

import helper.RobotBuilder;
import model.Vector2D;
import model.abstractModel.Entity;

import java.awt.*;
import java.util.List;

public class GroupingSheep extends BaseRobot {

    public GroupingSheep(RobotBuilder builder) {
        super(builder);
    }

    Entity nextDog;

    @Override
    public void behavior() {
        nextDog = closestEntityOfClass(List.of(SingleDog.class));
        if (10 > arena.getEuclideanDistanceToClosestPosition(pose, nextDog.getPose())) {
            Vector2D fleeVec = pose.getVectorInDirection(1, arena.getAngleToPosition(pose, nextDog.getPose()));
            driveToPosition(pose.creatPositionByDecreasing(fleeVec), 5);
        } else
            stayGroupedWithRobotType(2, List.of(this.getClass()), 4);
    }

    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}