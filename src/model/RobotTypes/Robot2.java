package model.RobotTypes;

import model.AbstractModel.RobotInterface;
import model.Position;
import model.RobotBuilder;

import java.awt.*;
import java.util.List;

public class Robot2 extends BaseRobot {

    public Robot2(RobotBuilder builder) {
        super(builder);
    }

    @Override
    public void behavior() {
        stayGroupedWithAllRobots(15, 8);
        if (getTimeToSimulate() % (ticsPerSimulatedSecond / 2) == 0) {
            logger.logDouble("x" + getId(), pose.getX(), 2);
            logger.logDouble("y" + getId(), pose.getY(), 2);
            logger.logDouble("closest" + getId(), distanceToClosestEntityOfClass(java.util.List.of(RobotInterface.class)), 2);
            Position center = arena.getRobots().get(0).centerOfGroupWithClasses(List.of(RobotInterface.class));
            if (getId() == 18) {
                logger.logDouble("centerX", center.getX(), 2);
                logger.logDouble("centerY", center.getY(), 2);
            }
        }
    }

    @Override
    public Color getClassColor() {
        return Color.YELLOW;
    }

}