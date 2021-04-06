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
        setSize(screenSize.width , screenSize.height - (int) (screenSize.height * 0.04));
        setTitle("Vibrobot Simulation");

        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Ich bin ein JMenu");
        JMenuItem item = new JMenuItem("Ich bin das JMenuItem");
        menu.add(item);
        bar.add(menu);

        simView = new SimulationView(arena);
        getContentPane().add(bar,BorderLayout.NORTH);
        getContentPane().add(simView,BorderLayout.CENTER);
        setVisible(true);
    }

    public SimulationView getSimView(){
        return simView;
    }

}
