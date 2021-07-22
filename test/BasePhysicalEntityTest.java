import controller.Logger;
import model.*;
import model.AbstractModel.*;
import model.RobotTypes.BaseRobot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(Parameterized.class)
public class BasePhysicalEntityTest {
    private final BasePhysicalEntity entity;
    private final Arena arena;
    private Position position;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                //width,  height,  poseX,  poseY,  isTorus,  isSquare
                {0, 0, 0, 0, false, true},
                {1, 1, 0, 0, true, true},
                {0, 0, 1, 1, true, true},
                {-1, -1, 0, 0, true, true},
                {0, 0, -1, -1, true, true},
                {1, 1, 1, 1, true, true},
                {-1, -1, -1, -1, true, true},
                {1, 1, 10, 10, true, true},
                {10, 10, 1, 1, true, true},
                {1, 1, 0, 0, false, true},
                {0, 0, 1, 1, false, true},
                {-1, -1, 0, 0, false, true},
                {0, 0, -1, -1, false, true},
                {1, 1, 1, 1, false, true},
                {-1, -1, -1, -1, false, true},
                {1, 1, 10, 10, false, true},
                {10, 10, 1, 1, false, true},
                {1, 1, 9, 11, false, true},
                {10, 10, 2, 8, false, true},
                {1, 1, 9, 11, false, true},
                {10, 10, -1, 9, false, true},
                {1, 1, 9, 11, false, true},
                {10, 10, 30, -11, false, true},
                {0, 0, 0, 0, false, false},
                {1, 1, 0, 0, true, false},
                {0, 0, 1, 1, true, false},
                {-1, -1, 0, 0, true, false},
                {0, 0, -1, -1, true, false},
                {1, 1, 1, 1, true, false},
                {-1, -1, -1, -1, true, false},
                {1, 1, 10, 10, true, false},
                {10, 10, 1, 1, true, false},
                {1, 1, 0, 0, false, false},
                {0, 0, 1, 1, false, false},
                {-1, -1, 0, 0, false, false},
                {0, 0, -1, -1, false, false},
                {1, 1, 1, 1, false, false},
                {-1, -1, -1, -1, false, false},
                {1, 1, 10, 10, false, false},
                {10, 10, 1, 1, false, false},
                {1, 1, 9, 11, false, false},
                {10, 10, 2, 8, false, false},
                {1, 1, 9, 11, false, false},
                {10, 10, -1, 9, false, false},
                {1, 1, 9, 11, false, false},
                {10, 10, 30, -11, false, false},
        });
    }

    public BasePhysicalEntityTest(double width, double height, double poseX, double poseY, boolean isTorus, boolean isSquare) {
        Pose pose = new Pose(poseX, poseY, 0);
        arena = Arena.overWriteInstance(100, 100, isTorus);
        entity = new BasePhysicalEntity(arena,
                new Random(), width, height, false, pose, 1) {
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
                if (isSquare)
                    return isPositionInEntitySquare(position);
                else return isPositionInEntityCircle(position);
            }

            @Override
            public Position getClosestPositionInEntity(Position position) {
                if (isSquare)
                    return closestPositionInEntityForSquare(position);
                else return closestPositionInEntityForCircle(position, (width / 2 + width / 2) / 2);
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
        PhysicalEntity wall = new Wall(arena, new Random(), 2, 2, false, new Pose(position, 0), 1);
        arena.addEntity(wall);

        if (arena.isTorus) {
            arena.setEntityInTorusArena(wall);
            arena.setEntityInTorusArena(entity);
        }
        Assert.assertTrue(entity.collidingWith().contains(wall));
    }


    @Test
    public void testCollidingWith() {
        Position entityPos = position.clone(), boxPos = position.creatPositionByDecreasing(1, 0);
        double width = 2, height = 2;
        Box box = new Box(arena, new Random(), width, height, false, new Pose(boxPos, 0), 1);

        entity.getMovingVec().set(Vector2D.creatCartesian(position.getX(), 0));
        box.getMovingVec().set(Vector2D.creatCartesian(position.getY(), Math.PI));

        arena.addEntity(box);
        //m1*v1+m2*v2
        Vector2D startingVec = entity.getMovingVec().get().multiplication(entity.getWeight()).add(box.getMovingVec().get().multiplication(box.getWeight()));

        box.collision(entity);

        //  m1*u1+m2*u2
        Vector2D resultingVec = entity.getMovingVec().get().multiplication(entity.getWeight()).add(box.getMovingVec().get().multiplication(box.getWeight()));

        //conservation of energy m1*v1+m2*v2=m1*u1+m2*u2
        Assert.assertEquals(startingVec.getLength(), resultingVec.getLength(), 0.01);


        if (resultingVec.getLength() > 0) {
            entity.setNextPosition();
            Assert.assertTrue(entityPos.getEuclideanDistance(boxPos) < entity.getPose().getEuclideanDistance(box.getPose()));
        }

    }

    @Test
    public void testEntityGroupByClasses() {
        PhysicalEntity robot = new BaseRobot(0, 0, 1, 1, 0,
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
        PhysicalEntity box = new Box(arena, new Random(), 2, 2, false, new Pose(position.creatPositionByDecreasing(1, 0), 0), 1);
        PhysicalEntity wall = new Wall(arena, new Random(), 2, 2, false, new Pose(position.creatPositionByDecreasing(0, 1), 0), 1);
        Entity area = new Area(arena, new Random(), 2, 2, new Pose(position.creatPositionByDecreasing(0, 1), 0));
        arena.addEntity(box);
        arena.addEntity(robot);
        arena.addEntity(wall);
        arena.addEntity(area);

        Assert.assertEquals(entity.entityGroupByClasses(List.of(Box.class)).size(), 1);
        Assert.assertEquals(entity.entityGroupByClasses(List.of(Wall.class, Box.class)).size(), 2);
        Assert.assertEquals(entity.entityGroupByClasses(List.of(Wall.class, Box.class, BaseRobot.class)).size(), 3);
        Assert.assertEquals(entity.entityGroupByClasses(List.of(Wall.class, Box.class, BaseRobot.class, Area.class)).size(), 4);
        Assert.assertEquals(entity.entityGroupByClasses(List.of(Entity.class)).size(), 5);
        Assert.assertEquals(entity.entityGroupByClasses(List.of(PhysicalEntity.class)).size(), 4);
        Assert.assertEquals(entity.entityGroupByClasses(List.of(RobotInterface.class)).size(), 1);
    }

    @Test
    public void testCenterOfGroupWithClasses() {
        BaseEntity robot = new BaseRobot(0, 0, 1, 1, 0,
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
        PhysicalEntity box = new Box(arena, new Random(), 2, 2, false, new Pose(position.creatPositionByDecreasing(1, 0), 0), 1);
        PhysicalEntity wall = new Wall(arena, new Random(), 2, 2, false, new Pose(position.creatPositionByDecreasing(0, 1), 0), 1);
        arena.addEntity(box);
        arena.addEntity(robot);
        arena.addEntity(wall);
        entity.entityGroupByClasses(List.of(Box.class));
        entity.entityGroupByClasses(List.of(Wall.class));
        entity.entityGroupByClasses(List.of(RobotInterface.class));
    }


    @Test
    public void testCollision() {
        BaseRobot robot = new BaseRobot(1, 0, 1, 1, 0,
                1, 0, new Logger(), 100, true, arena,
                new Random(), new Pose(position.creatPositionByDecreasing(-1, -1), 0), 1) {
            @Override
            public void behavior() {
            }
        };
        PhysicalEntity box = new Box(arena, new Random(), 1, 1, false, new Pose(position.creatPositionByDecreasing(2.5, 0), 0), 1);
        PhysicalEntity wall = new Wall(arena, new Random(), 1, 1, false, new Pose(position.creatPositionByDecreasing(0, 2.5), 0), 1);
        arena.addEntity(box);
        arena.addEntity(robot);
        arena.addEntity(wall);
        while (entity.collisionDetection()) {
            entity.alterMovingVector();
            box.alterMovingVector();
            wall.alterMovingVector();
            robot.alterMovingVector();
            entity.setNextPosition();
            wall.setNextPosition();
            box.setNextPosition();
            robot.setNextPosition();
        }
        Assert.assertEquals(0, entity.collidingWith().size());
    }
}

