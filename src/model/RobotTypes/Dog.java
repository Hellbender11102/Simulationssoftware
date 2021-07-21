package model.RobotTypes;

import model.AbstractModel.Entity;
import model.AbstractModel.PhysicalEntity;
import model.AbstractModel.RobotInterface;
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
    double distanceSheep =0;
    Position positionRunaway;
    @Override
    public void behavior() {
        centerOfSheep = centerOfGroupWithClasses(List.of(Sheep.class));
        distanceSheeps=0;
        for (RobotInterface sheep: sheepList) {
            distanceSheep = sheep.distanceToClosestEntityOfClass(List.of(Sheep.class));
            distanceSheeps += distanceSheep;
            positionRunaway = sheep.getPose().getEuclideanDistance(centerOfSheep) > positionRunaway.getEuclideanDistance(centerOfSheep)
            ? sheep.getPose(): positionRunaway;
        }
        //if distance is greater than 2 cm for each sheep
        if(distanceSheeps > sheepList.size() * 2)
            groupSheeps();
        else
            steerSheeps();
    }

    private void groupSheeps(){
        positionRunaway.getAngleToPosition(centerOfSheep);
    }
    private void steerSheeps(){

    }



    @Override
    public Color getClassColor() {
        return Color.YELLOW;
    }

}