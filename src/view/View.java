package view;

import model.Robot;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class View extends JFrame {
    private SimulationView simView;

    public View() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width - (int) (screenSize.width * 0.2), screenSize.height - (int) (screenSize.height * 0.2));
        setTitle("Vibrobot Simulation");

        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Ich bin ein JMenu");
        JMenuItem item = new JMenuItem("Ich bin das JMenuItem");
        menu.add(item);
        bar.add(menu);

        simView = new SimulationView();
        getContentPane().add(bar, BorderLayout.NORTH);
        getContentPane().add(simView, BorderLayout.CENTER);
        setVisible(true);
    }


    public void setRobot(LinkedList<Robot> localPositions) {
        simView.setRobot(localPositions);
    }

    public void repaint() {
        super.repaint();
        simView.repaint();
    }
}
