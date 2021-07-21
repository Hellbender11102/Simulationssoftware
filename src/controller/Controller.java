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
    private final List<Thread> robotThreads = new LinkedList<>();
    private Timer repaintTimer;
    private final Timer loggerTimer = new Timer();
    private final Logger logger = new Logger();
    private final JsonLoader jsonLoader = new JsonLoader(logger);

    /**
     * Constructor which loads and start all needed threads
     */
    public Controller() {
        arena = jsonLoader.initArena();
        if (jsonLoader.loadDisplayView()) {
            init(); // loads all entities
            view = new View(arena); // creates view
            repaintTimer(jsonLoader.loadFps()); // sets refresh rate of the view
            addViewListener(); // adds all keybindings
        } else {
            long startTime = System.currentTimeMillis();
            init();
            int timeToSimulate = jsonLoader.loadSimulatedTime();

            /*
             * If logging shall happen in regular timed thread
             * Important! This kind of logging will not determine the passed simulated time
             */
            //startLoggerTimer(1000);

            //start thread on each intractable object
            arena.getPhysicalEntityList().forEach(this::startThread);

            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

            // updates each second how far the current simulation progressed
            while (robotThreads.stream().anyMatch(Thread::isAlive)) {
                int finalTimeToSimulate = timeToSimulate;
                Optional<Long> percentageUntilDone = arena.getRobots().stream()
                        .map(robot -> Math.round((1 - ((double) robot.getTimeToSimulate() / (double) finalTimeToSimulate)) * 100))
                        .reduce(Long::sum);
                System.out.print((percentageUntilDone.map(Math::toIntExact).orElse(0) / arena.getRobots().size()) + "%\r");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    logger.dumpError(interruptedException.getMessage());
                }
            }
            //logs the remaining log entries to the current log file
            logger.saveFullLogToFile(true);
            if (robotThreads.stream().noneMatch(Thread::isAlive)
                    && (logger.saveThread == null || !logger.saveThread.isAlive())) {
                timeToSimulate = jsonLoader.loadSimulatedTime();
                long endTime = System.currentTimeMillis();
                // prints final status and exit
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

    /**
     * loads all entities from the JSON file and adds them to the arena
     */
    void init() {
        Random random = jsonLoader.loadRandom();
        arena.getEntityList().clear();
        arena.addEntities(jsonLoader.loadRobots(random, logger));
        arena.addEntities(jsonLoader.loadBoxes(random));
        arena.addEntities(jsonLoader.loadWalls(random));
        arena.addEntities(jsonLoader.loadAreas(random));
    }


    /**
     * Starts an scheduled timer to repaint the view
     *
     * @param framesPerSecond int
     */
    public void repaintTimer(int framesPerSecond) {
        repaintTimer = new Timer();
        if (framesPerSecond <= 0) {
            logger.dumpError("Started simulation with 0 frames per second.");
            framesPerSecond = 1;
        }
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

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        startStop();
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
                                robot.setNextPoseInMemory();
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
                        view.getSimView().toggleDrawInfosRight();
                        break;
                    case KeyEvent.VK_K:
                        view.getSimView().toggleDrawCenter();
                        break;
                    case KeyEvent.VK_V:
                        view.getSimView().toggleDrawRobotCone();
                        break;
                    case KeyEvent.VK_X:
                        view.getSimView().toggleDrawRobotSignal();
                        break;
                    case KeyEvent.VK_F1:
                        if (stopped) {
                            init();
                        }
                        break;
                    case KeyEvent.VK_F2:
                        logger.saveFullLogToFile(true);
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

        //Menu listener events
        //saves a log to file causes overwriting an if existing
        view.getLog().addActionListener(actionListener -> {
            logger.saveFullLogToFile(false);
        });
        //Restarts the simulation
        view.getItemStartStop().addActionListener(actionListener -> {
            startStop();
        });
        //Restarts the simulation
        view.getRestart().addActionListener(actionListener -> {
            for (RobotInterface robot : arena.getRobots()) {
                if (!stopped) {
                    robot.togglePause();
                }
            }
            stopped = true;
            init();
        });

        view.getItemLoadVariables().addActionListener(actionListener -> {
            String path = view.getPathOfSelectedFile();
            if (path != null)
                jsonLoader.setPathVariables(path);
            jsonLoader.reload();
            init();
        });
        //Restarts the simulation after initializing all resources again
        //Can lead to different behavior without random seed
        view.getFullRestart().addActionListener(actionListener -> {
            for (RobotInterface robot : arena.getRobots()) {
                if (!robot.getPaused()) {
                    robot.togglePause();
                }
            }
            stopped = true;
            arena.clearEntityList();
            jsonLoader.reload();
            arena = jsonLoader.reloadArena();
            view.getSimView().setArena(arena);
            repaintTimer.cancel();
            repaintTimer(jsonLoader.loadFps());
            init();
        });
    }

    /**
     * Starts a thread for any given PhysicalEntity
     * PhysicalEntity extends Runnable
     *
     * @param physicalEntity PhysicalEntity
     */
    private void startThread(PhysicalEntity physicalEntity) {
        Thread t = new Thread(physicalEntity);
        if (RobotInterface.class.isAssignableFrom(physicalEntity.getClass()))
            robotThreads.add(t);
        t.start();
    }

    /**
     *
     */
    private void startStop(){
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
    }
}
