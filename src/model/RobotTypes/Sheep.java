package model.RobotTypes;

import model.AbstractModel.Entity;
import model.Position;
import model.RobotBuilder;

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
        if (20 > nextDogDistance){
            Position fleeDog = pose.getPoseInDirection(1,
                    pose.getAngleToPosition(nextDog.getPose())+Math.PI);
            driveToPosition(fleeDog.addToPosition(nextSheep.getPose().toVector()));
        }else moveRandom(1,0.5,30);
    }

    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}