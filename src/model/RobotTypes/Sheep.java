package model.RobotTypes;

import model.AbstractModel.Entity;
import model.AbstractModel.PhysicalEntity;
import model.AbstractModel.RobotInterface;
import model.Pose;
import model.Position;
import model.RobotBuilder;
import model.Vector2D;

import java.awt.*;
import java.util.List;

public class Sheep extends BaseRobot {

    public Sheep(RobotBuilder builder) {
        super(builder);
    }

    Position center;
    Entity nextDog;
    RobotInterface nextSheep;
    @Override
    public void behavior() {
        center = centerOfGroupWithClasses(List.of(this.getClass()));
        nextDog = closestEntityOfClass(List.of(Dog.class));
        nextSheep = (RobotInterface) closestEntityOfClass(List.of(this.getClass()));
        Pose nextSheepPose = nextSheep.getPose();
        double nextDogDistance = arena.getEuclideanDistanceToClosestPosition(pose,nextDog.getPose());
        double nextSheepDistance = arena.getEuclideanDistanceToClosestPosition(pose,nextSheepPose);
        if (10 > nextDogDistance){
            Vector2D fleeVec = pose.getVectorInDirection(1, arena.getAngleToPosition(pose,nextDog.getPose()));
            fleeVec= fleeVec.add( pose.getVectorInDirection(1, arena.getAngleToPosition(nextSheepPose,pose)));
            driveToPosition(pose.creatPositionByDecreasing(fleeVec),5);
            signal = true;
        }else if (10 < nextSheepDistance){
            driveToPosition(nextSheepPose,3);
            signal = false;
        }else if(nextSheep.getSignal()) {
            driveToPosition(pose.getPositionInDirection(2, nextSheepPose.getRotation()),5 );
            signal = false;
        }
        else {
            moveRandom(3, 0.5, 20);
            signal = false;
        }
    }

    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}