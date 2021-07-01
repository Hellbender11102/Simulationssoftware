import model.Area;
import model.Arena;
import model.Pose;
import model.RobotBuilder;
import model.RobotTypes.BaseRobot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

@RunWith(Parameterized.class)
public class AreaTest {

    private final Area area;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {},
        });
    }

    public AreaTest(double diameters, double noticeableDistanceDiameters, double poseX, double poseY) {
        area = creatArea(diameters, noticeableDistanceDiameters, poseX, poseY);
    }

    public Area creatArea(double diameters, double noticeableDistanceDiameters, double poseX, double poseY) {
        return new Area(Arena.getInstance(1000, 1000, false), new Random(), diameters, noticeableDistanceDiameters, new Pose(poseX, poseY, 0));
    }

    @Test
    public void testArenaDecreaseArea() {
        double areaRadius = area.getRadius();
        area.decreaseArea(1);
        Assert.assertEquals( - 0.5,area.getRadius());
    }

    @Test
    public void testArenaDecreaseAreaOfSight() {
        area.decreaseAreaOfSight(1);
    }


}
