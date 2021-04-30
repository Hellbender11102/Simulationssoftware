package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

public class TextView extends JFrame {
    JTextPane jTextPane;
    private JMenuItem itemSave = new JMenuItem("Speichern");
    private JMenuItem itemReload = new JMenuItem("Neu laden");

    TextView(String title, String filePath, int posX, boolean editable) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
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

        if (editable) {
            setTitle("Vibrobot Simulation");
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
            return e.toString();
        }
    }

    private void saveFile(String filePath) {
        try {
            File file = new File(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(jTextPane.getText());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ignored) {
        }
    }
}
