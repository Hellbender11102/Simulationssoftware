import model.AbstractModel.BaseEntity;
import model.Area;
import model.Arena;
import model.Pose;
import model.Position;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.util.Random;

public class BaseEntityTest {
    private final BaseEntity entity;

    public BaseEntityTest() {
        Pose pose = new Pose(0, 0, 0);
        Arena arena = Arena.getInstance(100, 100, false);
        entity = new BaseEntity(arena, new Random(), 10, 10, pose) {
            @Override
            public Color getClassColor() {
                return null;
            }

            @Override
            public boolean hasPhysicalBody() {
                return true;
            }

            @Override
            public boolean isPositionInEntity(Position position) {
                return false;
            }

            @Override
            public Position getClosestPositionInEntity(Position position) {
                return closestPositionInEntityForCircle(pose, width / 2);
            }

            @Override
            public double getArea() {
                return getAreaCircle();
            }
        };
        arena.addEntity(entity);
    }

    @Test
    public void TestMemory() {
        for (int i = 0; i < 1000; i++) {
            entity.getPose().addToPosition(-1, 1);
            entity.updatePositionMemory();
        }
        Pose pose = entity.getPose().clone();
        for (int i = 0; i < 1000; i++) {
            entity.setPrevPose();
            Assert.assertEquals(entity.getPose().getX(), pose.creatPositionByDecreasing(-i, i).getX(), 0);
            Assert.assertEquals(entity.getPose().getY(), pose.creatPositionByDecreasing(-i, i).getY(), 0);
        }
        for (int i = 999; i > 0; i--) {
            entity.setNextPose();
            Assert.assertEquals(entity.getPose().getX(), pose.creatPositionByDecreasing(-i, i).getX(), 0);
            Assert.assertEquals(entity.getPose().getY(), pose.creatPositionByDecreasing(-i, i).getY(), 0);
        }
        entity.setPrevPose();
        entity.setToLatestPose();
        Assert.assertTrue(entity.getPose().equals(pose));
    }

    @Test
    public void TestClosestPositionInEntityForSquare() {
        for (double i = 0; i < Math.PI * 2; i += Math.toRadians(1)) {
            Position position = entity.getPose().getPoseInDirection(entity.getWidth() + entity.getHeight(), i);
            Position positionSquare = entity.closestPositionInEntityForSquare(position);
            Position positionCircle = entity.closestPositionInEntityForCircle(position, entity.getWidth() / 2.);

            Assert.assertTrue(positionSquare.getEuclideanDistance(position) < position.getEuclideanDistance(entity.getPose()));
            Assert.assertTrue(entity.isPositionInEntitySquare(positionSquare));
            Assert.assertTrue(positionCircle.getEuclideanDistance(position) < position.getEuclideanDistance(entity.getPose()));
            Assert.assertEquals(entity.getPose().getEuclideanDistance(positionCircle), entity.getPose().getEuclideanDistance(position) - positionCircle.getEuclideanDistance(position), 0.0001);

            //is wrong due to rounding errors
            if (!(entity.getPose().getEuclideanDistance(positionCircle) > entity.getPose().getEuclideanDistance(position) - positionCircle.getEuclideanDistance(position) - 0.000000001))
                Assert.assertTrue(entity.isPositionInEntityCircle(positionCircle));
        }
    }
}
