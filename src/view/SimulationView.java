package view;

import model.Arena;
import model.Pose;
import model.Position;
import model.Robot;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

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
                int x = (int) Math.round(robot.getLocalPose().getxCoordinate()) - offsetX;
                int y = (int) Math.round(robot.getLocalPose().getyCoordinate()) - offsetY;
                g.setColor(robot.getColor());
                g.fillOval(x - robot.getHeight() / 2, y - robot.getWidth() / 2, robot.getWidth(), robot.getHeight());
                g.setColor(Color.BLACK);
                Position direction = calcDirection(robot.getLocalPose().getRotation(), x, y);
                g.drawLine(x, y, (int) direction.getxCoordinate(), (int) direction.getyCoordinate());
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

    private Position calcDirection(double rotation, int x, int y) {
        double x2 = 0, y2 = 0;
        double small = rotation % 90;
        if (rotation == 90) {
            x2 = x;
            y2 = y + 10;
        } else if (rotation == 180) {
            x2 = x - 10;
            y2 = y;
        } else if (rotation == 270) {
            x2 = x;
            y2 = y - 10;
        } else if (rotation == 0) {
            x2 = x + 10;
            y2 = y;
        } else if (rotation < 90.0) {
            x2 = x + (10 * (1 - small / 90));
            y2 = y + (10 * (small / 90));
        } else if (rotation < 180.0) {
            x2 = x - (10 * (small / 90));
            y2 = y + (10 * (1 - small / 90));
        } else if (rotation < 270.0) {
            x2 = x - (10 * (1 - small / 90));
            y2 = y - (10 * (small / 90));
        } else if (rotation < 360.0) {
            x2 = x + (10 * (small / 90));
            y2 = y - (10 * (1 - small / 90));
        }
        return new Position(x2, y2);
    }

}
