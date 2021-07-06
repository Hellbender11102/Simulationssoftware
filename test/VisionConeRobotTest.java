import controller.Logger;
import model.*;
import model.AbstractModel.BaseEntity;
import model.AbstractModel.Entity;
import model.AbstractModel.PhysicalEntity;
import model.RobotTypes.BaseRobot;
import model.RobotTypes.BaseVisionConeRobot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

@RunWith(Parameterized.class)
public class VisionConeRobotTest {

    final int max = 10;
    final int min = -10;

    private double poseX;
    private final double poseY;
    private final boolean isInSight;
    private final BaseVisionConeRobot robot;
    private Arena arena;

    public BaseVisionConeRobot creatTestBaseRobot(int arenaWidth, int arenaHeight, boolean isTorus, double engineR, double engineL,
                                                  double engineDistance, double diameters, double poseX, double poseY,
                                                  double poseRotation, double visionAngle, double visionRange) {
        arena = Arena.overWriteInstance(arenaWidth, arenaHeight, isTorus);
        BaseVisionConeRobot robot = new RobotBuilder()
                .arena(arena)
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
                .visionAngle(visionAngle)
                .visionRange(visionRange)
                .buildVisionCone();
        arena.addEntity(robot);
        return robot;
    }


    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                //isTorus, isInSight,  rotation,  visionAngle,  visionRange,  poseX,  poseY,  arenaW,  arenaH
                {false, false, 0, 0, 0, 0, 0, 0, 0},
                {false, false, 0, 0, 0, 0, 0, 1, 1},

                {false, true, 0, 0, 5.01, 5, 5, 10, 10},
                {false, true, Math.PI / 2, 0, 5.01, 5, 5, 10, 10},
                {false, true, Math.PI, 0, 5.01, 5, 5, 10, 10},
                {false, true, 3 / 2 * Math.PI, 0, 5.01, 5, 5, 10, 10},
                {false, true, Math.PI * 2, 0, 5.01, 5, 5, 10, 10},

                {false, false, Math.PI / 4, Math.PI / 4, 5.01, 5, 5, 10, 10},
                {false, false, 3 / Math.PI * 4, Math.PI / 4, 5.01, 5, 5, 10, 10},
                {false, false, 5 / Math.PI * 4, Math.PI / 4, 5.01, 5, 5, 10, 10},
                {false, false, 7 / Math.PI * 4, Math.PI / 4, 5.01, 5, 5, 10, 10},

                {false, false, 0, Math.PI, 5, 10, 10, 100, 100},
                {false, false, 0, 2 * Math.PI, 5, 10, 10, 100, 100},
                {false, false, 0, Math.PI / 2, 5, 10, 10, 100, 100},

                {false, false, Math.PI, Math.PI, 5, 10, 10, 100, 100},
                {false, false, Math.PI, 2 * Math.PI, 5, 10, 10, 100, 100},
                {false, false, Math.PI, Math.PI / 2, 5, 10, 10, 100, 100},

                {false, false, Math.PI / 2, Math.PI, 5, 10, 10, 100, 100},
                {false, false, Math.PI / 2, 2 * Math.PI, 5, 10, 10, 100, 100},
                {false, false, Math.PI / 2, Math.PI / 2, 5, 10, 10, 100, 100},

                {false, false, Math.PI * 3 / 4, Math.PI, 5, 10, 10, 100, 100},
                {false, false, Math.PI * 3 / 4, 2 * Math.PI, 5, 10, 10, 100, 100},
                {false, false, Math.PI * 3 / 4, Math.PI / 2, 5, 10, 10, 100, 100},

                {false, false, -Math.PI / 2, Math.PI, 5, 10, 10, 100, 100},
                {false, false, -Math.PI / 2, 2 * Math.PI, 5, 10, 10, 100, 100},
                {false, false, -Math.PI / 2, Math.PI / 2, 5, 10, 10, 100, 100},

                {false, false, 2 * Math.PI, Math.PI, 5, 10, 10, 100, 100},
                {false, false, 2 * Math.PI, 2 * Math.PI, 5, 10, 10, 100, 100},
                {false, false, 2 * Math.PI, Math.PI / 2, 5, 10, 10, 100, 100},
        });
    }


    public VisionConeRobotTest(boolean isTorus, boolean isInSight, double rotation, double visionAngle, double visionRange, double poseX, double poseY, int arenaW, int arenaH) {
        this.poseX = poseX;
        this.poseY = poseY;
        this.isInSight = !isTorus && isInSight;
        robot = creatTestBaseRobot(arenaW, arenaH, isTorus, 0,
                0, 1, 3, poseX, poseY, rotation, visionAngle, visionRange);
    }

    @Test
    public void testIsArenaBoundsInVision() {
        Assert.assertEquals(robot.isArenaBoundsInVision(), isInSight);
    }

    @Test
    public void testListOfEntityInVision() {
        BaseEntity area = new Area(arena, new Random(), 2, 2, new Pose(poseX + 5, poseY, 1));
        BaseEntity baseRobot = new BaseRobot(0, 0, 0, 0, 0,
                0, 0, new Logger(), 100, true, arena, new Random(), new Pose(poseX - 5, poseY, 1), 0) {
            @Override
            public void behavior() {
            }

            @Override
            public Color getClassColor() {
                return null;
            }
        };
        PhysicalEntity box = new Box(arena, new Random(), 2, 2, new Pose(poseX, poseY + 5, 1), 1);
        PhysicalEntity wall = new Wall(arena, new Random(), 2, 2, new Pose(poseX, poseY - 5, 1), 1);

        arena.addEntity(area);
        arena.addEntity(baseRobot);
        arena.addEntity(box);
        arena.addEntity(wall);

        int numberOfEntitiesInVision = robot.getListOfEntityInVision().size();

        if (robot.getVisionRange() >= 5)
            if (robot.getVisionAngle() < Math.toRadians(90)) {
                Assert.assertTrue(numberOfEntitiesInVision == 0 || numberOfEntitiesInVision == 1);
            } else if (robot.getVisionAngle() <= Math.toRadians(90)) {
                Assert.assertTrue(numberOfEntitiesInVision == 1 || numberOfEntitiesInVision == 2);
            } else if (robot.getVisionAngle() <= Math.toRadians(180)) {
                Assert.assertTrue(numberOfEntitiesInVision == 2 || numberOfEntitiesInVision == 3);
            } else if (robot.getVisionAngle() <= Math.toRadians(270)) {
                Assert.assertEquals(3, numberOfEntitiesInVision);
            } else {
                Assert.assertTrue(numberOfEntitiesInVision == 3 || numberOfEntitiesInVision == 4);
            }
    }

    @Test
    public void testListOfAreasInSightByAreaNoticeableDistance() {
        for (int i = -5; i < 5; i += 5) {
            BaseEntity area1 = new Area(arena, new Random(), 0, 2, new Pose(poseX + 5, poseY + i, 1));
            BaseEntity area2 = new Area(arena, new Random(), 1, 2, new Pose(poseX + 5, poseY + i, 1));
            BaseEntity area3 = new Area(arena, new Random(), 2, 2, new Pose(poseX + 5, poseY + i, 1));
            arena.addEntity(area1);
            arena.addEntity(area2);
            arena.addEntity(area3);
        }

        int numberOfEntitiesInVision = robot.getListOfAreasInSightByAreaNoticeableDistance().size();

        if (robot.getVisionAngle() < Math.toRadians(90)) {
            if (robot.getVisionRange() < 3)
                Assert.assertEquals(0, numberOfEntitiesInVision);
            else if (robot.getVisionRange() == 3)
                Assert.assertTrue(numberOfEntitiesInVision <= 1);
            else if (robot.getVisionRange() <= 4)
                Assert.assertTrue(numberOfEntitiesInVision <= 2);
            else if (robot.getVisionRange() <= 5)
                Assert.assertTrue(numberOfEntitiesInVision <= 3);
            else Assert.assertTrue(numberOfEntitiesInVision <= 3);
        } else if (robot.getVisionAngle() <= Math.toRadians(90) || robot.getVisionAngle() <= Math.toRadians(180)) {
            if (robot.getVisionRange() < 3)
                Assert.assertEquals(0, numberOfEntitiesInVision);
            else if (robot.getVisionRange() == 3)
                Assert.assertTrue(numberOfEntitiesInVision <= 4 && numberOfEntitiesInVision >= 2);
            else if (robot.getVisionRange() <= 4)
                Assert.assertTrue(numberOfEntitiesInVision <= 4 && numberOfEntitiesInVision >= 2);
            else if (robot.getVisionRange() <= 5)
                Assert.assertTrue(numberOfEntitiesInVision <= 6 && numberOfEntitiesInVision >= 3);
            else Assert.assertTrue(numberOfEntitiesInVision <= 6 && numberOfEntitiesInVision >= 3);
        } else if (robot.getVisionAngle() <= Math.toRadians(270)) {
            if (robot.getVisionRange() < 3)
                Assert.assertEquals(0, numberOfEntitiesInVision);
            else if (robot.getVisionRange() == 3)
                Assert.assertTrue(numberOfEntitiesInVision <= 4 && numberOfEntitiesInVision >= 3);
            else if (robot.getVisionRange() <= 4)
                Assert.assertTrue(numberOfEntitiesInVision <= 8 && numberOfEntitiesInVision >= 4);
            else if (robot.getVisionRange() <= 5)
                Assert.assertTrue(numberOfEntitiesInVision <= 8 && numberOfEntitiesInVision >= 4);
            else Assert.assertTrue(numberOfEntitiesInVision <= 9 && numberOfEntitiesInVision >= 6);
        } else {
            if (robot.getVisionRange() < 3)
                Assert.assertEquals(0, numberOfEntitiesInVision);
            else if (robot.getVisionRange() == 3)
                Assert.assertEquals(4, numberOfEntitiesInVision);
            else if (robot.getVisionRange() <= 4)
                Assert.assertTrue(numberOfEntitiesInVision == 4 || numberOfEntitiesInVision == 8);
            else if (robot.getVisionRange() <= 5)
                Assert.assertTrue(numberOfEntitiesInVision == 8 || numberOfEntitiesInVision ==12);
            else Assert.assertEquals(12, numberOfEntitiesInVision);
        }
    }

    @Test
    public void testIsPositionInVisionCone() {

    }

    @Test
    public void testIsInBetween() {

    }

    @Test
    public void testisAreaVisionRangeInSight() {

    }

    @Test
    public void testCircleInSight() {

    }

    @Test
    public void testIsSquareInSight() {

    }
}
