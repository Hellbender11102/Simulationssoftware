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
                        Robot r = robotConcurrentQueue.poll();
                        robotList.add(r);
                    }
                    arena.setRobots(robotList);
                    collisionDetection();
                    inArenaBounds();
                }
                view.repaint();
            }
        }, 1000, 1000 / framesPerSecond);
    }


    private synchronized Robot convertPoseToGlobal(Position global, Robot robot) {
        Pose pose = transPos(global, robot.getPose());
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

    private void inArenaBounds() {
        for (Robot robot : arena.getRobots()) {
            if (robot.getPose().getXCoordinate() < robot.getRadius())
                robot.getPose().setXCoordinate(robot.getRadius());
            else if (robot.getPose().getXCoordinate() > arena.getWidth() - robot.getRadius())
                robot.getPose().setXCoordinate(arena.getWidth() - robot.getRadius());
            if (robot.getPose().getYCoordinate() < robot.getRadius())
                robot.getPose().setYCoordinate(robot.getRadius());
            else if (robot.getPose().getYCoordinate() > arena.getHeight() - robot.getRadius())
                robot.getPose().setYCoordinate(arena.getHeight() - robot.getRadius());
        }
    }

    private void collisionDetection() {
        arena.getRobots().forEach((r1) -> {
            arena.getRobots().forEach((r2) -> {
                if (!r1.equals(r2) && r1.getPose().euclideanDistance(r2.getPose()) < r1.getDiameters()) {
                    if (r2.isPositionInRobotArea(r1.getPose().getPositionInDirection(r1.getRadius() + 0.01))) {

                        bump(r1, r2, r1.getPose().getPositionInDirection(r1.trajectorySpeed()));
                    } else if (r1.isPositionInRobotArea(r2.getPose().getPositionInDirection(r2.getRadius() + 0.01))) {

                        bump(r2, r1, r2.getPose().getPositionInDirection(r2.trajectorySpeed()));
                    } else if (!r1.isPositionInRobotArea(r2.getPose().getPositionInDirection(r2.getRadius() + 0.01))) {

                        if (r1.getPose().getXCoordinate() < r2.getPose().getXCoordinate()) {
                            bump(r1, r2, new Position(r1.getPose().getXCoordinate() + r1.trajectorySpeed(), r1.getPose().getYCoordinate()));
                            bump(r2, r1, new Position(r2.getPose().getXCoordinate() - r2.trajectorySpeed(), r2.getPose().getYCoordinate()));
                        } else {
                            bump(r1, r2, new Position(r1.getPose().getXCoordinate() - r1.trajectorySpeed(), r1.getPose().getYCoordinate()));
                            bump(r2, r1, new Position(r2.getPose().getXCoordinate() + r2.trajectorySpeed(), r2.getPose().getYCoordinate()));
                        }
                        if (r1.getPose().getYCoordinate() < r2.getPose().getYCoordinate()) {
                            bump(r1, r2, new Position(r1.getPose().getXCoordinate(), r1.getPose().getYCoordinate() + r1.trajectorySpeed()));
                            bump(r2, r1, new Position(r2.getPose().getXCoordinate(), r2.getPose().getYCoordinate() - r2.trajectorySpeed()));
                        } else {
                            bump(r1, r2, new Position(r1.getPose().getXCoordinate(), r1.getPose().getYCoordinate() - r1.trajectorySpeed()));
                            bump(r2, r1, new Position(r2.getPose().getXCoordinate(), r2.getPose().getYCoordinate() + r2.trajectorySpeed()));
                        }
                    } else System.out.println("Alles doof");
                }
            });
        });
    }

    private void bump(Robot bumping, Robot getsBumped, Position positionInBumpDirection) {
        Position vector = bumping.getPose().getDiffrence(positionInBumpDirection);
        getsBumped.getPose().decPosition(vector);

        if (getsBumped.getPose().getXCoordinate() <= getsBumped.getRadius()) {
            bumping.getPose().incPosition(vector.getXCoordinate(), 0);
        } else if (getsBumped.getPose().getXCoordinate() >= arena.getWidth() - getsBumped.getRadius()) {
            bumping.getPose().incPosition(vector.getXCoordinate(), 0);
        }
        if (getsBumped.getPose().getYCoordinate() <= bumping.getRadius()) {
            bumping.getPose().incPosition(0, vector.getYCoordinate());
        } else if (getsBumped.getPose().getYCoordinate() >= arena.getHeight() - getsBumped.getRadius()) {
            bumping.getPose().incPosition(0, vector.getYCoordinate());
        }
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
