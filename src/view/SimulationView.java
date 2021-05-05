package view;

import model.Arena;
import model.Position;
import model.AbstractModel.RobotInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import java.util.List;

public class SimulationView extends JPanel {
    private final Arena arena;
    private List<RobotInterface> classList = new LinkedList<>();
    private int offsetX, offsetY;
    private boolean drawLines = false;
    private boolean drawRotationIndicator = true;
    private boolean drawRobotCoordinates = false;
    private boolean drawRobotEngines = false;
    private boolean drawRobotRotationo = false;
    private boolean drawInClassColor = false;
    private boolean drawCenter = false;
    private boolean infosLeft = false;
    private int fontSize = 10;
    private boolean zoomer = false;
    private double zoomFactor = 3;

    SimulationView(Arena arena) {
        this.arena = arena;
        offsetX = -arena.getWidth() / 2;
        offsetY = -50;
        for (RobotInterface robot : arena.getRobots()) {
            if (!classList.contains(robot.getClass()))
                classList.add(robot);
        }
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (zoomer) {
            AffineTransform at = new AffineTransform();
            at.scale(zoomFactor, zoomFactor);
            g2d.transform(at);
        }
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));

        g2d.drawString("0,0", -3 - offsetX, arena.getHeight() + 10 - offsetY);
        g2d.drawString(arena.getWidth() + ",0", arena.getWidth() - offsetX, arena.getHeight() + 10 - offsetY);
        g2d.drawString("0," + arena.getHeight(), 0 - offsetX, -3 - offsetY);

        g2d.drawLine(0 - offsetX, 0 - offsetY, 0 - offsetX, arena.getHeight() - offsetY);
        g2d.drawLine(0 - offsetX, 0 - offsetY, arena.getWidth() - offsetX, 0 - offsetY);
        g2d.drawLine(arena.getWidth() - offsetX, arena.getHeight() - offsetY, 0 - offsetX, arena.getHeight() - offsetY);
        g2d.drawLine(arena.getWidth() - offsetX, arena.getHeight() - offsetY, arena.getWidth() - offsetX, 0 - offsetY);

        if (drawLines) {
            g2d.setColor(Color.LIGHT_GRAY);
            for (int i = 10; i < arena.getWidth(); i += 10) {
                g2d.drawLine(i - offsetX, 1 - offsetY, i - offsetX, arena.getHeight() - 1 - offsetY);
            }
            for (int i = 10; i < arena.getHeight(); i += 10) {
                g2d.drawLine(1 - offsetX, i - offsetY, arena.getWidth() - 1 - offsetX, i - offsetY);
            }
        }
        if (drawCenter)
            for (RobotInterface robot : classList) {
                Position position = robot.centerOfGroupWithClasses(List.of(robot.getClass()));
                g2d.setColor(robot.getClassColor());
                g2d.drawOval((int) position.getXCoordinate() - offsetX,
                        arena.getHeight() - (int) position.getYCoordinate() - offsetY,
                        2, 2);
            }

        if (arena.getRobots() != null) {
            int x = arena.getWidth() - offsetX + 35 + fontSize, y = -offsetY - fontSize * 5, n = 0;
            for (RobotInterface robot : arena.getRobots()) {
                drawRobot(robot, g);
                if (!infosLeft) {
                    x = (int) Math.round(robot.getPose().getXCoordinate() - offsetX - robot.getRadius());
                    y = arena.getHeight() - (int) Math.round(robot.getPose().getYCoordinate() - offsetY + robot.getRadius());
                } else {
                    x += 0;
                    y += fontSize * 5;
                    if (n % 7 == 0 && n != 0) {
                        y -= 35 * fontSize;
                        x += 5 + fontSize * 10;
                    }
                }
                n++;
                drawInfo(g, robot, x, y);
            }
        }
    }


    /**
     * Draws the robot and adds an extra infomation
     *
     * @param robot RobotInterface
     * @param g2d   Graphics
     */
    private void drawRobot(RobotInterface robot, Graphics g2d) {
        int x = (int) Math.round(robot.getPose().getXCoordinate()) - offsetX;
        int y = arena.getHeight() - (int) Math.round(robot.getPose().getYCoordinate()) - offsetY;
        if (!drawInClassColor)
            g2d.setColor(robot.getColor());
        else g2d.setColor(robot.getClassColor());
        if (!robot.getStop())
            g2d.fillOval(x - (int)robot.getRadius(), y - (int) robot.getRadius(), (int) robot.getDiameters(), (int) robot.getDiameters());
        else
            g2d.drawOval(x - (int)robot.getRadius(), y - (int) robot.getRadius(), (int) robot.getDiameters(), (int) robot.getDiameters());
        g2d.setColor(Color.BLACK);
        if (drawRotationIndicator) {
            Position direction = robot.getPose().getPositionInDirection(robot.getRadius());
            g2d.drawLine(x, y, (int) direction.getXCoordinate() - offsetX, arena.getHeight() - (int) direction.getYCoordinate() - offsetY);
        }
    }

    private void drawInfo(Graphics g2d, RobotInterface robot, int x, int y) {
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
        if (infosLeft && (drawRobotCoordinates || drawRobotEngines || drawRobotRotationo)) {
            g2d.setColor(robot.getColor());
            g2d.fillOval(x, y, fontSize, fontSize);
            if (drawInClassColor) {
                g2d.setColor(robot.getClassColor());
                g2d.fillOval(x + fontSize, y, fontSize, fontSize);
            }
            y += fontSize;
        }
        g2d.setColor(Color.black);
        if (drawRobotCoordinates) {
            g2d.drawString(String.format("%,.2f", robot.getPose().getXCoordinate()) +
                            " | " + String.format("%,.2f", robot.getPose().getYCoordinate()),
                    x - 15 - fontSize, y += fontSize);
        }
        if (drawRobotEngines) {
            g2d.drawString("R:" + String.format("%,.2f", robot.getEngineR()) +
                            " L:" + String.format("%,.2f", robot.getEngineL()) +
                            " V:" + String.format("%,.2f", robot.trajectorySpeed()),
                    x - 28 - fontSize, y += fontSize);
        }
        if (drawRobotRotationo) {
            g2d.drawString(String.format("%,.2f", robot.getPose().getRotation() / Math.PI * 180) + "° | " +
                            String.format("%,.2f", robot.getPose().getRotation() / Math.PI) + " *Pi",
                    x - 16 - fontSize, y + fontSize);
        }
    }

    public void incOffsetX(int amount) {
        Rectangle rectangle = this.getBounds();
        offsetX = calcBorders(amount, arena.getWidth(), rectangle.width, offsetX);
    }

    public void incOffsetY(int amount) {
        Rectangle rectangle = this.getBounds();
        offsetY = calcBorders(amount, arena.getHeight(), rectangle.height, offsetY);
    }

    int calcBorders(int amount, int border, int screenSize, int offset) {
        if (-screenSize / 2 <= offset + amount &&
                (border >= offset + amount && infosLeft) ||
                (border - screenSize / 2 >= offset + amount))
            return offset + amount;
        else if (amount > 0)
            if (infosLeft)
                return border;
            else
                return border - screenSize / 2;
        else
            return -screenSize / 2;
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

    public void toggleDrawTypeInColor() {
        drawInClassColor = !drawInClassColor;
    }

    public void toggleDrawInfosLeft() {
        infosLeft = !infosLeft;
    }

    public void toggleDrawCenter() {
        drawCenter = !drawCenter;
    }

    public void incFontSize(int addend) {
        fontSize += addend + fontSize < 10 ? 0 : addend + fontSize > 30 ? 0 : addend;
    }

    public void incZoom() {
        zoomer = true;
        zoomFactor *= 1.1;
        repaint();
    }

    public void decZoom() {
        zoomer = true;
        zoomFactor /= 1.1;
        repaint();
    }

}
