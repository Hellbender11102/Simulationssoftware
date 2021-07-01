import model.RobotBuilder;
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

    final int max = 10;
    final int min = -10;
    private final int rounds;
    private final BaseRobot baseRobot;

    public BaseRobot creatTestBaseRobot(int arenaWidth, int arenaHeight, double engineR, double engineL,
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
                .buildDefault();
    }

    public BaseRobotTest(double engineRight, double engineLeft, int rounds) {
        this.rounds = rounds;
        baseRobot = creatTestBaseRobot(1000, 1000, engineRight,
                engineLeft, 1, 5, 10, 10, 0);
        if(baseRobot.getPaused()) baseRobot.togglePause();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 1, 1},
                {0, 0, 1},
                {1, 0, 1},
                {0, 1, 1},
                {1, 0, 1000},
                {0, 1, 1000},
                {1, 0.9, 1},
                {0.9, 1, 1},
                {.1, 0, 1},
                {.1, .1, 10000},
                {0, .1, 1},
                {.1, 0, 1000},
                {0, .1, 1000},
                {.5, 0, 1000},
                {7, 4, 1000},
                {2, 9, 10000},
                {0, .5, 1000},
                {-1, -1, 1000},
                {-11, -1, 100},
                {-1, 0, 1000},
                {10, 10, 100},
                {-10, -10, 100},
                {11, 11, 100},
                {-11, -11, 100},
                {5, 5, 100},
                {-5, -5, 100},
        });
    }

    /**
     * Tests if the robot will stand on the correct position with given distance after driving
     * Also checks if when driven backwards position is correct
     */
    @Test
    public void testDriveStraight() {
        double max = Math.max(baseRobot.getEngineL(), baseRobot.getEngineR());
        baseRobot.setEngines(max, max);
        double speed = baseRobot.getTrajectoryMagnitude();
        Position position;
        if (speed != 0) {
            if (max > 0) {
                position = baseRobot.getPose().getPositionInDirection(rounds);
                for (int i = 0; i < rounds / speed; i++) {
                    baseRobot.setNextPosition();
                }
            } else {
                position = baseRobot.getPose().getPositionInDirection(-rounds);
                for (int i = 0; i > rounds / speed; i--) {
                    baseRobot.setNextPosition();
                }
            }
            Assert.assertTrue(Math.round(baseRobot.getPose().getX()) == Math.round(position.getX()) &&
                    Math.round(baseRobot.getPose().getY()) == Math.round(position.getY()));
        }
    }

    /**
     * Tests if the robot will stay on the same position after driving in circles
     */
    @Test
    public void testDriveCircle() {
        double degree = baseRobot.angularVelocity();
        Position position = baseRobot.getPose().clone();
        for (double i = 0; i < rounds * 2 * Math.PI && i > rounds * 2 * Math.PI; i += degree) {
            baseRobot.run();
        }
        Assert.assertTrue(Math.round(baseRobot.getPose().getX()) == Math.round(position.getX()) &&
                Math.round(baseRobot.getPose().getY()) == Math.round(position.getY()));
    }

    /**
     * Tests if the distance between the destination and the robot gets less
     * if getTrajectoryMagnitude is negative it will test if the robot increased its distance
     */
    @Test
    public void testDriveToPosition() {
        double max = Math.max(baseRobot.getEngineL(), baseRobot.getEngineR());
        if (max != 0) {
            Position position = new Position(500, 500);
            double speed = baseRobot.getTrajectoryMagnitude();
            double distance = position.getEuclideanDistance(baseRobot.getPose());
            for (int i = 0; i < rounds / speed; i++) {
                baseRobot.driveToPosition(position, 1, max);
                baseRobot.setNextPosition();
            }
            if (max > 0)
                Assert.assertTrue(distance > position.getEuclideanDistance(baseRobot.getPose()));
            else
                Assert.assertTrue(distance <= position.getEuclideanDistance(baseRobot.getPose()));
        }
    }

    /**
     * Tests if the move random will change the position
     */
    @Test
    public void testMoveRandom() {
        double speed = baseRobot.getTrajectoryMagnitude();
        Pose clone = baseRobot.getPose().clone();
        if (speed != 0) {
            if (rounds / speed > 0) {
                for (int i = 0; i < rounds / speed; i++) {
                    baseRobot.moveRandom(10, speed, 45);
                    baseRobot.setNextPosition();
                }
            } else {
                for (int i = 0; i > rounds / speed; i--) {
                    baseRobot.moveRandom(10, speed, 45);
                    baseRobot.setNextPosition();
                }
            }
            Assert.assertFalse(clone.equals(baseRobot.getPose()));
        } else
            Assert.assertTrue(clone.equals(baseRobot.getPose()));
    }

    /**
     * Tests the increase Speed
     */
    @Test
    public void testIncreaseSpeed() {
        double engineL = baseRobot.getEngineL();
        double engineR = baseRobot.getEngineR();
        baseRobot.increaseSpeed(2);
        if (engineL <= min)
            Assert.assertEquals(baseRobot.getEngineL(), min / 2. + 1, 0.0);
        else if (engineL >= max)
            Assert.assertEquals(baseRobot.getEngineL(), max / 2., 0.0);
        if (engineR <= min) {
            System.out.println(baseRobot.getEngineR());
            Assert.assertEquals(baseRobot.getEngineR(), min / 2. + 1, 0.0);
        } else if (engineR >= max)
            Assert.assertEquals(baseRobot.getEngineR(), max / 2., 0.0);
    }
    /**
     * Tests the increase Speed
     */
    @Test
    public void testDecreaseSpeed() {
        double engineL = baseRobot.getEngineL();
        double engineR = baseRobot.getEngineR();
        baseRobot.setEngines(engineL,engineR);
        baseRobot.increaseSpeed(-3);
        if (engineL <= min)
            Assert.assertEquals(baseRobot.getEngineL(), min / 2., 0.0);
        else if (engineL >= max)
            Assert.assertEquals(baseRobot.getEngineL(), max / 2. -1.5, 0.0);
        if (engineR <= min) {
            Assert.assertEquals(baseRobot.getEngineR(), min / 2., 0.0);
        } else if (engineR >= max)
            Assert.assertEquals(baseRobot.getEngineR(), max / 2. -1.5, 0.0);
    }

    @Test
    public void testTurn() {
     double currentOrientation = baseRobot.getPose().getRotation();
        while(!baseRobot.turn(currentOrientation+Math.PI,4,0)){
            baseRobot.setNextPosition();
        }
        Assert.assertEquals(currentOrientation+Math.PI,baseRobot.getPose().getRotation());
    }

    @Test
    public void testSetEngines() {
        double engineL = baseRobot.getEngineL();
        double engineR = baseRobot.getEngineR();
        baseRobot.setEngines(engineL,engineR);
        if (engineL <= min)
            Assert.assertEquals(baseRobot.getEngineL(), min / 2., 0.0);
        else if (engineL >= max)
            Assert.assertEquals(baseRobot.getEngineL(), max / 2. -1.5, 0.0);
        if (engineR <= min) {
            System.out.println(baseRobot.getEngineR());
            Assert.assertEquals(baseRobot.getEngineR(), min / 2., 0.0);
        } else if (engineR >= max)
            Assert.assertEquals(baseRobot.getEngineR(), max / 2. -1.5, 0.0);
    }

}
