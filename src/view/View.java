package view;

import model.Position;
import model.Robot;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.awt.Graphics;

public class View extends JFrame {
    private Position singleRobotPos;
    private LinkedList<Robot> robots;

    public View() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width - (int) (screenSize.width * 0.2), screenSize.height - (int) (screenSize.height * 0.2));
        this.setVisible(true);
    }


    public void paint(Graphics g) {
        if (robots != null)
            for (Robot robot : robots) {
                g.setColor(robot.getColor());
                g.fillRect((int) Math.round(robot.getLocalPosition().getxCoordinate()), (int) Math.round(robot.getLocalPosition().getyCoordinate()),
                        10, 10);
                g.setColor(Color.red);
                g.drawLine((int) robot.getLocalPosition().getxCoordinate() + 5, (int) robot.getLocalPosition().getyCoordinate() + 5,
                        (int) robot.getLocalPosition().getxCoordinate() + 5, (int) robot.getLocalPosition().getyCoordinate() + 5);
            }
    }

    public void setRobot(LinkedList<Robot> localPositions) {
        this.robots = localPositions;
    }
}
