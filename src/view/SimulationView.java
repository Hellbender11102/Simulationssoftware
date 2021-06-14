package view;

import model.AbstractModel.PhysicalEntity;
import model.Area;
import model.Arena;
import model.Position;
import model.AbstractModel.RobotInterface;

import javax.swing.*;
import java.awt.*;
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
        for (PhysicalEntity pe1 : arena.getPhysicalEntityList()) {
            for (PhysicalEntity pe2 : arena.getPhysicalEntityList()) {
                if (!pe1.equals(pe2)) {
                    Position p2 = pe2.getPose();
                    Position closest = pe1.getClosestPositionInEntity(p2);
                    g2d.setColor(pe2.getColor());
                    g2d.fillRect(convertZoom(closest.getXCoordinate()-offsetX),
                            convertZoom(arena.getHeight()-closest.getYCoordinate()-offsetY),3,3);
                }
            }
        }
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
            for (Area area : arena.getAreaList()) {
                g2d.setColor(new Color(area.getColor().getRed(), area.getColor().getGreen(), area.getColor().getBlue(), 200));
                g2d.fillOval(convertZoom((area.getPose().getXCoordinate() - offsetX - (area.getNoticeableDistance()) / 2)),
                        convertZoom(arena.getHeight() - (area.getPose().getYCoordinate() + offsetY + (area.getNoticeableDistance()) / 2)),
                        convertZoom((area.getNoticeableDistance())),
                        convertZoom((area.getNoticeableDistance())));
                g2d.setColor(area.getClassColor());
                g2d.drawOval(convertZoom((area.getPose().getXCoordinate() - offsetX - area.getWidth() / 2)),
                        convertZoom(arena.getHeight() - (area.getPose().getYCoordinate() + offsetY + (area.getHeight() / 2))),
                        convertZoom(area.getWidth()),
                        convertZoom(area.getHeight()));
            }
        }
        if (arena.getPhysicalEntitiesWithoutRobots() != null) {
            for (PhysicalEntity entity : arena.getPhysicalEntitiesWithoutRobots()) {
                g2d.setColor(entity.getClassColor());
                g2d.fillRect(convertZoom((entity.getPose().getXCoordinate() - entity.getWidth() / 2) - offsetX),
                        convertZoom(arena.getHeight() - (entity.getPose().getYCoordinate() + entity.getHeight() / 2) - offsetY),
                        convertZoom(entity.getWidth()), convertZoom(entity.getHeight()));
            }
        }
        if (drawCenter)
            for (RobotInterface robot : classList) {
                Position position = robot.centerOfGroupWithClasses(List.of(robot.getClass()));
                g2d.setColor(robot.getClassColor());
                g2d.drawOval(convertZoom(position.getXCoordinate() - offsetX),
                        convertZoom(arena.getHeight() - position.getYCoordinate() - offsetY)
                        , convertZoom(2), convertZoom(2));
            }

        if (arena.getRobots() != null) {
            int x = convertZoom(arena.getWidth() - offsetX + fontSize) + 35, y = -convertZoom(offsetY) - fontSize * 5, n = 0;
            for (RobotInterface robot : arena.getRobots()) {
                drawRobot(robot, g);
                if (!infosLeft) {
                    x = convertZoom(Math.round(robot.getPose().getXCoordinate() - offsetX) - robot.getRadius());
                    y = convertZoom(arena.getHeight() - Math.round(robot.getPose().getYCoordinate() + offsetY) + robot.getRadius());
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
        double x = robot.getPose().getXCoordinate() - offsetX - robot.getRadius();
        double y = arena.getHeight() - robot.getPose().getYCoordinate() - offsetY - robot.getRadius();
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
            g2d.drawLine(convertZoom(x + robot.getRadius()),
                    convertZoom(y + robot.getRadius()),
                    convertZoom(direction.getXCoordinate() - offsetX),
                    convertZoom(arena.getHeight() - direction.getYCoordinate() - offsetY));
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
            y += fontSize;
            g2d.drawString(String.format("%,.2f", robot.getPose().getXCoordinate()) +
                            " | " + String.format("%,.2f", robot.getPose().getYCoordinate())
                    , x - 15 - fontSize, y);
        }
        if (drawRobotEngines) {
            y += fontSize;
            g2d.drawString("R:" + String.format("%,.2f", robot.getEngineR()) +
                            " L:" + String.format("%,.2f", robot.getEngineL()) +
                            " V:" + String.format("%,.2f", robot.trajectorySpeed())
                    , x - 28 - fontSize, y);
        }
        if (drawRobotRotationo) {
            g2d.drawString(String.format("%,.2f", robot.getPose().getRotation() / Math.PI * 180) + "° | " +
                            String.format("%,.2f", robot.getPose().getRotation() / Math.PI) + " *Pi"
                    , x - 16 - fontSize, y + fontSize);
        }
    }

    public void incOffsetX(int amount) {
        offsetX = calcBorders(amount, arena.getWidth(), offsetX);
    }

    public void incOffsetY(int amount) {
        offsetY = calcBorders(amount, arena.getHeight(), offsetY);
    }

    private int convertZoom(int number) {
        return (int) Math.round(number * zoomFactor);
    }

    private int convertZoom(double number) {
        return (int) Math.round(number * zoomFactor);
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
