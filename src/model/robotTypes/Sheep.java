package model.robotTypes;

import model.abstractModel.*;
import helper.RobotBuilder;
import model.*;

import java.awt.*;
import java.util.List;

public class Sheep extends BaseRobot {

    public Sheep(RobotBuilder builder) {
        super(builder);
    }

    Position center;
    Entity nextDog;
    RobotInterface nextSheep;
    final int fleeingDistance = 10;
    final int fleeingSpeed = 5;
    final int panicSpeed = 3;
    final int moveRandomSpeed = 1;
    final int turnRadius = 20;
    final int pathLength = 3;

    int logging = 0;

    @Override
    public void behavior() {
        center = centerOfGroupWithClasses(List.of(this.getClass()));
        nextDog = closestEntityOfClass(List.of(SingleDog.class));
        nextSheep = (RobotInterface) closestEntityOfClass(List.of(this.getClass()));
        Pose nextSheepPose = nextSheep.getPose();
        double nextDogDistance = arena.getEuclideanDistanceToClosestPosition(pose, nextDog.getPose());
        double nextSheepDistance = arena.getEuclideanDistanceToClosestPosition(pose, nextSheepPose);
        if (fleeingDistance > nextDogDistance) {
            Vector2D fleeVec = pose.getVectorInDirection(1, arena.getAngleToPosition(pose, nextDog.getPose()));
            fleeVec = fleeVec.add(pose.getVectorInDirection(1, arena.getAngleToPosition(center, pose)));
            driveToPosition(pose.creatPositionByDecreasing(fleeVec), fleeingSpeed);
            signal = true;
        } else if (fleeingDistance < nextSheepDistance) {
            driveToPosition(nextSheepPose, panicSpeed);
            signal = false;
        } else if (nextSheep.getSignal()) {
            driveToPosition(pose.getPositionInDirection(1, nextSheepPose.getRotation()), fleeingSpeed);
            signal = false;
        } else {
            moveRandom(pathLength, moveRandomSpeed, turnRadius);
            signal = false;
        }
        /*
          Functional logging code
          it will log once per simulated second
          it will log the position for each sheep on the same key
          also it keeps track of the distance to the sheep center
          and the closest sheep
        if (logging++ % ticsPerSimulatedSecond == 0) {
            logger.logDouble("sheepX",pose.getX(),2);
            logger.logDouble("sheepY",pose.getY(),2);
            logger.logDouble(getId()+"distance-center",arena.getEuclideanDistanceToClosestPosition(pose,center),2);
            logger.logDouble(getId()+"distance-closest",arena.getEuclideanDistanceToClosestPosition(pose,nextSheepPose),2);
        }
           */
    }

    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}