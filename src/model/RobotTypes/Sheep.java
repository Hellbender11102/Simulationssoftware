package model.RobotTypes;

import model.AbstractModel.Entity;
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
    Entity nextSheep;
    @Override
    public void behavior() {
        center = centerOfGroupWithClasses(List.of(this.getClass()));
        nextDog = closestEntityOfClass(List.of(Dog.class));
        nextSheep = closestEntityOfClass(List.of(this.getClass()));
        double nextDogDistance = arena.getEuclideanDistanceToClosestPosition(pose,nextDog.getPose());
        double nextSheepDistance = arena.getEuclideanDistanceToClosestPosition(pose,nextSheep.getPose());
        if (10 > nextDogDistance){
            Vector2D fleeVec = pose.getVectorInDirection(2, arena.getAngleToPosition(pose,nextDog.getPose()));
            fleeVec = fleeVec.add(pose.getVectorInDirection(1,pose.getAngleToPosition(nextSheep.getPose())));
            driveToPosition(pose.creatPositionByDecreasing(fleeVec),5);
        }else if (10 < nextSheepDistance){
            driveToPosition(nextSheep.getPose(),3);
        }else moveRandom(1,0.5,30);
    }

    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}