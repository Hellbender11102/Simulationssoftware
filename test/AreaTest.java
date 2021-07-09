import model.Area;
import model.Arena;
import model.Pose;
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
                {1, 1, 1, 1},
                {0, 0, 1, 1},
                {10, 10, 10, 10},
                {10, 10, 0, 0},
                {10, 10, 0, 0},
                {-1, -1, -1, -1},
        });
    }

    public AreaTest(double diameters, double noticeableDistanceDiameters, double poseX, double poseY) {
        Arena arena = Arena.getInstance(1000, 1000, false);
        area = new Area(arena, new Random(), diameters, noticeableDistanceDiameters, new Pose(poseX, poseY, 0));
        arena.addEntity(area);
    }


    @Test
    public void testArenaDecreaseAreaRadius() {
        double areaRadius = area.getRadius();
        area.decreaseAreaDiameters(1);
        if (areaRadius <= 0)
            Assert.assertEquals(0, area.getRadius(), 0.0);
        else
            Assert.assertEquals(areaRadius - 0.5, area.getRadius(), 0.01);
    }

    @Test
    public void testArenaDecreaseAreaDiameters() {
        double areaDiameters = area.getDiameters();
        area.decreaseAreaDiameters(1);
        if (areaDiameters <= 0)
            Assert.assertEquals(0, area.getDiameters(), 0.0);
        else
            Assert.assertEquals(areaDiameters - 1, area.getDiameters(), 0.01);
    }

    @Test
    public void testArenaDecreaseAreaOfSightRadius() {
        double noticeableDistanceRadius = area.getNoticeableDistanceRadius();
        area.decreaseNoticeableDistanceDiameters(1);
        if (noticeableDistanceRadius <= 0)
            Assert.assertEquals(0, area.getNoticeableDistanceRadius(), 0.0);
        else
            Assert.assertEquals(noticeableDistanceRadius - 0.5, area.getNoticeableDistanceRadius(), 0.01);
    }

    @Test
    public void testArenaDecreaseAreaOfSightDiameters() {
        double noticeableDistanceDiameter = area.getNoticeableDistanceDiameter();
        area.decreaseNoticeableDistanceDiameters(1);
        if (noticeableDistanceDiameter <= 0)
            Assert.assertEquals(0, area.getNoticeableDistanceDiameter(), 0.0);
        else
            Assert.assertEquals(noticeableDistanceDiameter - 1, area.getNoticeableDistanceDiameter(), 0.01);
    }

    @Test
    public void testIncreaseArea() {
        double areaDiameters = area.getDiameters();
        area.increaseArea(1);
        Assert.assertEquals(areaDiameters + 1, area.getDiameters(), 0.01);
    }

    @Test
    public void testArenaIncreaseAreaOfSightRadius() {
        double noticeableDistanceDiameter = area.getNoticeableDistanceDiameter();
        area.increaseNoticeableDistanceDiameters(1);
        Assert.assertEquals(noticeableDistanceDiameter + 1, area.getNoticeableDistanceDiameter(), 0.01);
    }
}
