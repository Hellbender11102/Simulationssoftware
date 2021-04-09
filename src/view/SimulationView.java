package view;

import model.Arena;
import model.Position;
import model.Robot;

import javax.swing.*;
import java.awt.*;

public class SimulationView extends JPanel {
    private Arena arena;
    private int offsetX, offsetY;

    public SimulationView(Arena arena) {
        this.arena = arena;
        offsetX = 0;
        offsetY = 0;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(0 - offsetX, 0 - offsetY, 0 - offsetX, arena.getHeight() - offsetY);
        g2d.drawLine(0 - offsetX, 0 - offsetY, arena.getWidth() - offsetX, 0 - offsetY);
        g2d.drawLine(arena.getWidth() - offsetX, arena.getHeight() - offsetY, 0 - offsetX, arena.getHeight() - offsetY);
        g2d.drawLine(arena.getWidth() - offsetX, arena.getHeight() - offsetY, arena.getWidth() - offsetX, 0 - offsetY);
        if (arena.getRobots() != null) {
            for (Robot robot : arena.getRobots()) {
                int x = (int) Math.round(robot.getPose().getxCoordinate()) - offsetX;
                int y = (int) Math.round(robot.getPose().getyCoordinate()) - offsetY;
                g.setColor(robot.getColor());
                g.fillOval(x - robot.getRadius(), y - robot.getRadius(), robot.getDiameters(), robot.getDiameters());
                g.setColor(Color.BLACK);

                Position direction = robot.getPose().getPositionInDirection(robot.trajectorySpeed() * 5);
                g.drawLine(x, y, (int) direction.getxCoordinate() - offsetX, (int) direction.getyCoordinate() - offsetY);
            }
        }
    }

    public void incOffsetX(int amount) {
        Rectangle rectangle = this.getBounds();
        if (-rectangle.width / 2 <= offsetX + amount && arena.getWidth() - rectangle.width / 2 >= offsetX + amount)
            offsetX += amount;
        else if (amount > 0)
            offsetX = arena.getWidth() - rectangle.width / 2;
        else
            offsetX = -rectangle.width / 2;
    }

    public void incOffsetY(int amount) {
        Rectangle rectangle = this.getBounds();
        if (-rectangle.height / 2 <= offsetY + amount && arena.getHeight() - rectangle.height / 2 >= offsetY + amount)
            offsetY += amount;
        else if (amount > 0)
            offsetY = arena.getHeight() - rectangle.height / 2;
        else
            offsetY = -rectangle.height / 2;
    }

}
