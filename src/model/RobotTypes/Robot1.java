package model.RobotTypes;

import model.Position;
import model.RobotBuilder;

import java.awt.*;
import java.util.List;

public class Robot1 extends BaseRobot {

    public Robot1(RobotBuilder builder) {
        super(builder);
    }

    int i = 0;

    @Override
    public void behavior() {
        if(identifier == 0)
            identifier = random.nextInt();
        moveRandom(10,8,60);
    }

    @Override
    public Color getClassColor() {
        return Color.GREEN;
    }
}