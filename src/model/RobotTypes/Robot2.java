package model.RobotTypes;

import model.RobotBuilder;

import java.awt.*;

public class Robot2 extends BaseRobot {

    public Robot2(RobotBuilder builder) {
        super(builder);
    }

    int toggle = 4;

    @Override
    public void behavior() {
      if (toggle==4) {
          setEngines(0, 2);
          toggle=0;
      }
        if (!isInTurn && toggle == 0) {
            turn(-30);
            System.out.println("case1");
            toggle += toggle == 0? 1 :0;
        } else if (!isInTurn && toggle == 1) {
            turn(30);
            System.out.println("case2");
            toggle += toggle == 1? 1 :0;
        } else if (!isInTurn && toggle == 2) {
            toggle = 0;
        }
    }

    @Override
    public Color getClassColor() {
        return Color.YELLOW;
    }

}