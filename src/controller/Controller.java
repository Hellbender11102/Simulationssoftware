package controller;

import model.Map;
import model.Robot;
import view.View;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Controller {
    private View view;
    private List<Robot> robotList;

    public Controller(View view) {
        Map map = new Map();
        this.view = view;
    }

    private void simulationLoop() {
        this.view.repaint();
    }

}
