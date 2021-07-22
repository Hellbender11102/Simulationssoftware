package model.RobotTypes;

import model.AbstractModel.Entity;
import model.AbstractModel.PhysicalEntity;
import model.AbstractModel.RobotInterface;
import model.Pose;
import model.Position;
import model.RobotBuilder;

import java.awt.*;
import java.util.List;

public class Dog extends BaseRobot {

    public Dog(RobotBuilder builder) {
        super(builder);
    }

    Position centerOfSheep;
    List<RobotInterface> sheepList = robotGroupByClasses(List.of(Sheep.class));
    double distanceSheeps;
    double distanceSheep;
    Position positionRunaway;

    @Override
    public void behavior() {
        if (sheepList.size() == 0) {
            sheepList = robotGroupByClasses(List.of(Sheep.class));
        }
        centerOfSheep = centerOfGroupWithClasses(List.of(Sheep.class));
        distanceSheeps = 0;
        for (RobotInterface sheep : sheepList) {
            distanceSheep = sheep.distanceToClosestEntityOfClass(List.of(Sheep.class));
            distanceSheeps += distanceSheep;
            if (positionRunaway == null)
                positionRunaway = sheep.getPose();
            else
                positionRunaway = sheep.getPose().getEuclideanDistance(centerOfSheep) > positionRunaway.getEuclideanDistance(centerOfSheep)
                        ? sheep.getPose() : positionRunaway;
        }
        //if distance is greater than 2 cm for each sheep
        if (distanceSheeps > sheepList.size()*2)
            groupSheeps();
        else
            steerSheeps();
    }

    private void groupSheeps() {
        double angle = positionRunaway.getAngleToPosition(centerOfSheep);
        Pose pose = new Pose(positionRunaway, angle - Math.PI);
        driveToPosition(pose.getPositionInDirection(3));
    }

    private void steerSheeps() {

    }


    @Override
    public Color getClassColor() {
        return Color.YELLOW;
    }

}