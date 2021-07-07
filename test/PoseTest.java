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
public class PoseTest {
    private final Pose pose1;
    private final Pose pose2;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0,0,0,0,0,0},
                {-1,-1,0,-1,-1,0},
        });
    }

    public PoseTest(double x1, double y1, double rotation1, double x2, double y2, double rotation2) {
        pose1 = new Pose(x1, y1, rotation1);
        pose2 = new Pose(x2, y2, rotation2);
    }

    @Test
    public void TestRotation(){

    }

}
