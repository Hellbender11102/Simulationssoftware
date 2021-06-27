package model.RobotTypes;

import model.AbstractModel.Entity;
import model.AbstractModel.RobotInterface;
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
     * @param builder RobotBuilder
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
     * @return boolean
     */
    boolean isArenaBoundsInVision() {
        double rotation = pose.getRotation();
        boolean inSight = false;
        for (double i = rotation - visionAngle / 2; i <= rotation + visionAngle / 2; i += Math.toRadians(2)) {
            Position position = pose.getPositionInDirection(visionRange, i);
            if (!arena.inArenaBounds(position))
                inSight = true;
        }
        return inSight;
    }

    /**
     * Returns euclidean distance to arena bounds in vision
     * If none returns -1
     *
     * @return double
     */
    double distanceToArenaBoundsInVision() {
        if (arena.isTorus || !isArenaBoundsInVision())
            return -1;
        double smallest = visionRange;
        double rotation = pose.getRotation();
        for (double i = rotation - visionAngle / 2; i <= rotation + visionAngle / 2; i += Math.toRadians(0.5)) {
            Position position = new Position(0, 0);
            double x = pose.getX(), y = pose.getY();

            x += Math.cos(rotation) ;
            y += Math.sin(rotation) ;
            position.setX(x);
            position.setY(y);
            System.out.println(position + " " + smallest);
            smallest = Math.min(pose.getEuclideanDistance(position), smallest);
        }

        return smallest;
    }

    /**
     * Gets all entities in the current vision
     * @return List<Entity>
     */
    List<Entity> listOfEntityInVision() {
        List<Entity> entityList = new LinkedList<>();
        for (Entity entity : arena.getEntityList()) {
            if (!equals(entity)) {
                Position closest = entity.getClosestPositionInEntity(pose);
                if (arena.isTorus) {
                    closest = arena.getClosestPositionInTorus(pose, closest);
                }
                if (pose.getEuclideanDistance(closest) <= visionRange) {
                    double angleOfEntity = pose.getAngleForPosition(closest) < 0 ? pose.getAngleForPosition(closest) + 2 * Math.PI : pose.getAngleForPosition(closest);
                    double upperAngle = pose.getRotation() + visionAngle / 2;
                    double lowerAngle = pose.getRotation() - visionAngle / 2;
                    if (angleOfEntity <= upperAngle && angleOfEntity >= lowerAngle)
                        entityList.add(entity);
                    else if (upperAngle > 2 * Math.PI || lowerAngle < 0) {
                        if (upperAngle > 2 * Math.PI && angleOfEntity < upperAngle % (2 * Math.PI)) {
                            entityList.add(entity);
                        } else if (lowerAngle < 0 && angleOfEntity > lowerAngle + 2 * Math.PI) {
                            entityList.add(entity);
                        }
                    }
                }
            }
        }
        return entityList;
    }

    /**
     *
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
