
import controller.Logger;
import model.*;
import model.AbstractModel.BaseEntity;
import model.AbstractModel.PhysicalEntity;
import model.RobotTypes.BaseRobot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.awt.*;
import java.util.*;

@RunWith(Enclosed.class)
public class ArenaTest {


    @RunWith(org.junit.runners.Parameterized.class)
    public static class Parameterized {
        private final Arena arena;
        private Position position;
        private final boolean result;

        public Parameterized(int width, int height, boolean torus, double poseX, double poseY, boolean result) {
            arena = Arena.overWriteInstance(width, height, torus);
            position = new Position(poseX, poseY);
            this.result = result;
        }


        @org.junit.runners.Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {0, 0, false, 0, 0, true},
                    {0, 0, false, 1, 1, false},
                    {0, 0, false, -1, -1, false},
                    {0, 0, false, -1, 0, false},
                    {0, 0, false, 0, -1, false},
                    {0, 0, false, 1, 0, false},
                    {0, 0, false, 0, 1, false},
                    {10, 10, false, 5, 5, true},
                    {10, 10, false, 1, 1, true},
                    {10, 10, false, -1, -1, false},
                    {10, 10, false, -1, 0, false},
                    {10, 10, false, 0, -1, false},
                    {10, 10, false, 10.1, 9, false},
                    {10, 10, false, 9, 10.1, false},
                    {10, 10, false, 0, -1, false},
                    {10, 10, false, 1, 0, true},
                    {10, 10, false, 0, 1, true},
                    {10, 10, false, 10, 0, true},
                    {10, 10, false, 0, 10, true},
                    {10, 10, false, 10, 10, true},
            });
        }


        @Test
        public void testInArenaBounds() {
            Assert.assertEquals(arena.inArenaBounds(position), result);
        }

        @Test
        public void testSetPositionInBoundsTorus() {
            Position changed = arena.setPositionInBoundsTorus(position);
            Assert.assertTrue(arena.inArenaBounds(changed));
            if (position.getY() > arena.getHeight() && 0 > arena.getHeight())
                Assert.assertEquals(changed.getY(), position.getY() - arena.getWidth(), .01);
            else if (position.getY() < 0 && 0 > arena.getHeight())
                Assert.assertEquals(changed.getY(), arena.getWidth() - position.getY(), .01);
            else if (0 > arena.getHeight())
                Assert.assertEquals(position.getY(), changed.getY(), 0);
            if (position.getX() > arena.getWidth() && 0 > arena.getWidth())
                Assert.assertEquals(changed.getX(), position.getX() - arena.getWidth(), .01);
            else if (position.getX() < 0 && 0 > arena.getHeight())
                Assert.assertEquals(changed.getX(), arena.getWidth() - position.getX(), .01);
            else if (0 > arena.getHeight())
                Assert.assertEquals(position.getX(), changed.getX(), 0);
            if (0 == arena.getWidth())
                Assert.assertEquals(changed.getX(), 0, 0);
            if (0 == arena.getHeight())
                Assert.assertEquals(changed.getY(), 0, 0);
        }

        @Test
        public void testSetPositionInBounds() {
            Position changed = arena.setPositionInBounds(position);
            Assert.assertTrue(arena.inArenaBounds(changed));
            if (position.getY() > arena.getHeight())
                Assert.assertEquals(changed.getY(), arena.getHeight(), 0);
            else if (position.getY() < 0)
                Assert.assertEquals(changed.getY(), 0, 0);
            else
                Assert.assertEquals(position.getY(), changed.getY(), 0);
            if (position.getX() > arena.getWidth())
                Assert.assertEquals(changed.getX(), arena.getWidth(), 0);
            else if (position.getX() < 0)
                Assert.assertEquals(changed.getX(), 0, 0);
            else
                Assert.assertEquals(position.getX(), changed.getX(), 0);
        }

        @Test
        public void testGetClosestPositionInTorus() {
            Position positionUpperLeft = new Position(0, arena.getHeight());
            Position positionUpperRight = new Position(arena.getWidth(), arena.getHeight());
            Position positionLowerLeft = new Position(0, 0);
            Position positionLowerRight = new Position(arena.getWidth(), 0);
            Position closest1 = arena.getClosestPositionInTorus(positionUpperLeft, positionUpperRight);
            Position closest2 = arena.getClosestPositionInTorus(positionLowerLeft, positionLowerRight);

            if (arena.getWidth() > 0 || arena.getHeight() > 0) {
                Assert.assertTrue(positionUpperLeft.getEuclideanDistance(positionUpperRight)
                        > positionUpperLeft.getEuclideanDistance(closest1));
                Assert.assertTrue(positionLowerLeft.getEuclideanDistance(positionLowerRight)
                        > positionLowerLeft.getEuclideanDistance(closest2));
            }
        }

        @Test
        public void testGetEuclideanDistanceToClosestPosition() {
            Position positionUpperLeft = new Position(0, arena.getHeight());
            Position positionUpperRight = new Position(arena.getWidth(), arena.getHeight());
            Position positionLowerLeft = new Position(0, 0);
            Position positionLowerRight = new Position(arena.getWidth(), 0);
            if (arena.getWidth() > 0 || arena.getHeight() > 0) {
                Assert.assertTrue(positionUpperLeft.getEuclideanDistance(positionUpperRight)
                        > arena.getEuclideanDistanceToClosestPosition(positionUpperLeft, positionUpperRight));
                Assert.assertTrue(positionLowerLeft.getEuclideanDistance(positionLowerRight)
                        > arena.getEuclideanDistanceToClosestPosition(positionLowerLeft, positionLowerRight));
            }
        }
    }

    public static class NotParameterizedPart {
        @Test
        public void testEntityManagement() {
            Arena arena = Arena.overWriteInstance(0, 0, false);
            BaseEntity area = new Area(arena, new Random(), 2, 2, new Pose(1, 1, 1));
            BaseEntity robot = new BaseRobot(0, 0, 0, 0, 0,
                    0, 0, new Logger(), 100, true, arena, new Random(), new Pose(1, 1, 1), 0) {
                @Override
                public void behavior() {
                }

                @Override
                public Color getClassColor() {
                    return null;
                }
            };
            PhysicalEntity box = new Box(arena, new Random(), 2, 2, new Pose(1, 1, 1), 1);
            PhysicalEntity wall = new Wall(arena, new Random(), 2, 2, new Pose(1, 1, 1), 1);
            arena.addEntity(area);
            arena.addEntity(robot);
            arena.addEntity(box);
            arena.addEntity(wall);
            Assert.assertEquals(arena.getEntityList().size(), 4);
            Assert.assertEquals(arena.getAreaList().size(), 1);
            Assert.assertEquals(arena.getAreaList().get(0), area);
            Assert.assertEquals(arena.getRobots().size(), 1);
            Assert.assertEquals(arena.getRobots().get(0), robot);
            Assert.assertEquals(arena.getBoxList().size(), 1);
            Assert.assertEquals(arena.getBoxList().get(0), box);
            Assert.assertEquals(arena.getWallList().size(), 1);
            Assert.assertEquals(arena.getWallList().get(0), wall);
            Assert.assertEquals(arena.getPhysicalEntityList().size(), 3);
            Assert.assertEquals(arena.getPhysicalEntitiesWithoutRobots().size(), 2);
        }
    }
}
