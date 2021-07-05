import model.Area;
import model.Arena;
import model.Pose;
import model.Position;
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
                {},
        });
    }

    public PositionTest(double x1, double y1, double x2, double y2) {
        position1 = new Position(x1, y1);
        position2 = new Position(x2, y2);
    }

}
