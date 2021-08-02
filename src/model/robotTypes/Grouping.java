package model.robotTypes;

import helper.RobotBuilder;

import java.awt.*;

public class Grouping extends BaseRobot {

    public Grouping(RobotBuilder builder) {
        super(builder);
    }

    int i = 0;

    @Override
    public void behavior() {
        stayGroupedWithAllRobots(10, 8);
        /*
          Functional logging code
          it will log once per simulated second
          it will record x- and y-coordinates
          also for the thread with id 16 it keeps track of the group center

        if (i++ % (ticsPerSimulatedSecond / 20) == 0) {
            logger.logDouble(getId() + "Distant closest", distanceToClosestEntityOfClass(List.of(getClass())), 2);
            logger.logDouble("y" + getId(), pose.getY(), 2);
            logger.logDouble("x" + getId(), pose.getX(), 2);
            if (getId() == 16) {
                Position groupCenter = centerOfGroupWithClasses(List.of(RobotInterface.class));
                logger.logDouble("centerX", groupCenter.getX(), 1);
                logger.logDouble("centerY", groupCenter.getY(), 1);
            }
        }
        */
    }


    @Override
    public Color getClassColor() {
        return Color.BLUE;
    }

}