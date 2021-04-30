package view;

import model.Arena;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class View extends JFrame {
    private SimulationView simView;
    private JMenuItem itemHelp = new JMenuItem("Hilfe",1);
    private JMenuItem itemLog = new JMenuItem("Log erstellen",2);
    private JMenuItem itemLogEditor = new JMenuItem("Log einsehen",3);
    private JMenuItem itemVariablesEditor = new JMenuItem("Variablen",4);
    private JMenuItem itemSettingsEditor = new JMenuItem("Simulationsvariablen",5);
    private JMenuItem itemRestart = new JMenuItem("Versuch neu starten",6);
    private JMenuItem itemFullRestart= new JMenuItem("Neu laden und neu Starten",7);
    private TextView settings;
    private TextView log;
    private TextView variabls;
    private TextView help;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public View(Arena arena) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize((int) (screenSize.width * 0.5), (int) (screenSize.height * 0.90));
        setLocation((int) (screenSize.width * 0.25), (int) (screenSize.height * 0.05));
        setTitle("Vibrobot Simulation");
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Optionen");

        menu.add(itemHelp);
        menu.add(itemRestart);
        menu.add(itemFullRestart);
        menu.add(itemLog);
        menu.add(itemLogEditor);
        menu.add(itemVariablesEditor);
        menu.add(itemSettingsEditor);
        bar.add(menu);

        simView = new SimulationView(arena);
        getContentPane().add(bar, BorderLayout.NORTH);
        getContentPane().add(simView, BorderLayout.CENTER);
        setJmenuItemlogic();
        setVisible(true);
    }

    public SimulationView getSimView() {
        return simView;
    }

    private void setJmenuItemlogic() {
        ActionListener helpAction = e -> {
            if(help==null)
           help = new TextView("Hilfe","README.md", 0, false);
            else help.setVisible(true);
        };
        itemHelp.addActionListener(helpAction);

        ActionListener settingsAction = e -> {
                 if(variabls==null)
          variabls=  new TextView("variables.json","resources/variables.json",
                    (int) (screenSize.width * 0.75), true);
            else variabls.setVisible(true);
        };
        itemSettingsEditor.addActionListener(settingsAction);

        ActionListener variablesAction = e -> {
                 if(settings==null)
           settings= new TextView("settings.json","resources/settings.json",
                    (int) (screenSize.width * 0.75), true);
            else settings.setVisible(true);
        };
        itemVariablesEditor.addActionListener(variablesAction);

        ActionListener logAction = e -> {
                 if(log==null)
          log =  new TextView("log.csv","out/log.csv",
                    (int) (screenSize.width * 0.75), true);
            else log.setVisible(true);
        };
        itemLogEditor.addActionListener(logAction);
    }

    public JMenuItem getRestart() {
        return itemRestart;
    }

    public JMenuItem getLog() {
        return itemLog;
    }
    public JMenuItem getFullRestart() {
        return itemFullRestart;
    }

}
