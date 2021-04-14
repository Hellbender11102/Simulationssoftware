import controller.Controller;
import model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

class Main {

    public static void main(String[] args) {
        JSONObject settings = loadJSON("resources/settings.json");
        JSONObject variables = loadJSON("resources/variables.json");
        ConcurrentLinkedQueue<RobotInterface> threadOutputQueue = new ConcurrentLinkedQueue<>();
        Map<RobotInterface, Position> robotsAndPositionOffsets = new HashMap<>();
        Random random;
        Arena arena;
        Controller controller;

        if (variables != null) {
            random = new Random((long) variables.get("seed"));
            JSONObject arenaObj = (JSONObject) variables.get("arena");
            arena = Arena.getInstance((int) (long) arenaObj.get("width"), (int) (long) arenaObj.get("height"));
            JSONArray robots = (JSONArray) variables.get("robots");
            robots.forEach(entry -> loadRobots((JSONObject) entry, robotsAndPositionOffsets, threadOutputQueue, random));
        } else {
            random = new Random();
            arena = Arena.getInstance(500, 500);
        }
        if (settings != null) {
            controller = new Controller(threadOutputQueue, robotsAndPositionOffsets, arena, random);
            controller.visualisationTimer((int) (long) settings.get("fps"));
            controller.startRobotThreads();
        } else {
            controller = new Controller(threadOutputQueue, robotsAndPositionOffsets, arena, random);
            controller.visualisationTimer(30);
            controller.startRobotThreads();
        }
    }

    /**
     * @param filePath
     * @return
     */
    private static JSONObject loadJSON(String filePath) {
        JSONObject object = null;
        try {
            FileReader inputFile = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(inputFile);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line);
            object = (JSONObject) JSONValue.parse(stringBuilder.toString());
            System.out.println(filePath + " " + object.entrySet());
        } catch (IOException e) {
            System.out.println(e);
        }
        return object;
    }

    /**
     * @param robotObject
     * @param robotsAndPositionOffsets
     * @param threadOutputQueue
     * @param random
     */
    private static void loadRobots(JSONObject robotObject,
                                   Map<RobotInterface, Position> robotsAndPositionOffsets,
                                   ConcurrentLinkedQueue<RobotInterface> threadOutputQueue, Random random) {
        JSONObject positonObject = (JSONObject) robotObject.get("position");
        Pose pos = new Pose((Double) positonObject.get("x"), (Double) positonObject.get("y"), (Double) positonObject.get("rotation"));
        RobotInterface robot = new RobotBuilder().engineRight((Double) robotObject.get("engineR"))
                .engineLeft((Double) robotObject.get("engineL"))
                .engineDistnace((Double) robotObject.get("distance"))
                .threadOutputQueue(threadOutputQueue)
                .random(random)
                .pose(pos)
                .powerTransmission(0)
                .diameters(20)
                .buildRobot2();
        robotsAndPositionOffsets.put(robot, new Position(0, 0));
    }
}