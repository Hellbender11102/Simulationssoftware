import controller.Logger;
import model.*;
import model.AbstractModel.BaseEntity;
import model.AbstractModel.BasePhysicalEntity;
import model.AbstractModel.PhysicalEntity;
import model.RobotTypes.BaseRobot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

@RunWith(Parameterized.class)
public class BasePhysicalEntityTest {
    private final BasePhysicalEntity entity;
    private final Arena arena;
    private Position position;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 0, 0, 0, false},
                {1, 1, 0, 0, true},
                {0, 0, 1, 1, true},
                {-1, -1, 0, 0, true},
                {0, 0, -1, -1, true},
                {1, 1, 1, 1, true},
                {-1, -1, -1, -1, true},
                {1, 1, 10, 10, true},
                {10, 10, 1, 1, true},
                {1, 1, 0, 0, false},
                {0, 0, 1, 1, false},
                {-1, -1, 0, 0, false},
                {0, 0, -1, -1, false},
                {1, 1, 1, 1, false},
                {-1, -1, -1, -1, false},
                {1, 1, 10, 10, false},
                {10, 10, 1, 1, false},
        });
    }

    public BasePhysicalEntityTest(double width, double height, double poseX, double poseY, boolean isTorus) {
        Pose pose = new Pose(poseX, poseY, 0);
        arena = Arena.overWriteInstance(100, 100, isTorus);
        entity = new BasePhysicalEntity(Arena.getInstance(100, 100, isTorus), new Random(), width, height, pose, 1) {
            @Override
            public double getTrajectoryMagnitude() {
                return 0;
            }

            @Override
            public Color getClassColor() {
                return null;
            }

            @Override
            public boolean isPositionInEntity(Position position) {
                return false;
            }

            @Override
            public Position getClosestPositionInEntity(Position position) {
                return null;
            }

            @Override
            public double getArea() {
                return 0;
            }
        };
        arena.addEntity(entity);
        position = new Position(poseX, poseY);
    }

    @Test
    public void testArenaBounds() {
        if (!entity.inArenaBounds())
            entity.setInArenaBounds();
        if (arena.getWidth() > entity.getWidth() && arena.getHeight() > entity.getHeight())
            Assert.assertTrue(entity.inArenaBounds());
        entity.getPose().set(new Pose(0, 0, entity.getPose().getRotation()));
        if (0 < entity.getWidth() && 0 < entity.getHeight())
            Assert.assertFalse(entity.inArenaBounds());
    }

    @Test
    public void testAlterMovingVector() {
        Vector2D vec = new Vector2D(1, 0);
        entity.getMovingVec().set(vec);
        entity.alterMovingVector();
        Assert.assertEquals(vec.multiplication(1 - entity.getFriction()).getX(), entity.getMovingVec().get().getX(), .0);
    }

    @Test
    public void testCollisionDetection() {
        PhysicalEntity wall = new Wall(arena, new Random(), 2, 2, new Pose(position, 0), 1);
        arena.addEntity(wall);
        Assert.assertTrue(entity.collisionDetection());
    }

    @Test
    public void testCollision() {
        BaseEntity robot = new BaseRobot(0, 0, 0, 0, 0,
                0, 0, new Logger(), 100, true, arena,
                new Random(), new Pose(position.creatPositionByDecreasing(-1, -1), 0), 0) {
            @Override
            public void behavior() {
            }

            @Override
            public Color getClassColor() {
                return null;
            }
        };
        PhysicalEntity box = new Box(arena, new Random(), 2, 2, new Pose(position.creatPositionByDecreasing(1, 0), 0), 1);
        PhysicalEntity wall = new Wall(arena, new Random(), 2, 2, new Pose(position.creatPositionByDecreasing(0, 1), 0), 1);
        arena.addEntity(box);
        arena.addEntity(robot);
        arena.addEntity(wall);
        while (entity.collisionDetection()) {
            entity.setNextPosition();
            Assert.assertTrue(entity.collidingWith().size() > 0);
        }
    }

    @Test
    public void testCollidingWith() {
        PhysicalEntity box = new Box(arena, new Random(), 2, 2, new Pose(position.creatPositionByDecreasing(1, 0), 0), 1);

        entity.getMovingVec().set(Vector2D.creatCartesian(10,0));
        box.getMovingVec().set(Vector2D.creatCartesian(10,-Math.PI));
        arena.addEntity(box);
        Vector2D vec1 = entity.getMovingVec().get(), vec2 = box.getMovingVec().get();

        entity.collision(box);
        //conservation of energy
        Assert.assertEquals(vec1.getLength() + vec1.getLength(),
                box.getMovingVec().get().getLength()
                        +entity.getMovingVec().get().getLength(),0.01);
    }

    @Test
    public void testCenterOfGroupWithEntities() {

    }
}
