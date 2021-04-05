package view;

import model.Arena;
import model.Robot;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class View extends JFrame {
    private SimulationView simView;

    public View(Arena arena) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width - (int) (screenSize.width * 0.2), screenSize.height - (int) (screenSize.height * 0.2));
        setTitle("Vibrobot Simulation");

        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Ich bin ein JMenu");
        JMenuItem item = new JMenuItem("Ich bin das JMenuItem");
        menu.add(item);
        bar.add(menu);

        simView = new SimulationView();
        simView.setSize(arena.getWidth(), arena.getHeight());
        getContentPane().add(bar);
        getContentPane().add(simView);
        setVisible(true);
    }


    public void setRobot(LinkedList<Robot> localPositions) {
        repaint();
        simView.setRobot(localPositions);
    }

}
