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
        SEARCHING(.2),
        RETURNINGFOOD(.4);
        public final double value;

        pheremoneType(double value) {
            this.value = value;
        }
    }

    List<Area> antAreasSpawned = new LinkedList<>();
    int i = 0;
    final int randomMoveLength = 5;
    final int randomAngleChange = 10;
    private boolean hasFood = false;
    List<Area> areaList = new LinkedList<>();
    List<Area> antAreaList = new LinkedList<>();

    public Ant(RobotBuilder builder) {
        super(builder);
    }


    @Override
    public void behavior() {
        Vector2D targetVector;
        areaList = getListOfAreasInSight();
        antAreaList = areaList.stream()
                .filter(area -> area.getNoticeableDistanceRadius() < 1)
                .collect(Collectors.toList());

        if (!hasFood) {
            targetVector = search(pheremoneType.SEARCHING.value, pheremoneType.RETURNINGFOOD.value, 2, true);
        } else {
            targetVector = search(pheremoneType.RETURNINGFOOD.value, pheremoneType.SEARCHING.value, 1, false);
        }
        if (i++ % ticsPerSimulatedSecond == 0) {
            fadePheromone();
        }
        if (targetVector.getLength() > 0) {
            driveToPosition(pose.creatPositionByIncreasing(targetVector), maxSpeed);
        } else {
            moveRandom(randomMoveLength, maxSpeed, randomAngleChange);
        }
        areaList.clear();
        antAreaList.clear();
    }

    /**
     * Adds new mark to the map
     */
    private void spawnPheromone(double size, double pheromoneType) {
        Area antArea = new Area(arena, new Random(), size, pheromoneType, pose.clone());
        arena.addEntity(antArea);
        antAreasSpawned.add(antArea);
    }

    /**
     * Decreases the size of the created pheromones
     * deletes the entries from each list if none left
     */
    private void fadePheromone() {
        if (antAreasSpawned.size() > 0) {
            antAreasSpawned.forEach(aArea -> aArea.increaseArea(-0.1));
            List<Area> faded = antAreasSpawned.stream().filter(aArea -> aArea.getDiameters() <= 0).collect(Collectors.toList());
            antAreasSpawned.removeAll(faded);
            arena.getEntityList().removeIf(faded::contains);
        }
    }

    private Vector2D search(double pheromoneTypeSpawning, double pheromoneTypeSearching, int searchedAreaType, boolean getFood) {
        Vector2D buffVec = new Vector2D(0, 0);
        List<Entity> group = antAreaList.stream()
                .filter(x -> x.getNoticeableDistanceDiameter() == pheromoneTypeSearching)
                .collect(Collectors.toList());
        if (areaList.stream().anyMatch(area -> area.getNoticeableDistanceDiameter() == searchedAreaType)) {
            Area searchedArea = areaList.stream().filter(area -> area.getNoticeableDistanceDiameter() == searchedAreaType)
                    .collect(Collectors.toList()).get(0);
            buffVec = Vector2D.creatCartesian(1, pose.getAngleToPosition(searchedArea.getPose()));
            if (isPositionInEntity(searchedArea.getClosestPositionInEntity(pose))) {
                hasFood = getFood;
                signal = getFood;
            }
         } else if (group.size() > 1) {
            Position strongest = group.stream().reduce((x, y) -> x.getWidth() > y.getWidth() ? x : y).get().getPose();
            Position weakest = group.stream().reduce((x, y) -> x.getWidth() < y.getWidth() ? x : y).get().getPose();
            Position center = centerOfGroupWithEntities(group);
            buffVec = Vector2D.creatCartesian(1, strongest.getAngleToPosition(weakest));
            buffVec = buffVec.subtract(Vector2D.creatCartesian(1,pose.getAngleDiff( pose.getAngleToPosition(center))));
        }
        if (i % ticsPerSimulatedSecond == 0) {
            spawnPheromone(1, pheromoneTypeSpawning);
        }
        return buffVec;
    }

    @Override
    public Color getClassColor() {
        return new Color(170, 100, 40, 90);
    }

}