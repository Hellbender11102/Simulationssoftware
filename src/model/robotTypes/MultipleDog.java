package model.robotTypes;

import helper.RobotBuilder;
import model.*;
import model.abstractModel.RobotInterface;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleDog extends BaseRobot {

    public MultipleDog(RobotBuilder builder) {
        super(builder);
    }

    private final int sheepHerdDistance = 5;
    private final int avoidingDistance = 9;
    private final int avoidDogs = 15;
    double i = 0;
    Position centerOfSheep;
    List<RobotInterface> sheepList;
    double distanceSheeps;
    double distanceSheep;
    Position positionRunaway, positionFurthest;
    Position target = null;
    List<Position> rotatedPositions = new LinkedList<>();
    Vector2D movingResult;
    int a = 1, b = 1;

    //This is an approach to multiple behavior of dogs to guide sheeps
    @Override
    public void behavior() {
        if (target == null && arena.getAreaList().size() > 0) {
            target = arena.getAreaList().get(0).getPose();
        }
        sheepList = robotGroupByClasses(List.of(Sheep.class, GroupingSheep.class));
        centerOfSheep = centerOfGroupWithRobots(sheepList);
        double angleCenterTarget = arena.getAngleToPosition(centerOfSheep, target);

        //rotate
        for (RobotInterface sheep : sheepList) {
            Pose position = sheep.getPose();
            rotatedPositions.add(new Position(
                    position.getX() * Math.cos(angleCenterTarget) + position.getY() * Math.sin(angleCenterTarget),
                    -position.getX() * Math.sin(angleCenterTarget) + position.getY() * Math.cos(angleCenterTarget)
            ));
            new Position(Vector2D.creatCartesian(position.getEuclideanDistance(0,0),angleCenterTarget).getX(),
                    Vector2D.creatCartesian( position.getEuclideanDistance(0,0),angleCenterTarget).getY());
        }
        double minX = Double.NaN, minY = Double.NaN, maxX = Double.NaN, maxY = Double.NaN;
        for (Position position : rotatedPositions) {
            if (Double.isNaN(minX)) {
                minX = position.getX();
                minY = position.getY();
                maxX = position.getX();
                maxY = position.getY();
            } else {
                minX = Math.min(minX, position.getX());
                minY = Math.min(minY, position.getY());
                maxX = Math.max(maxX, position.getX());
                maxY = Math.max(maxY, position.getY());
            }
        }
        //rotate back max/ min positions
        Position corner1 = new Position(
                maxX * Math.cos(angleCenterTarget) - maxY * Math.sin(angleCenterTarget),
                maxX * Math.sin(angleCenterTarget) + maxY * Math.cos(angleCenterTarget));
        Position corner2 = new Position(
                maxX * Math.cos(angleCenterTarget) - minY * Math.sin(angleCenterTarget),
                maxX * Math.sin(angleCenterTarget) + minY * Math.cos(angleCenterTarget));
        Position corner3 = new Position(
                minX * Math.cos(angleCenterTarget) - maxY * Math.sin(angleCenterTarget),
                minX * Math.sin(angleCenterTarget) + maxY * Math.cos(angleCenterTarget));
        Position corner4 = new Position(
                minX * Math.cos(angleCenterTarget) - minY * Math.sin(angleCenterTarget),
                minX * Math.sin(angleCenterTarget) + minY * Math.cos(angleCenterTarget));
        Position drivingTo = new Position(0, 0);
        if (a == 1) {
            drivingTo = corner1;
            if (arena.getEuclideanDistanceToClosestPosition(pose, corner1) < getRadius()) {
                b = 1;
                a += b;
            }
        } else if (a == 2) {
            drivingTo = corner3;
            if (arena.getEuclideanDistanceToClosestPosition(pose, corner3) < getRadius()) {
                a += b;
            }
        } else if (a == 3) {
            drivingTo = corner4;
            if (arena.getEuclideanDistanceToClosestPosition(pose, corner4) < getRadius())
                a += b;
        } else if (a == 4) {
            drivingTo = corner2;
            if (arena.getEuclideanDistanceToClosestPosition(pose, corner2) < getRadius()) {
                b = -1;
                a += b;
            }
        }
        drivingTo=arena.setPositionInBoundsTorus(drivingTo);
        System.out.println(drivingTo);
        System.out.println(a);
        driveToPosition(drivingTo);
    }

    @Override
    public Color getClassColor() {
        return Color.RED;
    }

}