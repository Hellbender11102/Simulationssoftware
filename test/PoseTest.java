
import model.Pose;
import model.Position;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class PoseTest {
    private final Pose pose1;
    private final Pose pose2;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 0, 0, 0, 0, 0},
                {-1, -1, 0, -1, -1, 0},
                {0, 0, Math.PI, 0, 0, -Math.PI},
                {-1, -1,  Math.PI /2, -1, -1,  Math.PI*2},
                {-1, -1, - Math.PI /2, -1, -1,  Math.PI},
                
                {10, 10, 0, 10, 10, 0},
                {0, 0, Math.PI, 0, 0, -Math.PI},
                {10, 10,  Math.PI /2, 10, 10,  Math.PI*2},
                {10, 10, - Math.PI /2, 10, 10,  Math.PI},
        });
    }

    public PoseTest(double x1, double y1, double rotation1, double x2, double y2, double rotation2) {
        pose1 = new Pose(x1, y1, rotation1);
        pose2 = new Pose(x2, y2, rotation2);
    }

    @Test
    public void TestSetRotation() {
        double orientation1 = pose1.getRotation(), orientation2 = pose2.getRotation();
        pose1.incRotation(pose2.getRotation());
        double inc = pose1.getRotation();
        Assert.assertEquals((orientation1 + orientation2) % (2 * Math.PI), inc, 0);
        pose1.setRotation(pose2.getRotation());
        Assert.assertEquals(pose1.getRotation(), orientation2, 0);

    }

    @Test
    public void TestGetPositionInDirection() {
        Position position = pose1.getPoseInDirection(10);
        Assert.assertEquals(pose1.getEuclideanDistance(position), 10, 0.001);
        Assert.assertEquals(pose1.getAngleToPosition(position), pose1.getRotation(), 0.001);
    }

    @Test
    public void TestGetAngleDiff() {
        Assert.assertTrue(pose1.getAngleDiff(pose2.getRotation()) < Math.PI * 2);
    }

}
