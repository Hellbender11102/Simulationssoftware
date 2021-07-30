import helper.Logger;
import model.*;
import model.abstractModel.PhysicalEntity;
import model.robotTypes.BaseRobot;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class SinglePhysicalEntityTest {

        Arena arena = Arena.overWriteInstance(100, 100, true);

        @Test
        public void test() {
            Pose startPose= new Pose(50, 50, 0);
            PhysicalEntity robot = new BaseRobot(1, -1, 0, 0, 1,
                    1, 0, new Logger(), 100, true, arena,
                    new Random(), startPose, 1) {
                @Override
                public void behavior() {
                }
            };
            PhysicalEntity box = new Box(arena, new Random(), 1, 1,
                    false, startPose, 0);
            PhysicalEntity wall = new Wall(arena, new Random(), 1, 1,
                    false, startPose, 0);

            arena.addEntity(box);
            arena.addEntity(wall);
            arena.addEntity(robot);


            for (PhysicalEntity physicalEntity : arena.getPhysicalEntityList()) {

                Assert.assertEquals(physicalEntity.inArenaBounds(), true);

                Assert.assertEquals(physicalEntity.getMovingVec().get().getLength(), 0, 0);


                if (physicalEntity.collidingWith().size() != 0) {
                    Assert.assertEquals(physicalEntity.collidingWith().size(), 2, 0);
                    Assert.assertTrue(physicalEntity.collisionDetection());
                    physicalEntity.setNextPosition();
                    if(physicalEntity.isMovable() && physicalEntity.getMovingVec().get().getLength() > 0){
                        Assert.assertNotEquals(physicalEntity.getPose(),startPose);
                    } else{
                        Assert.assertEquals(physicalEntity.getPose(),startPose);
                    }
                }
                Assert.assertEquals(physicalEntity.getTrajectoryMagnitude(), 0, 0);

                Position position = physicalEntity.getPose();
                physicalEntity.setNextPosition();
                Assert.assertTrue(physicalEntity.getPose().equals(position));
                Vector2D vec = physicalEntity.getMovingVec().get();

                if(vec.getLength() ==0){
                    physicalEntity.alterMovingVector();
                    System.out.println(physicalEntity.getMovingVec().get() + " "+physicalEntity.toString());
                    Assert.assertEquals(vec.getX(),physicalEntity.getMovingVec().get().getX(),0.0);
                    Assert.assertEquals(vec.getY(),physicalEntity.getMovingVec().get().getY(),0.0);
                }else{
                    physicalEntity.alterMovingVector();
                    Assert.assertNotEquals(vec,physicalEntity.getMovingVec().get());
                }

                Assert.assertNotNull(physicalEntity.getWeight());

                Assert.assertNotNull(physicalEntity.getFriction());

                Assert.assertNotNull(physicalEntity.isMovable());

            }
        }
    }

