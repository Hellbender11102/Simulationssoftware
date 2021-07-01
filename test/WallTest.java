import model.Area;
import model.Arena;
import model.Pose;
import model.Wall;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

@RunWith(Parameterized.class)
public class WallTest {
    private final Wall wall;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {},
        });
    }

    public WallTest(double diameters, double noticeableDistanceDiameters, double poseX,double poseY){
        wall = creatWall(diameters,noticeableDistanceDiameters,poseX,poseY);
    }

    public Wall creatWall( double diameters, double noticeableDistanceDiameters, double poseX,double poseY) {
        return new Wall(Arena.getInstance(1000,1000,false),new Random(),diameters,noticeableDistanceDiameters,new Pose(poseX,poseY,0));
    }


}
