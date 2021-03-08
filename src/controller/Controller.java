package controller;

import model.Position;
import model.Robot;
import view.View;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Controller {
    private View view;
    private List<Robot> robotList = new LinkedList<>();
    private Map<Robot, Position> globalPosition = new HashMap<>();

    public Controller(View view) {
        this.view = view;
        robotList.add(new Robot(0, 1, 2));
    }

    public void simulationLoop() {
        for (int i = 0; i < 720; i++) {
            for (Robot robot : robotList) {
                robot.drive();
                System.out.println(robot.toString());
            }
        }
    }


}
