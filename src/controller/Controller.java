package controller;

import model.Position;
import model.Robot;
import view.View;

import java.util.*;

public class Controller {
    private View view;
    private Map<Robot, Position> robotsAndPositionOffsets = new HashMap<>();
    int x = 0, y = 0;

    public Controller(View view) {
        this.view = view;

        Position p1 = new Position(300, 300);
        Robot r1 = new Robot(0, 1, 2, p1);
        //Robot r2 = new Robot(1, 0, 2);
        robotsAndPositionOffsets.put(r1, p1);
        //  robotsAndPositionOffsets.put(r2, new Position(100, 100, 50));
    }

    public void simulationLoop(int i) {
        view.repaint();
        for (Robot r : robotsAndPositionOffsets.keySet()) {
            view.setRobot(r.getLocalPosition());
            r.start(i);
                            view.setRobot(r.getLocalPosition());
            for (int j = 0; j < i; j++) {
                view.setRobot(r.getLocalPosition());
                view.repaint();
            }

        }
    }

    private LinkedList<Position> convertPositionsToGlobal(Map<Robot, Position> localAndOffset) {
        LinkedList<Position> globalPositionList = new LinkedList<>();
        for (Robot robot : localAndOffset.keySet()) {
            globalPositionList.add(transformation(localAndOffset.get(robot), robot.getLocalPosition()));
        }
        return globalPositionList;
    }


    private Position transformation(Position pGlobal, Position pLocal) {
        double x = pGlobal.getxCoordinate() + pLocal.getxCoordinate();
        double y = pGlobal.getyCoordinate() + pLocal.getyCoordinate();
        double rotation = pGlobal.getRotation() + pLocal.getRotation();
        return new Position(x, y, rotation);
    }
}
