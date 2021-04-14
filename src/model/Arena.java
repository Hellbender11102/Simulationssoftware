package model;

import model.Robot.RobotInterface;

import java.util.LinkedList;

public class Arena {
    private LinkedList<RobotInterface> robotList = new LinkedList<>();
    private final int height, width;
    private static Arena singleton;

    /**
     * Constructor
     *
     * @param width  in centemeter
     * @param height in centemeter
     */
    synchronized
    public static Arena getInstance(int width, int height) {

        if (singleton == null) {
            singleton = new Arena(width, height);
        }
        return singleton;
    }

    private Arena(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "width:" + singleton.width + " height:" + singleton.height;
    }

    synchronized public void setRobots(LinkedList<RobotInterface> robotList) {
        singleton.robotList = robotList;
    }

    synchronized public LinkedList<RobotInterface> getRobots() {
        return singleton.robotList;
    }

    public int getHeight() {
        return singleton.height;
    }

    public int getWidth() {
        return singleton.width;
    }
}
