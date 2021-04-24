package controller;

import model.*;
import model.RobotTypes.BaseRobot;
import model.AbstractModel.RobotInterface;
import view.View;

import java.awt.event.*;
import java.util.*;

public class Controller {
    private View view;
    private Arena arena;
    private Map<RobotInterface, Position> robotsAndPositionOffsets;
    private List<Thread> threadList = new LinkedList<>();
    private final Random random;
    private final Timer repaintTimer = new Timer();
    private final Timer pastTimeTimer = new Timer();

    public Controller(Map<RobotInterface, Position> robotsAndPositionOffsets, Arena arena, Random random) {
        view = new View(arena);
        addViewListener();
        this.arena = arena;
        this.robotsAndPositionOffsets = robotsAndPositionOffsets;
        this.random = random;
    }

    /**
     * Schedules an timer that checks for robot collisions
     * Inserts the robots in the map and pauses them
     */
    public void initRobots() {
        arena.setRobots(new ArrayList<>(robotsAndPositionOffsets.keySet()));
        for (RobotInterface robot : robotsAndPositionOffsets.keySet()) {
            startThread(robot);
        }
    }

    /**
     * Starts an scheduled timer which checks for new robot locations and puts these on the arena
     * Repaints the view after
     *
     * @param framesPerSecond int
     */
    public void visualisationTimer(int framesPerSecond) {
        repaintTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                view.repaint();
            }
        }, 1000, 1000 / framesPerSecond);
    }

    /**
     * Translates the local position of an robot into an global position of the map
     *
     * @param globalOffset offset which the robot had when created
     * @param robot        robot
     * @return RobotInterface
     */
    private synchronized RobotInterface convertPoseToGlobal(Position globalOffset, BaseRobot robot) {
        Pose pose = transPos(globalOffset, robot.getPose());
        robot.getPose().setXCoordinate(pose.getXCoordinate());
        robot.getPose().setYCoordinate(pose.getYCoordinate());
        robot.getPose().setRotation(pose.getRotation());
        return robot;
    }


    private synchronized Pose transPos(Position pGlobal, Pose pLocal) {
        double x = pGlobal.getXCoordinate() + pLocal.getXCoordinate();
        double y = pGlobal.getYCoordinate() + pLocal.getYCoordinate();
        double rotation = pLocal.getRotation();
        return new Pose(x, y, rotation);
    }

    /**
     * Adds event listener for the Simulation view
     */
    private void addViewListener() {
        KeyListener keyListener = new KeyListener() {
            int x = 0, y = 0;
            boolean stopped = true;
            Map<RobotInterface, Position> robots;

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        robots = new HashMap<>();
                        for (RobotInterface robot : robotsAndPositionOffsets.keySet()) {
                            if (stopped) {
                                robot.resetToOrigin();
                                robot.toggleStop();
                                startThread(robot);
                            } else {
                                robot.toggleStop();
                                threadList.clear();
                            }
                        }
                        stopped = !stopped;
                        break;
                    case KeyEvent.VK_B:
                        if (stopped)
                            for (RobotInterface robot : robotsAndPositionOffsets.keySet()) {
                                robot.setPrevPose();
                            }
                        break;
                    case KeyEvent.VK_N:
                        if (stopped)
                            for (RobotInterface robot : robotsAndPositionOffsets.keySet()) {
                                robot.setNextPose();
                            }
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
                    case KeyEvent.VK_O:
                        view.getSimView().toggleDrawRotationIndicator();
                        break;
                    case KeyEvent.VK_E:
                        view.getSimView().toggleDrawRobotEngines();
                        break;
                    case KeyEvent.VK_R:
                        view.getSimView().toggleDrawrobotRotationo();
                        break;
                    case KeyEvent.VK_C:
                        view.getSimView().toggleDrawrobotCoordinates();
                        break;
                    case KeyEvent.VK_SHIFT:
                    case KeyEvent.VK_PLUS:
                        view.getSimView().incFontSize(1);
                        break;
                    case KeyEvent.VK_CONTROL:
                    case KeyEvent.VK_MINUS:
                        view.getSimView().incFontSize(-1);
                        break;
                    case KeyEvent.VK_G:
                    case KeyEvent.VK_NUMBER_SIGN:
                        view.getSimView().toggleDrawLines();
                        break;
                    case KeyEvent.VK_T:
                        view.getSimView().toggleDrawTypeInColor();
                        break;
                    case KeyEvent.VK_L:
                        view.getSimView().toggleDrawInfosLeft();
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

    private void startThread(RobotInterface robot) {
        Thread t = new Thread(robot);
        t.setDaemon(true);
        threadList.add(t);
        t.start();
    }
}
