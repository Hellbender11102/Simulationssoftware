package view;

import model.Arena;
import model.Position;
import model.Robot;

import javax.swing.*;
import java.awt.*;

public class SimulationView extends JPanel {
    private Arena arena;
    private int offsetX, offsetY;
    boolean drawLines = false;
    private boolean drawRotationIndicator= true;
    private boolean drawRobotCoordinates = false;
    private boolean drawRobotEngines = false;
    private boolean drawRobotRotationo = false;
    private int fontSize = 10;

    SimulationView(Arena arena) {
        this.arena = arena;
        offsetX = 0;
        offsetY = 0;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));

        g2d.drawString("0,0", -3 - offsetX, arena.getHeight() + 10 - offsetY);
        g2d.drawString("0," + arena.getWidth(), arena.getWidth() - offsetX, arena.getHeight() + 10 - offsetY);
        g2d.drawString(arena.getHeight() + ",0", 0 - offsetX, -3 - offsetY);

        g2d.drawLine(0 - offsetX, 0 - offsetY, 0 - offsetX, arena.getHeight() - offsetY);
        g2d.drawLine(249 - offsetX, 249 - offsetY, 251 - offsetX, 251 - offsetY);
        g2d.drawLine(251 - offsetX, 249 - offsetY, 249 - offsetX, 251 - offsetY);
        g2d.drawLine(0 - offsetX, 0 - offsetY, arena.getWidth() - offsetX, 0 - offsetY);
        g2d.drawLine(arena.getWidth() - offsetX, arena.getHeight() - offsetY, 0 - offsetX, arena.getHeight() - offsetY);
        g2d.drawLine(arena.getWidth() - offsetX, arena.getHeight() - offsetY, arena.getWidth() - offsetX, 0 - offsetY);

        if (drawLines) {
            g.setColor(Color.LIGHT_GRAY);
            for (int i = 10; i < arena.getWidth(); i += 10) {
                g.drawLine(i - offsetX, 1 - offsetY, i - offsetX, arena.getHeight()-1 - offsetY);
            }
            for (int i = 10; i < arena.getHeight(); i += 10) {
                g.drawLine(1 - offsetX, i - offsetY, arena.getWidth()- 1 - offsetX, i - offsetY);
            }
        }

        if (arena.getRobots() != null) {
            for (Robot robot : arena.getRobots()) {
                drawRobot(robot, g);
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

    /**
     * Draws the robot and adds an extra infomation
     *
     * @param robot
     * @param g
     */
    private void drawRobot(Robot robot, Graphics g) {
        int x = (int) Math.round(robot.getPose().getXCoordinate()) - offsetX;
        int y = arena.getHeight() - (int) Math.round(robot.getPose().getYCoordinate()) - offsetY;
        g.setColor(robot.getColor());
        if (!robot.getStop())
            g.fillOval(x - robot.getRadius(), y - robot.getRadius(), robot.getDiameters(), robot.getDiameters());
        else g.drawOval(x - robot.getRadius(), y - robot.getRadius(), robot.getDiameters(), robot.getDiameters());
        g.setColor(Color.BLACK);
        if (drawRotationIndicator) {
            Position direction = robot.getPose().getPositionInDirection(robot.getRadius());
            g.drawLine(x, y, (int) direction.getXCoordinate() - offsetX, arena.getHeight() - (int) direction.getYCoordinate() - offsetY);
        }
        y += robot.getRadius();
        x -= robot.getRadius();

        g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));

        if (drawRobotCoordinates) {
            g.drawString(String.format("%,.2f", robot.getPose().getXCoordinate()) +
                            " | " + String.format("%,.2f", robot.getPose().getYCoordinate()),
                    x - 15 - fontSize, y += fontSize);
        }
        if (drawRobotEngines) {
            g.drawString("R:" + String.format("%,.2f", robot.getEngineR()) +
                            " L:" + String.format("%,.2f", robot.getEngineL()) +
                            " V:" + String.format("%,.2f", robot.trajectorySpeed()),
                    x - 28 - fontSize, y += fontSize);
        }
        if (drawRobotRotationo) {
            g.drawString(String.format("%,.2f", robot.getPose().getRotation()) + "°",
                    x + 8 - fontSize, y + fontSize);
        }
    }

    public void toggleDrawrobotCoordinates() {
        drawRobotCoordinates = !drawRobotCoordinates;
    }

    public void toggleDrawRobotEngines() {
        drawRobotEngines = !drawRobotEngines;
    }

    public void toggleDrawRotationIndicator() {
        drawRotationIndicator = !drawRotationIndicator;
    }

    public void toggleDrawrobotRotationo() {
        drawRobotRotationo = !drawRobotRotationo;
    }

    public void toggleDrawLines() {
        drawLines = !drawLines;
    }

    public void incFontSize(int addend) {
        fontSize += addend + fontSize < 5 ? 0 : addend + fontSize > 100 ? 0 : addend;
    }

}
