package model.RobotTypes;

import model.AbstractModel.Entity;
import model.AbstractModel.RobotInterface;
import model.Position;
import model.RobotBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class LightConeRobot extends BaseRobot {

    final double visionRange, visionAngle;

    /**
     * Constructs object via Builder
     *
     * @param builder
     */
    public LightConeRobot(RobotBuilder builder, double visionRange, double visionAngle) {
        super(builder);
        this.visionRange = visionRange;
        visionAngle = visionAngle > 360?360:visionAngle < 0 ? 0: visionAngle;
        this.visionAngle = Math.toRadians(visionAngle);
    }

    boolean isArenaBoundsInVision() {
        Position pos1 = pose.getPositionInDirection(visionRange, pose.getRotation() + visionAngle / 2);
        Position pos2 = pose.getPositionInDirection(visionRange, pose.getRotation() - visionAngle / 2);
        Position pos3 = pose.getPositionInDirection(visionRange, pose.getRotation());
        return arena.inArenaBounds(pos1) && arena.inArenaBounds(pos2) && arena.inArenaBounds(pos3);
    }

    double distanceToArenaBounds() {
        return 0;
    }

    List<Entity> listOfEntityInVision() {
        List<Entity> entityList = new LinkedList<>();
        for (Entity entity : arena.getEntityList()) {
            if (!equals(entity)) {
                Position closest = entity.getClosestPositionInEntity(pose);
                if (pose.euclideanDistance(closest) <= visionRange) {
                    double angleOfEntity = pose.calcAngleForPosition(closest) < 0 ? pose.calcAngleForPosition(closest) + 2 * Math.PI : pose.calcAngleForPosition(closest);
                    double upperAngle = pose.getRotation() + visionAngle / 2;
                    double lowerAngle = pose.getRotation() - visionAngle / 2;
                    if (angleOfEntity <= upperAngle && angleOfEntity >= lowerAngle)
                        entityList.add(entity);
                    else if (upperAngle > 2 * Math.PI || lowerAngle < 0) {
                        if (upperAngle > 2 * Math.PI && angleOfEntity < upperAngle %(2 * Math.PI)) {
                            entityList.add(entity);
                        }
                       else if (lowerAngle < 0 && angleOfEntity > lowerAngle + 2 * Math.PI) {
                            entityList.add(entity);
                        }
                    }
                }
            }
        }
        return entityList;
    }

    List<RobotInterface> listOfRobotsInVision() {
        return listOfEntityInVision().stream()
                .filter(x -> RobotInterface.class.isAssignableFrom(x.getClass()))
                .map(x -> (RobotInterface) x)
                .collect(Collectors.toList());
    }

    List<Object> listOfRobotsInVisionByCLass(Class type) {
        return listOfEntityInVision().stream()
                .filter(x -> type.getClass().isAssignableFrom(x.getClass()))
                .map(x -> (type.cast(x)))
                .collect(Collectors.toList());
    }

    public double getVisionAngle() {
        return visionAngle;
    }

    public double getVisionRange() {
        return visionRange;
    }
}
