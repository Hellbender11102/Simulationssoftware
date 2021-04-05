package view;

import model.Robot;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class SimulationView extends JPanel {

    private LinkedList<Robot> robots;

    public void paint(Graphics g) {
        if (robots != null) {
            g.setPaintMode();
            for (Robot robot : robots) {
                int x = (int) Math.round(robot.getLocalPosition().getxCoordinate());
                int y = (int) Math.round(robot.getLocalPosition().getyCoordinate());
                g.setColor(robot.getColor());
                g.fillOval(x, y, robot.getWidth(), robot.getHeight());
            }
        }
    }

    void setRobot(LinkedList<Robot> localPositions) {
        this.robots = localPositions;
        repaint();
    }
}
