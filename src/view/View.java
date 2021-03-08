package view;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public View() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(screenSize.width - (int) (screenSize.width * 0.02), screenSize.height - (int) (screenSize.height * 0.04));
        this.setVisible(false);
    }


}
