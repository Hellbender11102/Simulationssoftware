import model.AbstractModel.BaseEntity;
import model.AbstractModel.BasePhysicalEntity;
import model.Area;
import model.Arena;
import model.Pose;
import model.Position;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

@RunWith(Parameterized.class)
public class BasePhysicalEntityTest {
    private final BaseEntity entity;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {},
        });
    }

    public BasePhysicalEntityTest(double width, double height, double poseX, double poseY,boolean isTorus){
        Pose pose = new Pose(poseX,poseY,0);
        Arena arena = Arena.getInstance(100, 100, isTorus);
        entity = new BasePhysicalEntity( Arena.getInstance(100,100,isTorus),new Random(),width,height,pose,1) {
            @Override
            public double getTrajectoryMagnitude() {
                return 0;
            }
            @Override
            public Color getClassColor() {
                return null;
            }
            @Override
            public boolean isPositionInEntity(Position position) {
                return false;
            }
            @Override
            public Position getClosestPositionInEntity(Position position) {
                return null;
            }
            @Override
            public double getArea() {
                return 0;
            }
        };

    }
}
