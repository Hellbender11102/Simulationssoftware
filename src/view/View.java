package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class View extends JFrame {
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    BufferedImage img;

    public View() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(screenSize.width - (int) (screenSize.width * 0.02), screenSize.height - (int) (screenSize.height * 0.04));
        this.setVisible(true);
    }
}
