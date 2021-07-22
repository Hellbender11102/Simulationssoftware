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

    int i = 0;
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
        if (20 > nextDogDistance){
            Vector2D fleeDog = pose.getVectorInDirection(1, pose.getAngleToPosition(nextDog.getPose())+Math.PI);
            fleeDog.add(pose.getVectorInDirection(1,pose.getAngleToPosition(center)));
            driveToPosition(pose.creatPositionByDecreasing(fleeDog.getX(),fleeDog.getY()),6);
        }else if (10 > nextSheepDistance){
            driveToPosition(center,3);
        }else moveRandom(1,0.5,30);
    }

    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}