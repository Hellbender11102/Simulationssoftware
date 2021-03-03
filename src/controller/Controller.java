package controller;

import model.Robot;
import view.View;

import java.util.LinkedList;
import java.util.List;

public class Controller {
    private View view;
    private List<Robot> robotList = new LinkedList<>();
    private Double[][] map;

    public Controller(View view) {
        this.view = view;
        robotList.add(new Robot(1, 2));
        map = new Double[100][100];
    }

    public void simulationLoop() {
        for (int i = 0; i < 100; i++) {
            for (Robot robot : robotList) {
                double speed = robot.translationSpeed();
                double angularVelocity = robot.angularVelocity();
                double rotation = robot.getRotation();
            }
        }
    }

}
