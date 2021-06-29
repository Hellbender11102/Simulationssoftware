package model.RobotTypes;

import model.AbstractModel.Entity;
import model.AbstractModel.RobotInterface;
import model.Area;
import model.Position;
import model.RobotBuilder;
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
        for (Entity entity : arena.getEntityList()) {
            if (!equals(entity)) {
                Position closest = entity.getClosestPositionInEntity(pose);
                if (Area.class.isAssignableFrom(entity.getClass())) {
                    if (isAreaVisionRangeInSight((Area) entity)) {
                        entityList.add(entity);
                    }
                } else if (isPositionInVisionCone(closest)) {
                    entityList.add(entity);
                }
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
        return isCircleInSight(area.getPose(),area.getNoticeableDistance());
    }

    /**
     * Returns true if either the robot is inside the Area
     * Or the Vision range reaches the Area
     *
     * @param area Area
     * @return boolean
     */
    public boolean isAreaInSight(Area area) {
       return isCircleInSight(area.getPose(),area.getWidth());
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
        double angleToRobot = getPose().getAngleForPosition(center);
        if (angleToRobot >= getVisionAngle() / 2 && angleToRobot <= getVisionAngle() / 2)
            return pose.getEuclideanDistance(center) < distance + visionRange;
        return pose.getEuclideanDistance(center) < distance + getRadius() / 2;
    }


    /**
     * @return List<RobotInterface>
     */
    List<RobotInterface> listOfRobotsInVision() {
        return listOfEntityInVision().stream()
                .filter(x -> RobotInterface.class.isAssignableFrom(x.getClass()))
                .map(x -> (RobotInterface) x)
                .collect(Collectors.toList());
    }

    /**
     * Creates a list of entities with given class
     *
     * @param c Class
     * @return List<Object>
     */
    List<Object> listOfRobotsInVisionByCLass(Class c) {
        return listOfEntityInVision().stream()
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
