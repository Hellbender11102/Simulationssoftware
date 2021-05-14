package controller;

import model.*;
import model.AbstractModel.PhysicalEntity;
import model.RobotTypes.BaseRobot;
import model.AbstractModel.RobotInterface;
import view.View;

import java.awt.event.*;
import java.util.*;

public class Controller {
    private boolean stopped = true;
    private View view;
    private Arena arena;
    private List<Thread> entityThreads = new LinkedList<>();
    private Map<RobotInterface, Position> robotsAndPositionOffsets;
    private Random random;
    private JsonLoader jsonLoader = new JsonLoader();
    private final Timer repaintTimer = new Timer();
    private final Timer logTimer = new Timer();
    private final Logger logger = new Logger();

    public Controller() {
        arena = jsonLoader.initArena();
        if (jsonLoader.displayView()) {
            init();
            view = new View(arena);
            repaintTimer(jsonLoader.loadFps());
            addViewListener();
        } else {
            init();
            int timeToSimulate = arena.getRobots().get(0).getTimeToSimulate();
            arena.getRobots().forEach(this::startThread);
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            while (entityThreads.stream().anyMatch(Thread::isAlive)){
                System.out.print(
                   Math.round((1- ((double)arena.getRobots().get(0).getTimeToSimulate() / (double)timeToSimulate))*100 )+ "%\r");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            logger.saveFullLogToFile(true);
            if(entityThreads.stream().noneMatch(Thread::isAlive)
                    && (logger.saveThread == null || !logger.saveThread.isAlive())){
                timeToSimulate=jsonLoader.loadSimulatedTime();
                System.out.println("Done simulating.\nSimulated "
                        + (timeToSimulate / 60)/60+ "h " + timeToSimulate / 60+ "min " + timeToSimulate%60 +"sec");
                System.exit(0);
            }
        }
    }

    void init() {
        random = jsonLoader.loadRandom();
        robotsAndPositionOffsets = jsonLoader.loadRobots(random, logger);
        arena.setRobots(new ArrayList<>(robotsAndPositionOffsets.keySet()));
        arena.setPhysicalEntities(new ArrayList<>(robotsAndPositionOffsets.keySet()));
    }


    /**
     * Starts an scheduled timer which checks for new robot locations and puts these on the arena
     * Repaints the view after
     *
     * @param framesPerSecond int
     */
    public void repaintTimer(int framesPerSecond) {
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
                                robot.setToLatestPose();
                                robot.togglePause();
                                startThread(robot);
                            } else {
                                robot.togglePause();
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
                        view.getSimView().incFontSize(1);
                        break;
                    case KeyEvent.VK_PLUS:
                        view.getSimView().incZoom();
                        break;
                    case KeyEvent.VK_CONTROL:
                        view.getSimView().incFontSize(-1);
                        break;
                    case KeyEvent.VK_MINUS:
                        view.getSimView().decZoom();
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
                    case KeyEvent.VK_K:
                        view.getSimView().toggleDrawCenter();
                        break;
                    case KeyEvent.VK_F1:
                        if (stopped) {
                            init();
                        }
                        break;
                    case KeyEvent.VK_F2:
                        logger.saveFullLogToFile(false);
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

        //Menu listener
        view.getLog().addActionListener(actionListener -> {
            logger.saveFullLogToFile(false);
        });
        view.getRestart().addActionListener(actionListener -> {
            for (RobotInterface robot : robotsAndPositionOffsets.keySet()) {
                if (!stopped) {
                    robot.togglePause();
                }
            }
            stopped = true;
            init();
        });
        view.getFullRestart().addActionListener(actionListener -> {
            for (RobotInterface robot : robotsAndPositionOffsets.keySet()) {
                if (!stopped) {
                    robot.togglePause();
                }
            }
            stopped = true;
            jsonLoader = new JsonLoader();
            arena = jsonLoader.reloadArena();
            repaintTimer(jsonLoader.loadFps());
            init();
        });

    }

    private void startThread(RobotInterface robot) {
        Thread t = new Thread(robot);
        entityThreads.add(t);
        t.start();
    }
}
