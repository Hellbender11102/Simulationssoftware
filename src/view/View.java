package view;

import model.Arena;

import javax.naming.spi.DirectoryManager;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FileChooserUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Paths;

public class View extends JFrame {
    private final SimulationView simView;
    private final JMenuItem itemHelp = new JMenuItem("Hilfe", 1);
    private final JMenuItem itemStartStop = new JMenuItem("Starten / pausieren", 2);
    private final JMenuItem itemLog = new JMenuItem("Log erstellen", 3);
    private final JMenuItem itemLogEditor = new JMenuItem("Log einsehen", 4);
    private final JMenuItem itemVariablesEditor = new JMenuItem("Einstellung", 5);
    private final JMenuItem itemSettingsEditor = new JMenuItem("Simulationsvariablen", 6);
    private final JMenuItem itemLoadVariables = new JMenuItem("Simulationsvariablen laden von", 7);
    private final JMenuItem itemRestart = new JMenuItem("Versuch neu starten", 8);
    private final JMenuItem itemFullRestart = new JMenuItem("Neu laden und neu Starten", 9);
    private TextView settings;
    private TextView log;
    private TextView help;
    private String variablesPath = "resources/variables.json";
    private String settingsPath = "resources/settings.json";
    private String logPath = "out/log.csv";
    private String readmePath = "README.md";
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    JFileChooser fileChooserUI;
    FileFilter filter = new FileNameExtensionFilter("txt, JSON", "txt", "JSON", "Json");

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
        URL iconURL =  getClass().getClassLoader().getResource("icon.PNG");
        if (null != iconURL) {
            ImageIcon icon = new ImageIcon(iconURL);
            setIconImage(icon.getImage());
        }
        menu.add(itemHelp);
        menu.add(itemStartStop);
        menu.add(itemRestart);
        menu.add(itemFullRestart);
        menu.add(itemLog);
        menu.add(itemLogEditor);
        menu.add(itemVariablesEditor);
        menu.add(itemSettingsEditor);
        menu.add(itemLoadVariables);
        bar.add(menu);
        simView = new SimulationView(arena);
        getContentPane().add(bar, BorderLayout.NORTH);
        getContentPane().add(simView, BorderLayout.CENTER);
        setMenuLogic();

        fileChooserUI = new JFileChooser();
        setVisible(true);
    }

    public SimulationView getSimView() {
        return simView;
    }

    private void setMenuLogic() {
        ActionListener helpAction = e -> {
            if (help == null)
                help = new TextView("Hilfe", readmePath, 0, false);
            else help.setVisible(true);
        };
        itemHelp.addActionListener(helpAction);

        ActionListener settingsAction = e -> {
            new TextView(variablesPath, variablesPath,
                        (int) (screenSize.width * 0.75), true);
        };
        itemSettingsEditor.addActionListener(settingsAction);

        ActionListener variablesAction = e -> {
            if (settings == null)
                settings = new TextView("settings.json", settingsPath,
                        (int) (screenSize.width * 0.75), true);
            else settings.setVisible(true);
        };
        itemVariablesEditor.addActionListener(variablesAction);

        ActionListener logAction = e -> {
            if (log == null)
                log = new TextView("log.csv", logPath,
                        (int) (screenSize.width * 0.75), true);
            else log.setVisible(true);
        };
        itemLogEditor.addActionListener(logAction);
    }

    //Jmenu getter
    public JMenuItem getRestart() {
        return itemRestart;
    }

    public JMenuItem getLog() {
        return itemLog;
    }

    public JMenuItem getFullRestart() {
        return itemFullRestart;
    }

    public JMenuItem getItemLoadVariables() {
        return itemLoadVariables;
    }

    public JMenuItem getItemStartStop() {
        return itemStartStop;
    }

    public String getPathOfSelectedFile() {
        fileChooserUI.setFileFilter(filter);
        int returnVal = fileChooserUI.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION && fileChooserUI.getSelectedFile() != null && fileChooserUI.getSelectedFile().exists())
             variablesPath = fileChooserUI.getSelectedFile().getAbsolutePath();
        return variablesPath;
    }

}
