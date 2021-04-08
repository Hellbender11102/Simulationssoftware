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
        robot.getPose().setxCoordinate(pose.getxCoordinate());
        robot.getPose().setyCoordinate(pose.getyCoordinate());
        robot.getPose().setRotation(pose.getRotation());
        return robot;
    }


    private synchronized Pose transPos(Position pGlobal, Pose pLocal) {
        double x = pGlobal.getxCoordinate() + pLocal.getxCoordinate();
        double y = pGlobal.getyCoordinate() + pLocal.getyCoordinate();
        double rotation = pLocal.getRotation();
        return new Pose(x, y, rotation);
    }

    private void inArenaBounds() {
        for (Robot robot : arena.getRobots()) {
            if (robot.getPose().getxCoordinate() < robot.getRadius())
                robot.getPose().setxCoordinate(robot.getRadius());
            else if (robot.getPose().getxCoordinate() > arena.getWidth() - robot.getRadius())
                robot.getPose().setxCoordinate(arena.getWidth() - robot.getRadius());
            if (robot.getPose().getyCoordinate() < robot.getRadius())
                robot.getPose().setyCoordinate(robot.getRadius());
            else if (robot.getPose().getyCoordinate() > arena.getHeight() - robot.getRadius())
                robot.getPose().setyCoordinate(arena.getHeight() - robot.getRadius());
        }
    }

    private void collisionDetection() {
        List<Robot> robotsColliding = new LinkedList<>();
/*        List<List<Robot>> result = arena.getRobots().stream().map(
                r1 -> arena.getRobots().stream().filter(
                        r2 -> !r1.equals(r2) && r1.getLocalPose().euclideanDistance(r2.getLocalPose()) < r1.getWidth() * 2).collect(Collectors.toList()))
                .collect(Collectors.toList());*/
        arena.getRobots().forEach((r1) -> {
            arena.getRobots().forEach((r2) -> {
                if (!r1.equals(r2) && r1.getPose().euclideanDistance(r2.getPose()) < r1.getDiameters()) {
                    Pose oldPose = r2.getPose();
                    if (r2.isPositionInRobotArea(r1.getPose().getPositionInDirection(0)))
                    //    bump(r1, r2);
                    if (r1.isPositionInRobotArea(r2.getPose().getPositionInDirection(0))){}
                    //    bump(r2, r1);
                    double shiftedX = r2.getPose().getxCoordinate() - r1.getPose().getxCoordinate();
                    double shiftedY = r2.getPose().getyCoordinate() - r1.getPose().getyCoordinate();
                    if (r1.getPose().getxCoordinate() < oldPose.getxCoordinate()) {
                        r1.getPose().setxCoordinate(oldPose.getxCoordinate() - (r1.getRadius() + r2.getRadius()));
                    } else
                        r1.getPose().setxCoordinate(oldPose.getxCoordinate() + (r1.getRadius() + r2.getRadius()));

                    if (r1.getPose().getyCoordinate() < r2.getPose().getyCoordinate()) {
                        r1.getPose().setyCoordinate(oldPose.getyCoordinate() -(r1.getRadius() + r2.getRadius()));
                    } else
                        r1.getPose().setyCoordinate(oldPose.getyCoordinate() + (r1.getRadius() + r2.getRadius()));
                }
            });
        });
    }

    private void bump(Robot bumping, Robot getsBumped) {
        Position positionInBumpDirection = bumping.getPose().getPositionInDirection(bumping.trajectorySpeed());
        Pose bumpPose = bumping.getPose();
        System.out.println(getsBumped);
        getsBumped.getPose().setxCoordinate(getsBumped.getPose().getxCoordinate() + (positionInBumpDirection.getxCoordinate() - bumpPose.getxCoordinate()));
        getsBumped.getPose().setyCoordinate(getsBumped.getPose().getyCoordinate() + (positionInBumpDirection.getyCoordinate() - bumpPose.getyCoordinate()));
        System.out.println(getsBumped);
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
