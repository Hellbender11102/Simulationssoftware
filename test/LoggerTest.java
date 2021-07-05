import controller.Logger;
import model.Pose;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class LoggerTest {
    Logger logger;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {},
        });
    }

    public LoggerTest(double x1, double y1, double rotation1, double x2, double y2, double rotation2) {
        logger = new Logger();
    }


}
