import helper.Logger;
import model.abstractModel.RobotInterface;
import model.Arena;
import model.Pose;
import helper.RobotBuilder;
import model.robotTypes.BaseVisionConeRobot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

@RunWith(Parameterized.class)
public class BuilderTest {

    private final RobotBuilder robotBuilder;
    private final double engineL, engineR, max, min;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 10, 10, 1, Math.PI * 3},
                {1, 1, 1, 1, 1, 1, Math.PI * 3, 10, 10, 1, 1},
                {10, 10, 1, 1, 1, 1, 1, 10, 10, 1, 1},
                {-1, 11, 1, 1, 1, 1, 1, 10, -10, 1, 1},
                {-9, -11, 1, 1, 1, 1, 1, 10, -10, 1, 1},
        });
    }


    public BuilderTest(double engineR, double engineL, double engineDistance, double diameters, double poseX,
                       double poseY, double poseRotation, double max, double min, double visionRange, double visionAngle) {
        Arena arena = Arena.getInstance(100, 100, false);

        this.engineL = engineL;
        this.engineR = engineR;
        this.max = max;
        this.min = min;


        robotBuilder = new RobotBuilder()
                .arena(arena)
                .diameters(diameters)
                .engineDistance(engineDistance)
                .powerTransmission(0)
                .random(new Random())
                .engineLeft(engineL)
                .engineRight(engineR)
                .timeToSimulate(0)
                .minSpeed(min)
                .maxSpeed(max)
                .ticsPerSimulatedSecond(10)
                .logger(new Logger())
                .pose(new Pose(poseX, poseY, poseRotation))
                .visionAngle(visionAngle)
                .visionRange(visionRange);
    }

    @Test
    public void testBuildDefault() {
        RobotInterface robot = robotBuilder.buildDefault();

        if (engineL >= max)
            Assert.assertEquals(robot.getEngineL(), max, 0);
        else if (engineL <= min)
            Assert.assertEquals(robot.getEngineL(), min, 0);
        else Assert.assertEquals(robot.getEngineL(), engineL, 0);
        if (engineR >= max)
            Assert.assertEquals(robot.getEngineR(), max, 0);
        else if (engineR <= min)
            Assert.assertEquals(robot.getEngineR(), max, 0);
        else Assert.assertEquals(robot.getEngineR(), engineR, 0);

        if(robotBuilder.getDiameters() >= 0 ) {
            Assert.assertEquals(robot.getHeight(), robotBuilder.getDiameters(), 0);
            Assert.assertEquals(robot.getWidth(), robotBuilder.getDiameters(), 0);
        }   else{
            Assert.assertEquals(robot.getHeight(),0, 0);
            Assert.assertEquals(robot.getWidth(), 0, 0);
        }
        if(robotBuilder.getDiameters() > 0 )
        Assert.assertEquals(robot.getDiameters(), robotBuilder.getDiameters(), 0);
        Assert.assertEquals(robot.getRadius() * 2, robotBuilder.getDiameters(), 0);

        if(robotBuilder.getPose().getRotation() >=Math.PI * 2)
            Assert.assertEquals(robot.getPose().getRotation(),Math.PI * 2,0);
        else if(robotBuilder.getPose().getRotation() <=0)
            Assert.assertEquals(robot.getPose().getRotation(),0,0);
        else
            Assert.assertEquals(robot.getPose().getRotation(),robotBuilder.getPose().getRotation(),0);
    }


    @Test
    public void testBuildVision() {
        BaseVisionConeRobot robot = robotBuilder.buildVisionCone();
        if (robotBuilder.getVisionAngle() <= 0)
            Assert.assertEquals(robot.getVisionAngle(), 0, 0);
        else if ( Math.toRadians(robotBuilder.getVisionAngle()) >= Math.PI * 2)
            Assert.assertEquals(robot.getVisionAngle(), Math.PI * 2, 0);
        else
            Assert.assertEquals(robot.getVisionAngle(), Math.toRadians(robotBuilder.getVisionAngle()), 0);

        if (robotBuilder.getVisionRange() <= 0) Assert.assertEquals(robot.getVisionRange(), 0, 0);
        else Assert.assertEquals(robot.getVisionRange(), robotBuilder.getVisionRange(), 0);
    }


}
