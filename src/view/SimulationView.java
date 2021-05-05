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
    private double zoomFactor = 1;

    SimulationView(Arena arena) {
        this.arena = arena;
        offsetX = 0;
        offsetY = 0;
        for (RobotInterface robot : arena.getRobots()) {
            if (!classList.contains(robot.getClass()))
                classList.add(robot);
        }
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawString("0,0", convertZoom(-3 - offsetX), convertZoom(arena.getHeight() + 10 - offsetY));
        g2d.drawString(arena.getWidth() + ",0", convertZoom(arena.getWidth() - offsetX), convertZoom(arena.getHeight() + 10 - offsetY));
        g2d.drawString("0," + arena.getHeight(), convertZoom(0 - offsetX), convertZoom(-3 - offsetY));

        g2d.drawLine(convertZoom(0 - offsetX), convertZoom(0 - offsetY), convertZoom(0 - offsetX), convertZoom(arena.getHeight() - offsetY));
        g2d.drawLine(convertZoom(0 - offsetX), convertZoom(0 - offsetY), convertZoom(arena.getWidth() - offsetX), convertZoom(0 - offsetY));
        g2d.drawLine(convertZoom(arena.getWidth() - offsetX), convertZoom(arena.getHeight() - offsetY), convertZoom(0 - offsetX), convertZoom(arena.getHeight() - offsetY));
        g2d.drawLine(convertZoom(arena.getWidth() - offsetX), convertZoom(arena.getHeight() - offsetY), convertZoom(arena.getWidth() - offsetX), convertZoom(0 - offsetY));

        if (drawLines) {
            g2d.setColor(Color.LIGHT_GRAY);
            for (int i = 10; i < arena.getWidth(); i += 10) {
                g2d.drawLine(convertZoom(i - offsetX), convertZoom(1 - offsetY), convertZoom(i - offsetX), convertZoom(arena.getHeight() - 1 - offsetY));
            }
            for (int i = 10; i < arena.getHeight(); i += 10) {
                g2d.drawLine(convertZoom(1 - offsetX), convertZoom(i - offsetY), convertZoom(arena.getWidth() - 1 - offsetX), convertZoom(i - offsetY));
            }
        }
        if (drawCenter)
            for (RobotInterface robot : classList) {
                Position position = robot.centerOfGroupWithClasses(List.of(robot.getClass()));
                g2d.setColor(robot.getClassColor());
                g2d.drawOval(convertZoom((int) position.getXCoordinate() - offsetX),
                        convertZoom(arena.getHeight() - (int) position.getYCoordinate() - offsetY)
                        , convertZoom(2), convertZoom(2));
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
        if (!robot.getPaused())
            g2d.fillOval(convertZoom(x - (int) robot.getRadius()), convertZoom(y - (int) robot.getRadius()), convertZoom((int) robot.getDiameters()), convertZoom((int) robot.getDiameters()));
        else
            g2d.drawOval(convertZoom(x - (int) robot.getRadius()), convertZoom(y - (int) robot.getRadius()), convertZoom((int) robot.getDiameters()), convertZoom((int) robot.getDiameters()));
        g2d.setColor(Color.BLACK);
        if (drawRotationIndicator) {
            Position direction = robot.getPose().getPositionInDirection(robot.getRadius());
            g2d.drawLine(convertZoom(x), convertZoom(y), convertZoom((int) direction.getXCoordinate() - offsetX), convertZoom(arena.getHeight() - (int) direction.getYCoordinate() - offsetY));
        }
    }

    private void drawInfo(Graphics g2d, RobotInterface robot, int x, int y) {
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
        if (infosLeft && (drawRobotCoordinates || drawRobotEngines || drawRobotRotationo)) {
            g2d.setColor(robot.getColor());
            g2d.fillOval(convertZoom(x), convertZoom(y), convertZoom(fontSize), convertZoom(fontSize));
            if (drawInClassColor) {
                g2d.setColor(robot.getClassColor());
                g2d.fillOval(convertZoom(x + fontSize), convertZoom(y), convertZoom(fontSize), convertZoom(fontSize));
            }
            y += fontSize;
        }
        g2d.setColor(Color.black);
        if (drawRobotCoordinates) {
            y += fontSize;
            g2d.drawString(String.format("%,.2f", robot.getPose().getXCoordinate()) +
                            " | " + String.format("%,.2f", robot.getPose().getYCoordinate())
                    , convertZoom(x - 15 - fontSize), convertZoom(y));
        }
        if (drawRobotEngines) {
            y += fontSize;
            g2d.drawString("R:" + String.format("%,.2f", robot.getEngineR()) +
                            " L:" + String.format("%,.2f", robot.getEngineL()) +
                            " V:" + String.format("%,.2f", robot.trajectorySpeed())
                    , convertZoom(x - 28 - fontSize), convertZoom(y));
        }
        if (drawRobotRotationo) {
            g2d.drawString(String.format("%,.2f", robot.getPose().getRotation() / Math.PI * 180) + "° | " +
                            String.format("%,.2f", robot.getPose().getRotation() / Math.PI) + " *Pi"
                    , convertZoom(x - 16 - fontSize), convertZoom(y + fontSize));
        }
    }

    public void incOffsetX(int amount) {
        offsetX = calcBorders(amount, arena.getWidth(), offsetX);
    }

    public void incOffsetY(int amount) {
        offsetY = calcBorders(amount, arena.getHeight(), offsetY);
    }

    private int convertZoom(int numer) {
        return (int) (numer * zoomFactor);
    }

    private int calcBorders(int amount, int border, int offset) {
        if (-border / 2 < offset + amount && offset + amount < border + border / 2)
            return offset + amount;
        else if (-border / 2 > offset + amount) return -border / 2;
        else if (offset + amount > border + border / 2) return border + border / 2;
        else return offset;
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
        zoomFactor *= 1.1;
        repaint();
    }

    public void decZoom() {
        zoomFactor /= 1.1;
        repaint();
    }

}
