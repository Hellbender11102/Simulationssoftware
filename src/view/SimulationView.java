package view;

import model.Arena;
import model.Robot;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class SimulationView extends JPanel {
    Arena arena;
    int offsetX, offsetY;

    public SimulationView(Arena arena) {
        this.arena = arena;
        System.out.println(getSize().width+" "+ arena.getWidth());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width , screenSize.height - (int) (screenSize.height * 0.04));
        offsetX = (arena.getWidth()-screenSize.width) / 2;
        offsetY = (arena.getHeight()- (int) (screenSize.height-screenSize.height * 0.1)) / 2;
    }

    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawLine(0 - offsetX, 0 - offsetY, 0 - offsetX, arena.getHeight() - offsetY);
        g.drawLine(0 - offsetX, 0 - offsetY, arena.getWidth() - offsetX, 0 - offsetY);
        g.drawLine(arena.getWidth() - offsetX, arena.getHeight() - offsetY, 0 - offsetX, arena.getHeight() - offsetY);
        g.drawLine(arena.getWidth() - offsetX, arena.getHeight() - offsetY, arena.getWidth() - offsetX, 0 - offsetY);
        if (arena.getRobots() != null) {
            for (Robot robot : arena.getRobots()) {
                int x = (int) Math.round(robot.getLocalPosition().getxCoordinate()) - offsetX;
                int y = (int) Math.round(robot.getLocalPosition().getyCoordinate()) - offsetY;
                g.setColor(robot.getColor());
                g.fillOval(x, y, robot.getWidth(), robot.getHeight());
            }
        }
    }

    public void incOffsetX(int amount) {
        offsetX += amount;
    }

    public void incOffsetY(int amount) {
        offsetY += amount;
    }
}
