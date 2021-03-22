package controller;

import model.Position;
import model.Robot;
import view.View;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Controller {
    private View view;
    private Map<Robot, Position> robotsAndPositionOffsets;
    private ConcurrentLinkedQueue<Robot> threadOutputQueue;
    private final Random random;

    public Controller( ConcurrentLinkedQueue<Robot> threadOutputQueue,
                      Map<Robot, Position> robotsAndPositionOffsets, Random random) {
        view = new View();
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
                view.setRobot(new LinkedList<>(threadOutputQueue));
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
            int i = 1;

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case ' ':
                        robotsAndPositionOffsets.keySet().forEach(robot -> {
                            System.out.println(robot.getState());
                        });
                    case 'w':
                        System.out.println("scroll: " + i++);
                    case 'a':
                        System.out.println("scroll: " + i++);
                    case 's':
                        System.out.println("scroll: " + i++);
                    case 'd':
                        System.out.println("scroll: " + i++);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'w':
                        i = 0;
                        System.out.println("scroll: " + i);
                    case 'a':
                        i = 0;
                        System.out.println("scroll: " + i);
                    case 's':
                        i = 0;
                        System.out.println("scroll: " + i);
                    case 'd':
                        i = 0;
                        System.out.println("scroll: " + i);
                }
            }
        };
        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
        view.addKeyListener(keyListener);
        view.addMouseListener(mouseListener);
    }
}
