package model.RobotTypes;

import model.*;
import model.AbstractModel.Entity;
import model.AbstractModel.RobotInterface;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class LightConeRobot extends BaseRobot {

    private final double visionRange, visionAngle;

    /**
     * Constructs object via Builder
     *
     * @param builder     RobotBuilder
     * @param visionRange double
     * @param visionAngle double
     */
    public LightConeRobot(RobotBuilder builder, double visionRange, double visionAngle) {
        super(builder);
        this.visionRange = visionRange;
        visionAngle = visionAngle > 360 ? 360 : visionAngle < 0 ? 0 : visionAngle;
        this.visionAngle = Math.toRadians(visionAngle);
    }

    /**
     * Returns true if the the bounds are in sight
     *
     * @return boolean
     */
    boolean isArenaBoundsInVision() {
        double rotation = pose.getRotation();
        for (double i = rotation - visionAngle / 2; i <= rotation + visionAngle / 2; i += Math.toRadians(2)) {
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
    List<Entity> listOfEntityInVision() {
        List<Entity> entityList = new LinkedList<>();
        entityList.addAll(listOfBoxesInSight());
        entityList.addAll(listOfWallsInSight());
        entityList.addAll(listOfAreasInSight());
        entityList.addAll(listOfRobotsInSight());
        return entityList;
    }

    /**
     * Returns all Areas in the current vision
     * @return List<Area>
     */
    List<Area> listOfAreasInSight() {
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
     * @return List<Area>
     */
    List<Area> listOfAreasInSightByAreaNoticeableDistance() {
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
     * @return List<RobotInterface>
     */
    List<RobotInterface> listOfRobotsInSight() {
        List<RobotInterface> entityList = new LinkedList<>();
        for (RobotInterface robotInterface : arena.getRobots()) {
            if (isCircleInSight(robotInterface.getPose(),robotInterface.getRadius())) {
                entityList.add(robotInterface);
            }
        }
        return entityList;
    }

    /**
     * Returns all Boxes in the current vision
     * @return List<Entity>
     */
    List<Box> listOfBoxesInSight() {
        List<Box> entityList = new LinkedList<>();
        for (Box box : arena.getBoxList()) {
            if (isSquareInSight(box.getPose(), box.getWidth(),box.getHeight())) {
                entityList.add(box)  ;
            }
        }
        return entityList;
    }

    /**
     * Returns all Walls in the current vision
     * @return List<Entity>
     */
    List<Wall> listOfWallsInSight() {
        List<Wall> entityList = new LinkedList<>();
        for (Wall wall : arena.getWallList()) {
            if (isSquareInSight(wall.getPose(), wall.getWidth(),wall.getHeight())) {
                entityList.add(wall) ;
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
            position = arena.getClosestPositionInTorus(pose, position);
        }
        if (pose.getEuclideanDistance(position) <= visionRange) {
            return isInBetween(position);
        }
        return false;
    }

    private boolean isInBetween(Position position) {
        double angleOfEntity = pose.getAngleForPosition(position) < 0 ? pose.getAngleForPosition(position) + 2 * Math.PI : pose.getAngleForPosition(position);
        double upperAngle = pose.getRotation() + visionAngle / 2;
        double lowerAngle = pose.getRotation() - visionAngle / 2;
        if (angleOfEntity <= upperAngle && angleOfEntity >= lowerAngle)
            return true;
        else if (upperAngle > 2 * Math.PI || lowerAngle < 0) {
            if (upperAngle > 2 * Math.PI && angleOfEntity < upperAngle % (2 * Math.PI)) {
                return true;
            } else if (lowerAngle < 0 && angleOfEntity > lowerAngle + 2 * Math.PI) {
                return true;
            }
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
    public boolean isSquareInSight(Position center, double width, double height) {
        double rotation = pose.getRotation();
        Rectangle2D rectangle2D = new Rectangle2D.Double(center.getX(), center.getY() - height / 2, width, height);
        System.out.println("center x" + rectangle2D.getCenterX()+" y" + rectangle2D.getCenterY());
        System.out.println("top left x" + (rectangle2D.getCenterX() - height / 2) +" y" + (rectangle2D.getCenterY() +width /2));
        System.out.println("top right x" +( rectangle2D.getCenterX() + height / 2)+" y" + (rectangle2D.getCenterY()+width /2));
        System.out.println("bottom left x" +( rectangle2D.getCenterX()- height / 2)+" y" + (rectangle2D.getCenterY()-width /2));
        System.out.println("bottom right x" +( rectangle2D.getCenterX()+ height / 2)+" y" + (rectangle2D.getCenterY()-width /2));
        System.out.println();
        // if body is square
        if (rectangle2D.contains(getClosestPositionInEntity(center))) return true;
        // one side of the vision cone is inside box
        Line2D firstConeLine = new Line2D.Double(pose, pose.getPositionInDirection(visionRange, rotation - visionAngle / 2));
        Line2D secondConeLine = new Line2D.Double(pose, pose.getPositionInDirection(visionRange, rotation + visionAngle / 2));
     //   System.out.println((firstConeLine.intersects(rectangle2D) || secondConeLine.intersects(rectangle2D)));
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
    List<Object> listOfRobotsInVisionByCLass(Class c) {
        return listOfRobotsInSight().stream()
                .filter(x -> c.getClass().isAssignableFrom(x.getClass()))
                .map(x -> (c.cast(x)))
                .collect(Collectors.toList());
    }

    //getter
    public double getVisionAngle() {
        return visionAngle;
    }

    public double getVisionRange() {
        return visionRange;
    }
}
