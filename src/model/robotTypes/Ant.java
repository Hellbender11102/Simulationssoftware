package model.robotTypes;

import helper.RobotBuilder;
import model.*;
import model.abstractModel.Entity;
import org.uncommons.maths.random.GaussianGenerator;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Ant extends BaseVisionConeRobot {
    enum pheromoneType {
        SEARCHING(.2),
        RETURNING(.4);
        public final double value;

        pheromoneType(double value) {
            this.value = value;
        }
    }

    private final List<Area> antAreasSpawned = new LinkedList<>();
    int i = 0;
    private final GaussianGenerator generator = new GaussianGenerator(0, Math.toRadians(20), random);
    private boolean hasFood = false;
    private List<Area> areaList = new LinkedList<>();
    private List<Area> antAreaList = new LinkedList<>();
    private Position turned = null;
    private Vector2D randomVec = new Vector2D(0, 0);

    public Ant(RobotBuilder builder) {
        super(builder);
    }


    private Position food = null, home = null;

    @Override
    public void behavior() {
        Vector2D targetVector;
        areaList = getListOfAreasInSight();
        antAreaList = areaList.stream()
                .filter(area -> area.getNoticeableDistanceRadius() < 1)
                .collect(Collectors.toList());

        if (!hasFood) {
            targetVector = search(pheromoneType.SEARCHING.value, pheromoneType.RETURNING.value, 2, 1, true);
        } else {
            targetVector = search(pheromoneType.RETURNING.value, pheromoneType.SEARCHING.value, 1, 2, false);
        }
        if (i++ % ticsPerSimulatedSecond == 0) {
            randomVec = Vector2D.creatCartesian(1, pose.getRotation() + generator.nextValue());
            fadePheromone();
        }
        if (targetVector.getLength() <= 0)
            targetVector = randomVec;
        if (hasFood && home != null)
            targetVector = targetVector.add(Vector2D.creatCartesian(2, pose.getAngleToPosition(home)));
        else if (!hasFood && food != null)
            targetVector = targetVector.add(Vector2D.creatCartesian(2, pose.getAngleToPosition(food)));
        driveToPosition(pose.creatPositionByIncreasing(targetVector));

        if (turned != null) {
            driveToPosition(turned);
            if (isPositionInEntity(turned))
                turned = null;
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

    private Vector2D search(double pheromoneTypeSpawning, double pheromoneTypeSearching, int searchedAreaType, int obtainedAreaType, boolean getFood) {
        Vector2D buffVec = new Vector2D(0, 0);
        List<Entity> group = antAreaList.stream()
                .filter(x -> x.getNoticeableDistanceDiameter() == pheromoneTypeSearching)
                .collect(Collectors.toList());
        if (areaList.stream().anyMatch(area -> area.getNoticeableDistanceDiameter() == searchedAreaType)) {
            Area searchedArea = areaList.stream().filter(area -> area.getNoticeableDistanceDiameter() == searchedAreaType)
                    .collect(Collectors.toList()).get(0);
            buffVec = Vector2D.creatCartesian(1, pose.getAngleToPosition(searchedArea.getPose()));

            if (isPositionInEntity(searchedArea.getClosestPositionInEntity(pose))) {
                if (hasFood == false && food == null)
                    food = searchedArea.getPose();
                else if (hasFood == true && home == null)
                    home = searchedArea.getPose();
                hasFood = getFood;
                signal = getFood;
                turned = pose.getPositionInDirection(3, searchedArea.getPose().getAngleToPosition(pose));
            }

        } else if (group.size() > 1) {
            double right = 0, left = 0, center = 0;
            for (Entity e : group) {
                double angleOfEntity = pose.getAngleToPosition(e.getPose()) < 0 ? pose.getAngleToPosition(e.getPose()) + 2 * Math.PI : pose.getAngleToPosition(e.getPose());
                double upper1 = pose.getRotation() + getVisionAngle() / 2, upper2 = pose.getRotation() + getVisionAngle() / 4;
                double lower1 = pose.getRotation() - getVisionAngle() / 4, lower2 = pose.getRotation() - getVisionAngle() / 2;
                if (inVisionArea(angleOfEntity, upper1, upper2)) left++;
                if (inVisionArea(angleOfEntity, upper2, lower1)) center++;
                if (inVisionArea(angleOfEntity, lower1, lower2)) right++;
            }
            if (center > Math.max(left, right)) {
                buffVec = Vector2D.creatCartesian(2, pose.getRotation());
            } else if (left > right) {
                buffVec = Vector2D.creatCartesian(2, pose.getRotation() + Math.PI / 2);
            } else if (right > left) {
                buffVec = Vector2D.creatCartesian(2, pose.getRotation() - Math.PI / 2);
            }

        }
        if (i % (ticsPerSimulatedSecond / 2) == 0) {
            spawnPheromone(1, pheromoneTypeSpawning);
        }
        return buffVec;
    }

    @Override
    public Color getClassColor() {
        return new Color(170, 100, 40, 90);
    }

    private boolean inVisionArea(double angleOfEntity, double upperAngle, double lowerAngle) {
        if (angleOfEntity <= upperAngle && angleOfEntity >= lowerAngle) {
            return true;
        } else if (upperAngle > 2 * Math.PI || lowerAngle < 0) {
            if (upperAngle >= 2 * Math.PI && angleOfEntity <= upperAngle % (2 * Math.PI)) {
                return true;
            } else return lowerAngle <= 0 && angleOfEntity >= lowerAngle + (2 * Math.PI);
        }
        return false;
    }
}