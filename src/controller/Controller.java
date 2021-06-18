package controller;

import model.*;
import model.AbstractModel.PhysicalEntity;
import model.AbstractModel.RobotInterface;
import view.View;

import java.awt.event.*;
import java.util.*;

public class Controller {
    private boolean stopped = true;
    private View view;
    private Arena arena;
    private List<Thread> entityThreads = new LinkedList<>();
    private Random random;
    private JsonLoader jsonLoader = new JsonLoader();
    private final Timer repaintTimer = new Timer();
    private final Timer loggerTimer = new Timer();
    private final Logger logger = new Logger();
    private int ticsPerSimulatedSecond;

    public Controller() {
        long startTime = System.currentTimeMillis();
        arena = jsonLoader.initArena();
        ticsPerSimulatedSecond = jsonLoader.loadTicsPerSimulatedSecond();
        if (jsonLoader.loadDisplayView()) {
            init();
            view = new View(arena);
            repaintTimer(jsonLoader.loadFps());
            addViewListener();
        } else {
            init();
            int timeToSimulate = arena.getRobots().get(0).getTimeToSimulate();
            startLoggerTimer(1000);
            arena.getPhysicalEntityList().forEach(this::startThread);
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            while (entityThreads.stream().anyMatch(Thread::isAlive)) {
                int finalTimeToSimulate = timeToSimulate;
                Optional<Long> percantageUntilDone = arena.getRobots().stream()
                        .map(robot -> Math.round((1 - ((double) robot.getTimeToSimulate() / (double) finalTimeToSimulate)) * 100))
                        .reduce(Long::sum);
                System.out.print(
                        (percantageUntilDone.map(Math::toIntExact).orElse(0) / arena.getRobots().size()) + "%\r");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            logger.saveFullLogToFile(true);
            if (entityThreads.stream().noneMatch(Thread::isAlive)
                    && (logger.saveThread == null || !logger.saveThread.isAlive())) {
                timeToSimulate = jsonLoader.loadSimulatedTime();
                long endTime = System.currentTimeMillis();
                System.out.println("Done simulating.\nSimulated "
                        + (timeToSimulate / 60) / 60 + "h " + (timeToSimulate / 60) % 60 + "min " + timeToSimulate % 60 + "sec (" + timeToSimulate + ")");
                System.out.println("That took " + ((endTime - startTime) / 1000) / 60 + " min and " + ((endTime - startTime) / 1000) % 60 + " sec");
                System.exit(0);
            }
        }
    }

    /**
     * Starts an scheduled timer which logs in an set time interval
     *
     * @param logsPerSec int
     */
    public void startLoggerTimer(int logsPerSec) {
        loggerTimer.schedule(new TimerTask() {
            @Override
            public void run() { // logging can be done her

            }
        }, 0, 1000 / logsPerSec);
    }

    void init() {
        random = jsonLoader.loadRandom();
        arena.getEntityList().clear();
        arena.addEntities(jsonLoader.loadRobots(random, logger));
        arena.addEntities(jsonLoader.loadBoxes(random));
        arena.addEntities(jsonLoader.loadWalls(random));
        arena.getEntityList().addAll(jsonLoader.loadAreas(random));
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
                        for (PhysicalEntity entity : arena.getPhysicalEntityList()) {
                            if (stopped) {
                                entity.setToLatestPose();
                                entity.togglePause();
                                startThread(entity);
                            } else {
                                entity.togglePause();
                            }
                        }
                        stopped = !stopped;
                        break;
                    case KeyEvent.VK_B:
                        if (stopped)
                            for (RobotInterface robot : arena.getRobots()) {
                                robot.setPrevPose();
                            }
                        break;
                    case KeyEvent.VK_N:
                        if (stopped)
                            for (RobotInterface robot : arena.getRobots()) {
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
                        view.getSimView().toggleDrawRobotCoordinates();
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
                    case KeyEvent.VK_V:
                        view.getSimView().toggleDrawRobotCone();
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
            for (RobotInterface robot : arena.getRobots()) {
                if (!stopped) {
                    robot.togglePause();
                }
            }
            stopped = true;
            init();
        });
        view.getFullRestart().addActionListener(actionListener -> {
            for (RobotInterface robot : arena.getRobots()) {
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

    private void startThread(PhysicalEntity physicalEntity) {
        Thread t = new Thread(physicalEntity);
        if (RobotInterface.class.isAssignableFrom(physicalEntity.getClass()))
            entityThreads.add(t);
        t.start();
    }
}
