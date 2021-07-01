import model.*;
import model.AbstractModel.RobotInterface;
import model.RobotTypes.BaseRobot;
import model.RobotTypes.BaseVisionConeRobot;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Random;

@RunWith(Parameterized.class)
public class VisionConeRobotTest {

    final int max = 10;
    final int min = -10;

    private final int rounds;
    private final RobotInterface robot;

    public RobotInterface creatTestBaseRobot(int arenaWidth, int arenaHeight, double engineR, double engineL,
                                             double engineDistance, double diameters, double poseX, double poseY,
                                             double poseRotation) {
        return new RobotBuilder()
                .arena(Arena.getInstance(arenaWidth, arenaHeight, false))
                .diameters(diameters)
                .engineDistnace(engineDistance)
                .powerTransmission(0)
                .random(new Random())
                .engineLeft(engineL)
                .engineRight(engineR)
                .timeToSimulate(0)
                .minSpeed(min)
                .maxSpeed(max)
                .ticsPerSimulatedSecond(10)
                .pose(new Pose(poseX, poseY, poseRotation))
                .visionCone();
    }

    public VisionConeRobotTest(double engineRight, double engineLeft, int rounds) {
        this.rounds = rounds;
        robot = creatTestBaseRobot(1000, 1000, engineRight,
                engineLeft, 1, 5, 10, 10, 0);
    }

}
