package model.RobotTypes;

import model.*;
import model.AbstractModel.RobotInterface;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Dog extends BaseRobot {

    public Dog(RobotBuilder builder) {
        super(builder);
    }
double i = 0;
    Position centerOfSheep;
    List<RobotInterface> sheepList = robotGroupByClasses(List.of(Sheep.class));
    double distanceSheeps;
    double distanceSheep;
    Position positionRunaway, positionFurthest;
    final Position target = new Position(100,100);
    Vector2D movingResult;
    @Override
    public void behavior() {
            sheepList = robotGroupByClasses(List.of(Sheep.class));
        centerOfSheep = centerOfGroupWithRobots(sheepList);
        distanceSheeps = 0;
        for (RobotInterface sheep : sheepList) {
            Pose sheepPose =sheep.getPose();
            distanceSheep = sheep.distanceToClosestEntityOfClass(List.of(Sheep.class));
            distanceSheeps += distanceSheep;
            if (positionRunaway == null)
                positionRunaway = sheepPose;
            else
                positionRunaway = sheepPose.getEuclideanDistance(centerOfSheep) > positionRunaway.getEuclideanDistance(centerOfSheep)
                        ? sheepPose : positionRunaway;
            if (positionFurthest == null)
                positionFurthest = sheepPose;
            else
                positionFurthest = sheepPose.getEuclideanDistance(target) > positionFurthest.getEuclideanDistance(target)
                        ? sheepPose : positionFurthest;
            if(sheepPose.getEuclideanDistance(target)< 5)
                logger.logDouble("finish",i++,0);

        }

        //if distance is greater than 2 cm for each sheep
        if (distanceSheeps > sheepList.size()*5)
            movingResult=  groupSheeps();
        else
            movingResult= steerSheeps();
        driveToPosition(pose.creatPositionByDecreasing(movingResult.reverse()),movingResult.getLength());
    }

    private Vector2D groupSheeps() {
        double angle = pose.getAngleToPosition(positionRunaway);
        Vector2D currentOrientation = Vector2D.creatCartesian(1,angle);
        List<RobotInterface> listOfToClose = sheepList.stream().filter(x->x.getPose().getEuclideanDistance(pose) < 9.8).collect(Collectors.toList());
        double speed = pose.getEuclideanDistance(positionRunaway.creatPositionByDecreasing(currentOrientation.reverse()));
        if(listOfToClose.size()>0) {
            RobotInterface closestSheep = listOfToClose.stream().reduce((robot1, robot2) -> robot1.getPose().getEuclideanDistance(pose) < robot2.getPose().getEuclideanDistance(pose) ? robot1:robot2).get();
            currentOrientation= currentOrientation.add(Vector2D.creatCartesian(2,closestSheep.getPose().getAngleToPosition(centerOfSheep)-Math.PI));
        }
        return currentOrientation.normalize().multiplication(speed);
    }

    private Vector2D steerSheeps() {
        double angle = pose.getAngleToPosition(positionFurthest);
        Vector2D currentOrientation = Vector2D.creatCartesian(1,angle);
        List<RobotInterface> listOfToClose = sheepList.stream().filter(x->x.getPose().getEuclideanDistance(pose) < 9.8).collect(Collectors.toList());
        double speed = pose.getEuclideanDistance(positionFurthest.creatPositionByDecreasing(currentOrientation.reverse()));
        if(listOfToClose.size()>0) {
            RobotInterface closestSheep = listOfToClose.stream().reduce((robot1, robot2) -> robot1.getPose().getEuclideanDistance(pose) < robot2.getPose().getEuclideanDistance(pose) ? robot1:robot2).get();
            currentOrientation= currentOrientation.add(Vector2D.creatCartesian(2,closestSheep.getPose().getAngleToPosition(centerOfSheep)-Math.PI));
        }
        return currentOrientation.normalize().multiplication(speed);
    }


    @Override
    public Color getClassColor() {
        return Color.YELLOW;
    }

}