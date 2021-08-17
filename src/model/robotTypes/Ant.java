package model.robotTypes;

import helper.RobotBuilder;
import model.*;
import model.abstractModel.Entity;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Ant extends BaseVisionConeRobot {
    enum pheremoneType {
        SEARCHING(.1),
        RETURNINGFOOD(.2);
        public final double value;

        pheremoneType(double value) {
            this.value = value;
        }
    }

    List<Area> antAreasSpawend = new LinkedList<>();
    int i = 0;
    final int randomMoveLength = 5;
    final int randomAngleChange = 10;
    private boolean hasFood = false;
    Position nest = null;
    List<Area> areaList = new LinkedList<>();
    List<Area> antAreaList = new LinkedList<>();

    public Ant(RobotBuilder builder) {
        super(builder);
    }


    @Override
    public void behavior() {
        Vector2D movingVector = new Vector2D(0, 0);
        areaList = getListOfAreasInSight();
        antAreaList = areaList.stream()
                .filter(area -> area.getNoticeableDistanceRadius() == pheremoneType.RETURNINGFOOD.value ||
                        area.getNoticeableDistanceRadius() == pheremoneType.SEARCHING.value)
                .collect(Collectors.toList());
        if (!hasFood) {
            if (areaList.stream().anyMatch(area -> area.getNoticeableDistanceDiameter() == area.getDiameters())) {
                Area food = areaList.stream().filter(area -> area.getNoticeableDistanceDiameter() ==
                        area.getDiameters()).collect(Collectors.toList()).get(0);
                driveToPosition(food.getPose());
                if (i % ticsPerSimulatedSecond == 0) {
                    spawnPheromone(1, pheremoneType.SEARCHING.value);
                }
                if (isPositionInEntity(food.getClosestPositionInEntity(pose))) {
                    hasFood = true;
                    signal = true;
                    // food.increaseArea(-1);
                    // food.increaseNoticeableDistanceDiameters(-1);
                }
            } else if (antAreaList.size() != 0) {
                movingVector = Vector2D.creatCartesian(2, pose.getAngleToPosition(centerOfGroupWithEntities(areaList.stream()
                        .filter(x -> x.getNoticeableDistanceDiameter() == pheremoneType.RETURNINGFOOD.value)
                        .map(x -> (Entity) x).collect(Collectors.toList()))));
                if (i % ticsPerSimulatedSecond == 0) {
                    spawnPheromone(1, pheremoneType.SEARCHING.value);
                }
            } else {
                moveRandom(randomMoveLength, maxSpeed, randomAngleChange);
                if (i % ticsPerSimulatedSecond == 0) {
                    spawnPheromone(1, pheremoneType.SEARCHING.value);
                }
            }

        } else {
            if (areaList.stream().anyMatch(x -> x.getNoticeableDistanceDiameter() == 1.1)) {
                hasFood = false;
                signal = false;
            } else if (areaList.size() > 0) {
                movingVector = Vector2D.creatCartesian(2, pose.getAngleToPosition(centerOfGroupWithEntities(areaList.stream()
                        .filter(x -> x.getNoticeableDistanceDiameter() == pheremoneType.SEARCHING.value)
                        .map(x -> (Entity) x).collect(Collectors.toList()))));
                if (i % ticsPerSimulatedSecond == 0) {
                    spawnPheromone(1, pheremoneType.RETURNINGFOOD.value);
                }
            } else {
                moveRandom(randomMoveLength, maxSpeed, randomAngleChange);
                if (i % ticsPerSimulatedSecond == 0) {
                    spawnPheromone(1, pheremoneType.RETURNINGFOOD.value);
                }
            }
        }
        if (i++ % ticsPerSimulatedSecond == 0) {
            fadePheromone();
            i = 1;
        }
        if (movingVector.getLength() > 0) {
            driveToPosition(pose.creatPositionByIncreasing(movingVector));
        }
    }

    /**
     * Adds new mark to the map
     */
    private void spawnPheromone(double size, double pheromoneType) {
        Area antArea = new Area(arena, new Random(), size, pheromoneType, pose.clone());
        antAreasSpawend.add(antArea);
        arena.addEntity(antArea);
    }

    /**
     * Decreases the size of the created pheromones
     * deletes the entries from each list if none left
     */
    private void fadePheromone() {
        if (antAreasSpawend.size() > 0) {
            antAreasSpawend.forEach(aArea -> aArea.increaseArea(-0.2));
            List<Area> faded = antAreasSpawend.stream().filter(aArea -> aArea.getDiameters() <= 0).collect(Collectors.toList());
            antAreasSpawend.removeAll(faded);
            arena.getEntityList().stream().filter(faded::contains);
        }
    }


    @Override
    public Color getClassColor() {
        return new Color(170, 100, 40, 90);
    }

}