package controller;

import model.Position;
import model.Robot;
import view.View;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Controller {
    private View view;
    private Map<Robot, Position> robotsAndPositionOffsets;
    private ConcurrentLinkedQueue<Robot> threadOutputQueue;
    private final Random random;

    public Controller(View view, ConcurrentLinkedQueue<Robot> threadOutputQueue,
                      Map<Robot, Position>robotsAndPositionOffsets,Random random) {
        this.view = view;
        this.robotsAndPositionOffsets = robotsAndPositionOffsets;
        viewListener();
        this.threadOutputQueue = threadOutputQueue;
        this.random = random;
    }

    public void startRobotThreads(int cycle) {
        robotsAndPositionOffsets.keySet().forEach(robot -> {
            robot.start(cycle);
        });
    }

    public void visiualisationLoop() {
        while (robotsAndPositionOffsets.keySet().stream().map(Thread::isAlive).reduce(false, (e1, e2) -> e1 || e2)) {
            if (!threadOutputQueue.isEmpty()) {
                view.setRobot(new LinkedList<Robot>(threadOutputQueue));
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

    private void viewListener() {
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == ' ') {
                    robotsAndPositionOffsets.keySet().forEach(robot -> {
                        System.out.println(robot.getState());
                    });
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
        view.addKeyListener(keyListener);
    }
}
