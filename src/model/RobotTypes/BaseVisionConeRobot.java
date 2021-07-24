package model.RobotTypes;

import model.*;
import model.AbstractModel.Entity;
import model.AbstractModel.RobotInterface;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseVisionConeRobot extends BaseRobot {

    private final double visionRange, visionAngle;

    /**
     * Constructs object via Builder
     *
     * @param builder     RobotBuilder
     * @param visionRange double
     * @param visionAngle double
     */
    public BaseVisionConeRobot(RobotBuilder builder, double visionRange, double visionAngle) {
        super(builder);
        this.visionRange = visionRange > 0 ? visionRange : 0;
        visionAngle = visionAngle > 360 ? 360 : visionAngle < 0 ? 0 : visionAngle;
        this.visionAngle = Math.toRadians(visionAngle);
    }

    public BaseVisionConeRobot(RobotBuilder builder) {
        super(builder);
        this.visionRange = builder.getVisionRange() > 0 ? builder.getVisionRange() : 0;
        double visionAngle = builder.getVisionAngle();
        visionAngle = visionAngle > 360 ? 360 : visionAngle < 0 ? 0 : visionAngle;
        this.visionAngle = Math.toRadians(visionAngle);
    }

    /**
     * Returns true if the the bounds are in sight
     *
     * @return boolean
     */
    public boolean isArenaBoundsInVision() {
        double rotation = pose.getRotation();
        for (double i = rotation - visionAngle / 2; i <= rotation + visionAngle / 2; i += Math.toRadians(1)) {
            Position position = pose.getPositionInDirection(visionRange, i);
            if (!arena.inArenaBounds(position))
                return true;
        }
        return false;
    }

    /**
     * Returns all entities in the current vision
     * If entity is an area it will be listen when vision range of area is inside vision range of this
     *
     * @return List<Entity>
     */
    public List<Entity> getListOfEntityInVision() {
        List<Entity> entityList = new LinkedList<>();
        entityList.addAll(getListOfBoxesInSight());
        entityList.addAll(getListOfWallsInSight());
        entityList.addAll(getListOfAreasInSight());
        entityList.addAll(getListOfRobotsInSight());
        entityList.remove(this);
        return entityList;
    }

    /**
     * Returns all Areas in the current vision
     *
     * @return List<Area>
     */
    public List<Area> getListOfAreasInSight() {
        List<Area> entityList = new LinkedList<>();
        for (Area area : arena.getAreaList()) {
            if (isAreaInSight(area)) {
                entityList.add(area);
            }
        }
        return entityList;
    }

    /**
     * Returns all Areas in the current vision
     * Checks for areas noticeable distance
     *
     * @return List<Area>
     */
    public List<Area> getListOfAreasInSightByAreaNoticeableDistance() {
        List<Area> entityList = new LinkedList<>();
        for (Area area : arena.getAreaList()) {
            if (isAreaVisionRangeInSight(area)) {
                entityList.add(area);
            }
        }
        return entityList;
    }

    /**
     * Returns all Robots in the current vision
     *
     * @return List<RobotInterface>
     */
    public List<RobotInterface> getListOfRobotsInSight() {
        List<RobotInterface> entityList = new LinkedList<>();
        for (RobotInterface robotInterface : arena.getRobots()) {
            if (isCircleInSight(robotInterface.getPose(), robotInterface.getRadius())) {
                entityList.add(robotInterface);
            }
        }
        return entityList;
    }

    /**
     * Returns all Boxes in the current vision
     *
     * @return List<Entity>
     */
    public List<Box> getListOfBoxesInSight() {
        List<Box> entityList = new LinkedList<>();
        for (Box box : arena.getBoxList()) {
            if (isRectangleInSight(box.getPose(), box.getWidth(), box.getHeight())) {
                entityList.add(box);
            }
        }
        return entityList;
    }

    /**
     * Returns all Walls in the current vision
     *
     * @return List<Entity>
     */
    public List<Wall> getListOfWallsInSight() {
        List<Wall> entityList = new LinkedList<>();
        for (Wall wall : arena.getWallList()) {
            if (isRectangleInSight(wall.getPose(), wall.getWidth(), wall.getHeight())) {
                entityList.add(wall);
            }
        }
        return entityList;
    }


    /**
     * Returns true if the given position is in sight
     *
     * @param position Position
     * @return boolean
     */
    public boolean isPositionInVisionCone(Position position) {
        if (arena.isTorus) {
            for (int x = -arena.getWidth(); x < arena.getWidth(); x += arena.getWidth()) {
                for (int y = -arena.getHeight(); y < arena.getHeight(); y += arena.getHeight()) {
                    Position pos = position.creatPositionByDecreasing(x, y);
                    if (isInVisionAngle(pos) && pose.getEuclideanDistance(pos) <= visionRange)
                        return true;
                }
            }
        } else if (pose.getEuclideanDistance(position) <= visionRange) {
            return isInVisionAngle(position);
        }
        return false;
    }

    /**
     * Returns true if the position is located in the cone of the vision
     *
     * @param position Position
     * @return boolean
     */
    public boolean isInVisionAngle(Position position) {
        double angleOfEntity = pose.getAngleToPosition(position) < 0 ? pose.getAngleToPosition(position) + 2 * Math.PI : pose.getAngleToPosition(position);
        double upperAngle = pose.getRotation() + visionAngle / 2;
        double lowerAngle = pose.getRotation() - visionAngle / 2;
        if (angleOfEntity <= upperAngle && angleOfEntity >= lowerAngle) {
            return true;
        } else if (upperAngle > 2 * Math.PI || lowerAngle < 0) {
            if (upperAngle >= 2 * Math.PI && angleOfEntity <= upperAngle % (2 * Math.PI)) {
                return true;
            } else return lowerAngle <= 0 && angleOfEntity >= lowerAngle + (2 * Math.PI);
        }
        return false;
    }

    /**
     * Returns true if either the robot is inside the Area vision
     * Or the Vision range reaches the Area vision range
     *
     * @param area Area
     * @return boolean
     */
    public boolean isAreaVisionRangeInSight(Area area) {
        return isCircleInSight(area.getPose(), area.getNoticeableDistanceRadius());
    }

    /**
     * Returns true if either the robot is inside the Area
     * Or the Vision range reaches the Area
     *
     * @param area Area
     * @return boolean
     */
    public boolean isAreaInSight(Area area) {
        return isCircleInSight(area.getPose(), area.getRadius());
    }

    /**
     * Returns true if either the robot is inside circle
     * Or the Vision range reaches the circle
     *
     * @param center   Position
     * @param distance double
     * @return boolean
     */
    public boolean isCircleInSight(Position center, double distance) {
        double rotation = pose.getRotation();
        // if body is in noticeable distance
        if (pose.getEuclideanDistance(center) <= distance + getRadius()) return true;
        // one side of the vision cone is in sight
        Line2D firstConeLine = new Line2D.Double(pose, pose.getPositionInDirection(visionRange, rotation - visionAngle / 2));
        Line2D secondConeLine = new Line2D.Double(pose, pose.getPositionInDirection(visionRange, rotation + visionAngle / 2));
        if (firstConeLine.ptSegDist(center) <= distance || secondConeLine.ptSegDist(center) <= distance) return true;
        // a point on the vision cone is in distance
        // with points taken every degree
        for (double i = rotation - visionAngle / 2; i <= rotation + visionAngle / 2; i += Math.toRadians(1)) {
            Position position = pose.getPositionInDirection(visionRange, i);
            if (position.getEuclideanDistance(center) <= distance) return true;
        }
        return false;
    }

    /**
     * Returns true if either the robot is inside Square
     * Or the Vision range reaches the Square
     *
     * @param center Position
     * @param width  Position
     * @param height Position
     * @return boolean
     */
    public boolean isRectangleInSight(Position center, double width, double height) {
        double rotation = pose.getRotation();
        Rectangle2D rectangle2D = new Rectangle2D.Double(center.getX() - width / 2, center.getY() - height / 2, width, height);
        // if body is square
        if (rectangle2D.contains(getClosestPositionInEntity(center))) return true;
        // one side of the vision cone is inside box
        Line2D firstConeLine = new Line2D.Double(pose, pose.getPositionInDirection(visionRange, rotation - visionAngle / 2));
        Line2D secondConeLine = new Line2D.Double(pose, pose.getPositionInDirection(visionRange, rotation + visionAngle / 2));
        if (firstConeLine.intersects(rectangle2D) || secondConeLine.intersects(rectangle2D)) return true;
        // a point on the vision cone is inside the wall
        // with points taken every degree
        for (double i = rotation - visionAngle / 2; i <= rotation + visionAngle / 2; i += Math.toRadians(1)) {
            Position position = pose.getPositionInDirection(visionRange, i);
            if (rectangle2D.contains(position)) return true;
        }
        return false;
    }


    /**
     * Creates a list of entities with given class
     *
     * @param c Class
     * @return List<Object>
     */
    List<Object> getListOfRobotsInVisionByCLass(Class c) {
        return getListOfRobotsInSight().stream()
                .filter(x -> c.isAssignableFrom(x.getClass()))
                .map((Function<RobotInterface, Object>) (c)::cast)
                .collect(Collectors.toList());
    }

    //getter
    public double getVisionAngle() {
        return visionAngle;
    }

    public double getVisionRange() {
        return visionRange;
    }

    @Override
    public String toString() {
        return "Base vision robot at " + pose + " radius:" + getRadius();
    }
}
