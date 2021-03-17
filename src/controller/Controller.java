package controller;

import model.Position;
import model.Robot;
import view.View;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Controller {
    private View view;
    private Map<Robot, Position> robotsAndPositionOffsets = new HashMap<>();
    ConcurrentLinkedQueue<Position> conQueue = new ConcurrentLinkedQueue<>();

    public Controller(View view) {
        this.view = view;

        Position p1 = new Position(300, 300);
        Position p2 = new Position(300, 300);
        Robot r1 = new Robot(0, 1, 2, p1, conQueue);
        Robot r2 = new Robot(1, 0, 2, p2, conQueue);
        robotsAndPositionOffsets.put(r1, p1);
        robotsAndPositionOffsets.put(r2, p2);
    }

    public void startRobotThreads(int cycle) {
        robotsAndPositionOffsets.keySet().forEach(robot -> {
            robot.start(cycle);
            view.setRobot(robot.getLocalPosition());
        });
    }

    public void visiualisationLoop() {
        while (robotsAndPositionOffsets.keySet().stream().map(Thread::isAlive).reduce(false,(e1, e2) -> e1||e2)) {
            if (!conQueue.isEmpty()) {
                Position pos = conQueue.poll();
                System.out.println(pos);
                view.setRobot(pos);
                view.repaint();
            }
        }
        System.out.println("fertig");
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
