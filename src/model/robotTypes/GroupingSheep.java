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

    @Override
    public void behavior() {
        nextDog = closestEntityOfClass(List.of(SingleDog.class));
        if (fleeingDistance > arena.getEuclideanDistanceToClosestPosition(pose, nextDog.getPose())) {
            Vector2D fleeVec = pose.getVectorInDirection(1, arena.getAngleToPosition(pose, nextDog.getPose()));
            driveToPosition(pose.creatPositionByDecreasing(fleeVec), fleeingSpeed);
        } else
            stayGroupedWithRobotType(distanceToGroupEntities, List.of(this.getClass()), speedInGroup);
        //logs once per simulated second
        if (logging++ % ticsPerSimulatedSecond == 0) {
            Position center = centerOfGroupWithClasses( List.of(this.getClass()));
            logger.logDouble(getId()+": sheep-x",pose.getX(),2);
            logger.logDouble(getId()+": sheep-y",pose.getY(),2);
            logger.logDouble(getId()+": distance-center",arena.getEuclideanDistanceToClosestPosition(pose,center),2);
            logger.logDouble(getId()+": distance-closest",arena.getEuclideanDistanceToClosestPosition(pose,center),2);
        }
    }

    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}