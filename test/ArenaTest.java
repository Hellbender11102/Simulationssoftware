import model.Area;
import model.Arena;
import model.Pose;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

@RunWith(Parameterized.class)
public class ArenaTest {
    private final Area area;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {},
        });
    }

    public ArenaTest(double diameters, double noticeableDistanceDiameters, double poseX,double poseY){
        area = creatArea(diameters,noticeableDistanceDiameters,poseX,poseY);
    }

    public Area creatArea( double diameters, double noticeableDistanceDiameters, double poseX,double poseY) {
        return new Area(Arena.getInstance(1000,1000,false),new Random(),diameters,noticeableDistanceDiameters,new Pose(poseX,poseY,0));
    }

}
