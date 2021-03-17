package view;

import model.Position;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.awt.Graphics;

public class View extends JFrame {
    private LinkedList<Position> globalPosition = new LinkedList();

    public View() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width - (int) (screenSize.width * 0.0), screenSize.height - (int) (screenSize.height * 0.04));
        this.setVisible(true);
    }


    public void setRobots(LinkedList<Position> globalPosition) {
        this.globalPosition = globalPosition;
    }

    public void paint(Graphics g) {
        for (Position pos : globalPosition) {
            g.setColor(Color.red);
            g.fillRect((int) pos.getxCoordinate(), (int) pos.getyCoordinate(), 15, 15);
        }
    }
}
