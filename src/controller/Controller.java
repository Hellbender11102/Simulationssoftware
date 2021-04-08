package controller;

import model.Arena;
import model.Pose;
import model.Position;
import model.Robot;
import view.View;

import java.awt.event.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                    inArenaBounds();
                    collisionDetection();
                }
                view.repaint();
            }
        }, 1000, 1000 / framesPerSecond);
    }


    private synchronized Robot convertPoseToGlobal(Position global, Robot robot) {
        Pose pose = transPos(global, robot.getLocalPose());
        robot.getLocalPose().setxCoordinate(pose.getxCoordinate());
        robot.getLocalPose().setyCoordinate(pose.getyCoordinate());
        robot.getLocalPose().setRotation(pose.getRotation());
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
            double halfSizeX = robot.getWidth() / 2.0;
            double halfSizeY = robot.getHeight() / 2.0;
            if (robot.getLocalPose().getxCoordinate() < halfSizeX)
                robot.getLocalPose().setxCoordinate(halfSizeX);
            else if (robot.getLocalPose().getxCoordinate() > arena.getWidth() - halfSizeX)
                robot.getLocalPose().setxCoordinate(arena.getWidth() - halfSizeX);
            if (robot.getLocalPose().getyCoordinate() < halfSizeY)
                robot.getLocalPose().setyCoordinate(halfSizeY);
            else if (robot.getLocalPose().getyCoordinate() > arena.getHeight() - halfSizeY)
                robot.getLocalPose().setyCoordinate(arena.getHeight() - halfSizeY);
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
                if (!r1.equals(r2) && r1.getLocalPose().euclideanDistance(r2.getLocalPose()) < r1.getWidth()) {
                    if (r2.isPositionInRobotArea(r1.getLocalPose().getPositionInDirection(0))) {
                        bump(r1, r2);
                    }
                    if (r1.isPositionInRobotArea(r2.getLocalPose().getPositionInDirection(0))) {
                        bump(r2, r1);
                    }
                }
            });
        });
    }

    private void bump(Robot bumping, Robot getsBumped) {
        Position r1NextPosition = bumping.getLocalPose().getPositionInDirection(bumping.trajectorySpeed());
        Pose r1Pose = bumping.getLocalPose();
        System.out.println((r1NextPosition.getxCoordinate() - r1Pose.getxCoordinate()));
        getsBumped.getLocalPose().setxCoordinate(getsBumped.getLocalPose().getxCoordinate() + (r1NextPosition.getxCoordinate() - r1Pose.getxCoordinate()));
        getsBumped.getLocalPose().setyCoordinate(getsBumped.getLocalPose().getxCoordinate() + (r1NextPosition.getyCoordinate() - r1Pose.getyCoordinate()));
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
