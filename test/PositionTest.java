import model.Area;
import model.Arena;
import model.Pose;
import model.Position;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

@RunWith(Parameterized.class)
public class PositionTest {
    private Position position1;
    private Position position2;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {1, 1, 0, 0},
                {0, 0, 1, 1},
                {1, 0, 1, 0},
                {0, 1, 0, 1},
                {1, 0, 0, 1},
                {0, 1, 1, 0},
                {-1, -1, 0, 0},
                {0, 0, -1, -1},
                {-1, 0, -1, 0},
                {0, -1, 0, -1},
                {-1, 0, 0, -1},
                {0, -1, -1, 0},
        });
    }

    public PositionTest(double x1, double y1, double x2, double y2) {
        position1 = new Position(x1, y1);
        position2 = new Position(x2, y2);
    }

    @Test
    public void TestGetAngle() {
        Position posZero = new Position(0, 0), posOne = new Position(1, 0);
        Assert.assertEquals(posZero.getAngleToPosition(posOne), 0, 0);
        Assert.assertEquals(posOne.getAngleToPosition(posZero), Math.PI, 0.01);
        if (!position2.creatPositionByDecreasing(position1).equals(new Position(0, 0))) {
            Assert.assertEquals(position1.getAngleToPosition(position2), position2.creatPositionByDecreasing(position1).getPolarAngle(), 0);
            Assert.assertEquals(position2.getAngleToPosition(position1), position1.creatPositionByDecreasing(position2).getPolarAngle(), 0);
        }
    }
    @Test
    public void TestDistance() {
        Assert.assertEquals(position1.distance(position2),position1.subtractFromPosition(position2.toVector()).distance(0,0), 0.00);
    }

    @Test
    public void TestPositionAddSubtract() {
        Position position1Old= position1.clone();
        Assert.assertEquals(position1.distance(position2),position1.creatPositionByDecreasing(position2).distance(0,0), 0.00);
        Assert.assertTrue(position1.creatPositionByDecreasing(position2).equals(position1.subtractFromPosition(position2.toVector())));
        position1.addToPosition(position2);
        Assert.assertTrue(position1.equals(position1Old));
    }
}
