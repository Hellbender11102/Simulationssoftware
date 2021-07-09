package model.RobotTypes;

import model.Position;
import model.RobotBuilder;

import java.awt.*;

public class Robot3 extends BaseRobot {

    public Robot3(RobotBuilder builder) {
        super(builder);
    }

    int i = 0;

    @Override
    public void behavior() {
        if (i == 1) {
            if (turn(-90, .5,-.5,0.01)) {
                setEngines(1, 1);
                i++;
            }
        }
        if(i == 2){
            if (move(arena.getHeight()-20,8))
                i--;
        }
        if (i == 0) {
            setEngines(1, 2);
            i++;
        }
    }
        @Override
        public Color getClassColor () {
            return Color.RED;
        }
    }