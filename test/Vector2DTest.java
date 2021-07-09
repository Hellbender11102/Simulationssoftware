import model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class Vector2DTest {
    private final Vector2D vector2D;
    private final double param;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 0, 1},
                {1, 0, 1},
                {0, 1, 1},
                {1, 1, 1},
                {-1, 0, 1},
                {0, -1, 1},
                {-1, -1, 1},
                {0, 0, 10},
                {1, 0, 10},
                {0, 1, 10},
                {1, 1, 10},
                {-1, 0, 10},
                {0, -1, 10},
                {-1, -1, 10},
                {0, 0, 0},
                {1, 0, 0},
                {0, 1, 0},
                {1, 1, 0},
                {-1, 0, 0},
                {0, -1, 0},
                {-1, -1, 0},
                {0, 0, -1},
                {1, 0, -1},
                {0, 1, -1},
                {1, 1, -1},
                {-1, 0, -1},
                {0, -1, -1},
                {-1, -1, -1},
        });
    }

    public Vector2DTest(double x, double y, double param) {
        vector2D = new Vector2D(x, y);
        this.param = param;
    }

    @Test
    public void TestSetToZeroVector() {
        vector2D.setToZeroVector();
        Assert.assertTrue(Vector2D.zeroVector().equals(vector2D));
    }

    @Test
    public void TestVector2DPosition() {
        Position vectorPosition = new Position(vector2D.getX(), vector2D.getY());
        Assert.assertTrue(vectorPosition.toVector().equals(vector2D));
    }

    @Test
    public void TestSetAndReverse() {
        Vector2D initially = vector2D.clone();
        vector2D.set(vector2D.reverse());
        Assert.assertEquals(vector2D.getX(), -initially.getX(), 0);
        Assert.assertEquals(vector2D.getY(), -initially.getY(), 0);
    }

    @Test
    public void TestMultiplication() {
        Vector2D changed = vector2D.multiplication(param);
        Assert.assertEquals(vector2D.getX() * param, changed.getX(), 0);
        Assert.assertEquals(vector2D.getX() * param, changed.getX(), 0);
    }

    @Test
    public void TestSubtract() {
        Vector2D changed = vector2D.subtract(new Vector2D(param, param));
        Assert.assertEquals(vector2D.getX() - param, changed.getX(), 0);
        Assert.assertEquals(vector2D.getX() - param, changed.getX(), 0);
    }

    @Test
    public void TestAdd() {
        Vector2D changed = vector2D.add(new Vector2D(param, param));
        Assert.assertEquals(vector2D.getX() + param, changed.getX(), 0);
        Assert.assertEquals(vector2D.getX() + param, changed.getX(), 0);
    }

    @Test
    public void TestScalar() {
        double result = vector2D.scalarProduct(new Vector2D(param, param));
        Assert.assertEquals(vector2D.getX() * param + vector2D.getY() * param, result, 0);
    }

    @Test
    public void TestCross() {
        double result = vector2D.cross(new Vector2D(param, -param));
        Assert.assertEquals(vector2D.getX() * -param - vector2D.getY() * param, result, 0);
    }

    @Test
    public void TestNormalize() {
        Vector2D normelized = vector2D.normalize();
        if (vector2D.getLength() > 0)
            Assert.assertEquals(normelized.getLength(), 1, 0.00001);
    }

    @Test
    public void TestDivide() {
        if (param != 0) {
            Vector2D changed = vector2D.divide(param);
            if (changed.getLength() > 0) {
                Assert.assertEquals(changed.getX(), vector2D.getX() / param, 0.00001);
                Assert.assertEquals(changed.getY(), vector2D.getY() / param, 0.00001);
            }
        }
    }


    @Test
    public void TestCreatCartesian() {
        Assert.assertEquals(vector2D.getX(), Vector2D.creatCartesian(vector2D.getLength(), vector2D.getAngle()).rotateTo(vector2D.getAngle()).getX(), 0.0001);
        Assert.assertEquals(vector2D.getY(), Vector2D.creatCartesian(vector2D.getLength(), vector2D.getAngle()).rotateTo(vector2D.getAngle()).getY(), 0.0001);
    }

    @Test
    public void TestRotateTo() {
        Assert.assertTrue(vector2D.rotateTo(param).equals(Vector2D.creatCartesian(vector2D.getLength(), param)));
    }


    @Test
    public void TestDistance() {
        Assert.assertEquals(vector2D.distance(new Vector2D(0,0)),new Position(vector2D.getX(),vector2D.getY()).distance(0,0),0);
    }

}
