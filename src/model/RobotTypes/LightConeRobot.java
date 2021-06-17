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
                double angel = pose.calcAngleForPosition(closest) < 0 ? pose.calcAngleForPosition(closest) + 2*Math.PI:pose.calcAngleForPosition(closest) ;
                if (angel <= pose.getRotation() + visionAngle / 2 &&
                        angel >= pose.getRotation() - visionAngle / 2 &&
                        pose.euclideanDistance(closest) <= visionRange)
                    entityList.add(entity);
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
