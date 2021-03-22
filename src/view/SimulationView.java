package view;

import model.Position;
import model.Robot;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

public class SimulationView extends JPanel {

    private LinkedList<Robot> robots;

    public void paint(Graphics g) {
        if (robots != null) {
            for (Robot robot : robots) {
                g.setColor(robot.getColor());
                g.fillRect((int) Math.round(robot.getLocalPosition().getxCoordinate()), (int) Math.round(robot.getLocalPosition().getyCoordinate()),
                        robot.getWidth(), robot.getHeight());
            }
        }
    }

    void setRobot(LinkedList<Robot> localPositions) {
        this.robots = localPositions;
    }
}
