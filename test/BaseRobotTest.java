import model.AbstractModel.EntityBuilder;
import model.Arena;
import model.Pose;
import model.Position;
import model.RobotTypes.BaseRobot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

@RunWith(Parameterized.class)
public class BaseRobotTest {

    public BaseRobot creatTestBaseRobot(int arenaWidth, int arenaHeight, double engineR, double engineL,
                                        double engineDistance, double diameters, double poseX, double poseY,
                                        double poseRotation) {
        return new EntityBuilder()
                .arena(Arena.getInstance(arenaWidth, arenaHeight))
                .diameters(diameters)
                .engineDistnace(engineDistance)
                .powerTransmission(0)
                .random(new Random())
                .engineLeft(engineL)
                .engineRight(engineR)
                .pose(new Pose(poseX, poseY, poseRotation))
                .buildDefault();
    }

    public BaseRobotTest(double engineRight, double engineLeft, int rounds) {
        this.rounds = rounds;
        baseRobot = creatTestBaseRobot(1000, 1000, engineRight,
                engineLeft, 1, 5, 10, 10, 0);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 1, 1},
                {1, 0, 1},
                {0, 1, 1},
                {1, 0, 1000},
                {0, 1, 1000},
                {1, 0.9, 1},
                {0.9, 1, 1},
                {.1, 0, 1},
                {0, .1, 1},
                {.1, 0, 10000},
                {0, .1, 10000},
                {.5, 0, 10000},
                {0, .5, 10000},
        });
    }

    private final int rounds;
    private final BaseRobot baseRobot;

    @Test
    public void testDriveStraight() {
        double max = Math.max(baseRobot.getEngineL(), baseRobot.getEngineR());
        baseRobot.setEngines(max, max);
        double speed = baseRobot.trajectorySpeed();
        Position position = baseRobot.getPose().getPositionInDirection(rounds);
        for (int i = 0; i < rounds / speed; i++) {
            baseRobot.setNextPosition();
        }
        Assert.assertTrue(Math.round(baseRobot.getPose().getXCoordinate()) == Math.round(position.getXCoordinate()) &&
                Math.round(baseRobot.getPose().getYCoordinate()) == Math.round(position.getYCoordinate()));
    }

    @Test
    public void testDriveCircle() {
        double degree = baseRobot.angularVelocity();
        Position position = baseRobot.getPose().getPositionInDirection(0);
        for (double i = 0; i < rounds * 2 * Math.PI && i > rounds * 2 * Math.PI; i += degree) {
            baseRobot.setNextPosition();
            System.out.println(i);
            System.out.println(degree);
        }
        Assert.assertTrue(Math.round(baseRobot.getPose().getXCoordinate()) == Math.round(position.getXCoordinate()) &&
                Math.round(baseRobot.getPose().getYCoordinate()) == Math.round(position.getYCoordinate()));
    }
}
