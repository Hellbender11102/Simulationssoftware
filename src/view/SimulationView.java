package view;

import model.AbstractModel.PhysicalEntity;
import model.Area;
import model.Arena;
import model.Position;
import model.AbstractModel.RobotInterface;
import model.RobotTypes.BaseVisionConeRobot;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

public class SimulationView extends JPanel {
    private final Arena arena;
    // Collects all different classes of robots
    private List<RobotInterface> classList = new LinkedList<>();
    private int offsetX, offsetY;
    private boolean drawLines = false;
    private boolean drawRotationIndicator = true;
    private boolean drawRobotCoordinates = false;
    private boolean drawRobotEngines = false;
    private boolean drawRobotRotation = false;
    private boolean drawInClassColor = false;
    private boolean drawCenter = false;
    private boolean infosLeft = false;
    private boolean drawRobotCone = true;
    private boolean drawRobotSignal = true;
    private int fontSize = 10;
    private double zoomFactor = 5;

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
        g2d.drawString("0,0", -3 - convertZoom(offsetX), convertZoom(arena.getHeight() - offsetY) + 10);
        g2d.drawString(arena.getWidth() + ",0", convertZoom(arena.getWidth() - offsetX), convertZoom(arena.getHeight() - offsetY) + 10);
        g2d.drawString("0," + arena.getHeight(), convertZoom(-offsetX), convertZoom(-offsetY) - 10);

        g2d.drawLine(convertZoom(-offsetX), convertZoom(-offsetY), convertZoom(-offsetX), convertZoom(arena.getHeight() - offsetY));
        g2d.drawLine(convertZoom(-offsetX), convertZoom(-offsetY), convertZoom(arena.getWidth() - offsetX), convertZoom(-offsetY));
        g2d.drawLine(convertZoom(arena.getWidth() - offsetX), convertZoom(arena.getHeight() - offsetY), convertZoom(-offsetX), convertZoom(arena.getHeight() - offsetY));
        g2d.drawLine(convertZoom(arena.getWidth() - offsetX), convertZoom(arena.getHeight() - offsetY), convertZoom(arena.getWidth() - offsetX), convertZoom(-offsetY));
        if (drawLines) {
            g2d.setColor(Color.LIGHT_GRAY);
            for (int i = 10; i < arena.getWidth(); i += 10) {
                g2d.drawLine(convertZoom(i - offsetX), convertZoom(1 - offsetY), convertZoom(i - offsetX), convertZoom(arena.getHeight() - 1 - offsetY));
            }
            for (int i = 10; i < arena.getHeight(); i += 10) {
                g2d.drawLine(convertZoom(1 - offsetX), convertZoom(i - offsetY), convertZoom(arena.getWidth() - 1 - offsetX), convertZoom(i - offsetY));
            }
        }
        if (arena.getAreaList() != null) {
            drawAreas(g2d);
        }
        if (arena.getPhysicalEntitiesWithoutRobots() != null) {
            drawSquares(g2d);
        }
        if (drawCenter)
            for (RobotInterface robot : classList) {
                Position position = robot.centerOfGroupWithClasses(List.of(robot.getClass()));
                g2d.setColor(robot.getClassColor());
                g2d.drawOval(convertZoom(position.getX() - offsetX),
                        convertZoom(arena.getHeight() - position.getY() - offsetY)
                        , convertZoom(2), convertZoom(2));
            }
        if (arena.getRobots() != null) {
            int x = convertZoom(arena.getWidth() - offsetX + fontSize) + 35, y = -convertZoom(offsetY) - fontSize * 5, n = 0;
            for (RobotInterface robot : arena.getRobots()) {
                drawRobot(robot, g2d);
                if (!infosLeft) {
                    x = convertZoom(robot.getPose().getX() - offsetX - robot.getRadius());
                    y = convertZoom(arena.getHeight() - (robot.getPose().getY() + offsetY) + robot.getRadius());
                } else {
                    x += 0;
                    y += fontSize * 5;
                    if (n % 11 == 0 && n != 0) {
                        y -= 55 * fontSize;
                        x += 5 + fontSize * 10;
                    }
                }
                n++;
                drawInfo(g, robot, x, y);
            }
        }
    }

    /**
     * Draws the areas
     *
     * @param g2d Graphics2D
     */
    private void drawAreas(Graphics2D g2d) {
        for (Area area : arena.getAreaList()) {
            g2d.setColor(new Color(area.getColor().getRed(), area.getColor().getGreen(), area.getColor().getBlue(), 200));
            g2d.fillOval(convertZoom((area.getPose().getX() - offsetX - (area.getNoticeableDistanceDiameter()) / 2)),
                    convertZoom(arena.getHeight() - (area.getPose().getY() + offsetY + (area.getNoticeableDistanceDiameter()) / 2)),
                    convertZoom((area.getNoticeableDistanceDiameter())),
                    convertZoom((area.getNoticeableDistanceDiameter())));
            g2d.setColor(area.getClassColor());
            g2d.drawOval(convertZoom((area.getPose().getX() - offsetX - area.getWidth() / 2)),
                    convertZoom(arena.getHeight() - (area.getPose().getY() + offsetY + (area.getHeight() / 2))),
                    convertZoom(area.getWidth()),
                    convertZoom(area.getHeight()));
        }
    }

    /**
     * Draws all physical entities except robots as squares
     *
     * @param g2d Graphics2D
     */
    private void drawSquares(Graphics2D g2d) {
        for (PhysicalEntity entity : arena.getPhysicalEntitiesWithoutRobots()) {
            g2d.setColor(entity.getClassColor());
            g2d.fillRect(convertZoom((entity.getPose().getX() - entity.getWidth() / 2) - offsetX),
                    convertZoom(arena.getHeight() - (entity.getPose().getY() + entity.getHeight() / 2) - offsetY),
                    convertZoom(entity.getWidth()), convertZoom(entity.getHeight()));

            Rectangle2D rectangle2D = new Rectangle2D.Double(convertZoom(entity.getPose().getX()-entity.getWidth()/2- offsetX),
                    convertZoom(arena.getHeight() -entity.getPose().getY()-entity.getHeight()/2- offsetY), convertZoom(entity.getWidth()), convertZoom(entity.getHeight()));
            g2d.fill(rectangle2D);
        }
    }


    /**
     * Draws the robot and adds an extra information
     *
     * @param robot RobotInterface
     * @param g2d   Graphics2D
     */
    private void drawRobot(RobotInterface robot, Graphics2D g2d) {
        double x = robot.getPose().getX() - offsetX - robot.getRadius();
        double y = arena.getHeight() - robot.getPose().getY() - offsetY - robot.getRadius();
        if (BaseVisionConeRobot.class.isAssignableFrom(robot.getClass()) && drawRobotCone) {
            drawVisionCone((BaseVisionConeRobot) robot, g2d);
        }
        if (!drawInClassColor)
            g2d.setColor(robot.getColor());
        else g2d.setColor(robot.getClassColor());
        if (!robot.getPaused())
            g2d.fillOval(convertZoom(x), convertZoom(y), convertZoom(robot.getDiameters()), convertZoom(robot.getDiameters()));
        else
            g2d.drawOval(convertZoom(x), convertZoom(y), convertZoom(robot.getDiameters()), convertZoom(robot.getDiameters()));
        g2d.setColor(Color.BLACK);
        if (drawRotationIndicator) {
            Position direction = robot.getPose().getPositionInDirection(robot.getRadius());
            drawLine(robot.getPose(), direction, g2d);
        }
        if (drawRobotSignal) {
            if (robot.getSignal())
                g2d.setColor(Color.red);
            else g2d.setColor(Color.gray);
            Position positionSignal = robot.getPose().getPositionInDirection(robot.getRadius() / 2, robot.getPose().getRotation() - Math.PI);
            double radiusOffset = robot.getRadius() * 0.6;
            g2d.fillOval(convertZoom(positionSignal.getX() - offsetX - radiusOffset / 2), convertZoom(arena.getHeight() - positionSignal.getY() - offsetY - radiusOffset / 2),
                    convertZoom(radiusOffset), convertZoom(radiusOffset));
        }
    }

    /**
     * Draws the vision area of the lightCone robot
     *
     * @param robot LightConeRobot
     * @param g2d   Graphics2D
     */
    private void drawVisionCone(BaseVisionConeRobot robot, Graphics2D g2d) {
        g2d.setColor(new Color(240, 160, 60, 170));
        double visionRange = robot.getVisionRange(), visionAngle = robot.getVisionAngle();
        Position edge1 = robot.getPose().getPositionInDirection(visionRange, robot.getPose().getRotation() + visionAngle / 2);
        Position edge2 = robot.getPose().getPositionInDirection(visionRange, robot.getPose().getRotation() - visionAngle / 2);
        drawLine(robot.getPose(), edge1, g2d);
        drawLine(robot.getPose(), edge2, g2d);
        for (double i = robot.getPose().getRotation() - visionAngle / 2; i < robot.getPose().getRotation() + visionAngle / 2; i += Math.toRadians(3)) {
            Position pos1 = robot.getPose().getPositionInDirection(visionRange, i);
            Position pos2 = robot.getPose().getPositionInDirection(visionRange, i + Math.toRadians(1));
            drawLine(pos1, pos2, g2d);
        }
    }

    private void drawLine(Position position1, Position position2, Graphics g) {
        g.drawLine(convertZoom(position1.getX() - offsetX),
                convertZoom(arena.getHeight() - position1.getY() - offsetY),
                convertZoom(position2.getX() - offsetX),
                convertZoom(arena.getHeight() - position2.getY() - offsetY));
    }

    private void drawInfo(Graphics g2d, RobotInterface robot, int x, int y) {
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
        if (infosLeft && (drawRobotCoordinates || drawRobotEngines || drawRobotRotation)) {
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
            y += fontSize;
            g2d.drawString(String.format("%,.2f", robot.getPose().getX()) +
                            " | " + String.format("%,.2f", robot.getPose().getY())
                    , x - 15 - fontSize, y);
        }
        if (drawRobotEngines) {
            y += fontSize;
            g2d.drawString("R:" + String.format("%,.2f", robot.getEngineR()) +
                            " L:" + String.format("%,.2f", robot.getEngineL()) +
                            " V:" + String.format("%,.2f", robot.cmPerSecond())
                    , x - 28 - fontSize, y);
        }
        if (drawRobotRotation) {
            g2d.drawString(String.format("%,.2f", robot.getPose().getRotation() / Math.PI * 180) + "° | " +
                            String.format("%,.2f", robot.getPose().getRotation() / Math.PI) + " *Pi"
                    , x - 16 - fontSize, y + fontSize);
        }
    }


    public void incOffsetX(int amount) {
        offsetX += amount;
    }

    public void incOffsetY(int amount) {
        offsetY += amount;
    }


    //zoom
    public void incZoom() {
        zoomFactor *= 1.1;
        repaint();
    }

    public void decZoom() {
        zoomFactor /= 1.1;
        repaint();
    }

    private int convertZoom(int number) {
        return (int) Math.round(number * zoomFactor);
    }

    private int convertZoom(double number) {
        return (int) Math.round(number * zoomFactor);
    }

    //toggle functions for visuals

    public void incFontSize(int addend) {
        fontSize += addend + fontSize < 10 ? 0 : addend + fontSize > 30 ? 0 : addend;
    }

    public void toggleDrawRobotCoordinates() {
        drawRobotCoordinates = !drawRobotCoordinates;
    }

    public void toggleDrawRobotEngines() {
        drawRobotEngines = !drawRobotEngines;
    }

    public void toggleDrawRotationIndicator() {
        drawRotationIndicator = !drawRotationIndicator;
    }

    public void toggleDrawrobotRotationo() {
        drawRobotRotation = !drawRobotRotation;
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

    public void toggleDrawRobotCone() {
        drawRobotCone = !drawRobotCone;
    }

    public void toggleDrawRobotSignal() {
        drawRobotSignal = !drawRobotSignal;
    }

}
