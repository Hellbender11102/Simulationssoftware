package controller;

import model.Position;
import model.Robot;
import view.View;

import java.util.*;

public class Controller {
    private View view;
    private Map<Robot, Position> robotsAndPositionOffsets = new HashMap<>();

    public Controller(View view) {
        this.view = view;
        robotsAndPositionOffsets.put(new Robot(0, 1, 2), new Position(0, 0));
        robotsAndPositionOffsets.put(new Robot(0, 1, 2), new Position(100, 100, 50));
    }

    public void simulationLoop() {
        for (Robot robot : robotsAndPositionOffsets.keySet()) {
            robot.start(1000);
        }
        for (int i = 0; i < 1000; i++) {
            view.setRobots(convertPositionsToGlobal(robotsAndPositionOffsets));
            view.repaint();
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
