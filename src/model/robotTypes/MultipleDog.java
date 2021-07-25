package model.robotTypes;

import helper.RobotBuilder;
import model.*;
import model.abstractModel.RobotInterface;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleDog extends BaseRobot {

    public MultipleDog(RobotBuilder builder) {
        super(builder);
    }

    private final int sheepHerdDistance = 5;
    private final int avoidingDistance = 9;
    double i = 0;
    Position centerOfSheep;
    List<RobotInterface> sheepList = robotGroupByClasses(List.of(Sheep.class,GroupingSheep.class));
    double distanceSheeps;
    double distanceSheep;
    Position positionRunaway, positionFurthest;
    Position target = null;
    Vector2D movingResult;

    @Override
    public void behavior() {
        if(target == null && arena.getAreaList().size() > 0)
            target = arena.getAreaList().stream().findFirst().get().getPose();
        sheepList = robotGroupByClasses(List.of(Sheep.class,GroupingSheep.class));
        centerOfSheep = centerOfGroupWithRobots(sheepList);
        distanceSheeps = 0;
        for (RobotInterface sheep : sheepList) {
            Pose sheepPose = sheep.getPose();
            distanceSheep = sheep.distanceToClosestEntityOfClass(List.of(Sheep.class,GroupingSheep.class));
            distanceSheeps += distanceSheep;
            if (positionRunaway == null)
                positionRunaway = sheepPose;
            else
                positionRunaway = arena.getEuclideanDistanceToClosestPosition(sheepPose, centerOfSheep) + arena.getEuclideanDistanceToClosestPosition(sheepPose, target)
                        > arena.getEuclideanDistanceToClosestPosition(positionRunaway, centerOfSheep) + arena.getEuclideanDistanceToClosestPosition(positionRunaway, target)
                        ? sheepPose : positionRunaway;
            if (positionFurthest == null)
                positionFurthest = sheepPose;
            else
                positionFurthest = arena.getEuclideanDistanceToClosestPosition(sheepPose, target) > arena.getEuclideanDistanceToClosestPosition(sheepPose, target)
                        ? sheepPose : positionFurthest;
        }

        //if distance is greater than 2 cm for each sheep
        if (distanceSheeps > sheepList.size() * sheepHerdDistance &&  robotGroupByClasses(List.of(GroupingSheep.class)).size() == 0)
            movingResult = steerSheep(positionRunaway,centerOfSheep);
        else
            movingResult = steerSheep(positionFurthest,target);
        driveToPosition(pose.creatPositionByIncreasing(movingResult), movingResult.getLength());
    }

    private Vector2D steerSheep(Position sheep,Position target) {
        double angle = arena.getAngleToPosition(pose, sheep);
        Vector2D currentOrientation = Vector2D.creatCartesian(5, angle);
        List<RobotInterface> listOfToClose = sheepList.stream().filter(x -> arena.getEuclideanDistanceToClosestPosition(x.getPose(), pose) < avoidingDistance).collect(Collectors.toList());
        double speed = pose.getEuclideanDistance(sheep.creatPositionByIncreasing(currentOrientation));
        if (listOfToClose.size() > 0) {
            RobotInterface closestSheep = listOfToClose.stream().reduce((robot1, robot2) ->
                    arena.getEuclideanDistanceToClosestPosition(robot1.getPose(), pose) < arena.getEuclideanDistanceToClosestPosition(robot2.getPose(), pose) ? robot1 : robot2).get();
            currentOrientation.set(Vector2D.creatCartesian(5, arena.getAngleToPosition(closestSheep.getPose(), target) - Math.PI));
        }
        return currentOrientation.normalize().multiplication(speed);
    }


    @Override
    public Color getClassColor() {
        return Color.YELLOW;
    }

}