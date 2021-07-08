package view;

import controller.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.URL;

public class TextView extends JFrame {
    JTextPane jTextPane;
    Logger errorLogger = new Logger();
    private final JMenuItem itemSave = new JMenuItem("Datei speichern",1);
    private final JMenuItem itemReload = new JMenuItem("Datei neu laden",2);

    TextView(String title, String filePath, int posX, boolean editable) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            errorLogger.dumpError("LookAndFeel konnte nicht gesetzt werden.");
            errorLogger.dumpError(ex.getMessage());
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screenSize.width * 0.25), (int) (screenSize.height * 0.90));
        setLocation(posX, (int) (screenSize.height * 0.05));
        setTitle(title);
        JScrollPane scrollPane = new JScrollPane();
        jTextPane = new JTextPane();
        jTextPane.setText(readFile(filePath));
        jTextPane.setFont(new Font("TimesRoman", Font.PLAIN, 13));
        jTextPane.setEditable(editable);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setViewportView(jTextPane);
        jTextPane.setCaretPosition(0);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        URL iconURL =  getClass().getClassLoader().getResource("icon.PNG");
        if (null != iconURL) {
            ImageIcon icon = new ImageIcon(iconURL);
            setIconImage(icon.getImage());
        }
        if (editable) {
            JMenuBar bar = new JMenuBar();
            JMenu menu = new JMenu("Optionen");
            menu.add(itemSave);
            itemSave.addActionListener(listener -> saveFile(filePath));
            menu.add(itemReload);
            itemReload.addActionListener(listener -> jTextPane.setText(readFile(filePath)));
            bar.add(menu);
            getContentPane().add(bar, BorderLayout.NORTH);
        }
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private String readFile(String filePath) {
        try {
            File file = new File(filePath);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line).append('\n');
            }
            bufferedReader.close();
            return text.toString();
        } catch (IOException e) {
            errorLogger.dumpError("Datei wurde nicht gefunden \nEs wurde probiert "+filePath+" zu laden.");
            errorLogger.dumpError(e.getMessage());
            return "Datei wurde nicht gefunden";
        }
    }

    /**
     * Saves file
     * @param filePath String
     */
    private void saveFile(String filePath) {
        try {
            File file = new File(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(jTextPane.getText());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ioException) {
            errorLogger.dumpError("Datei konnte nicht gespeichert werden von "+this.getClass());
            errorLogger.dumpError(ioException.getMessage());
        }
    }
}
