package controller;

import model.Arena;
import model.Pose;
import model.Position;
import model.Robot;
import view.View;

import java.awt.event.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Controller {
    private View view;
    private Arena arena;
    private Map<Robot, Position> robotsAndPositionOffsets;
    private ConcurrentLinkedQueue<Robot> robotConcurrentQueue;
    private final Random random;
    private final int robotCount;
    private final Timer timer = new Timer();

    public Controller(ConcurrentLinkedQueue<Robot> robotConcurrentQueue,
                      Map<Robot, Position> robotsAndPositionOffsets, Arena arena, Random random) {
        view = new View(arena);
        addViewListener();
        this.arena = arena;
        this.robotsAndPositionOffsets = robotsAndPositionOffsets;
        this.robotConcurrentQueue = robotConcurrentQueue;
        this.random = random;
        robotCount = robotsAndPositionOffsets.keySet().size();
    }

    public void startRobotThreads() {
        robotsAndPositionOffsets.keySet().forEach(Thread::start);

    }

    public void visualisationLoop(int framesPerSecond) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (robotConcurrentQueue.size() >= robotCount) {
                    LinkedList<Robot> robotList = new LinkedList<>();
                    for (int i = 0; i < robotCount; i++) {
                        robotList.add(robotConcurrentQueue.poll());
                    }
                    arena.setRobots(robotList);
                }
                view.repaint();
            }
        }, 1000, 1000 / framesPerSecond);
    }


    private LinkedList<Pose> convertPositionsToGlobal(Map<Robot, Pose> localAndOffset) {
        LinkedList<Pose> globalPositionList = new LinkedList<>();
        for (Robot robot : localAndOffset.keySet()) {
            globalPositionList.add(transPos(localAndOffset.get(robot), robot.getLocalPose()));
        }
        return globalPositionList;
    }


    private Pose transPos(Pose pGlobal, Pose pLocal) {
        double x = pGlobal.getxCoordinate() + pLocal.getxCoordinate();
        double y = pGlobal.getyCoordinate() + pLocal.getyCoordinate();
        double rotation = pLocal.getRotation();
        return new Pose(x, y, rotation);
    }

    private void addViewListener() {
        KeyListener keyListener = new KeyListener() {
            int x = 0, y = 0;
            boolean stopped = false;
            Map<Robot, Position> robots;

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        robots = new HashMap<>();
                        for (Robot robot : robotsAndPositionOffsets.keySet()) {
                            if (stopped) {
                                robots.put(new Robot(robot), robotsAndPositionOffsets.get(robot));
                            } else {
                                robot.toggleStop();
                            }
                        }
                        if (stopped) {
                            robotsAndPositionOffsets = robots;
                            robotsAndPositionOffsets.keySet().forEach(Thread::start);
                        }
                        stopped = !stopped;
                        break;
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                        view.getSimView().incOffsetY(--y);
                        break;
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                        view.getSimView().incOffsetX(--x);
                        break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        view.getSimView().incOffsetY(++y);
                        break;
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                        view.getSimView().incOffsetX(++x);
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        y = 0;
                        break;
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                        x = 0;
                        break;
                }
            }
        };

        view.addKeyListener(keyListener);
    }
}
