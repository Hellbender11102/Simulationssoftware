package controller;

import model.AbstractModel.EntityBuilder;
import model.AbstractModel.RobotInterface;
import model.Arena;
import model.Pose;
import model.Position;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class JsonLoader {
    private JSONObject settings = loadJSON("resources/settings.json");
    private JSONObject variables = loadJSON("resources/variables.json");
    private Arena arena;

    Arena initArena() {
        if (variables != null) {
            JSONObject arenaObj = (JSONObject) variables.get("arena");
            arena = Arena.getInstance((int) (long) arenaObj.get("width"), (int) (long) arenaObj.get("height"));
        } else {
            arena = Arena.getInstance(500, 500);
        }
        return arena;
    }

    Random loadRandom() {
        if (variables != null)
            return new Random((long) variables.get("seed"));
        else return new Random();
    }

    int loadFps() {
        if (settings != null)
            return (int) (long) settings.get("fps");
        else return 30;
    }

    Map<RobotInterface, Position> loadRobots(Random random) {
        JSONArray robots = (JSONArray) variables.get("robots");
        Map<RobotInterface, Position> robotsAndPositionOffsets = new HashMap<>();
        robots.forEach(entry -> loadRobots((JSONObject) entry, robotsAndPositionOffsets, random, arena));
        return robotsAndPositionOffsets;
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
     * @param random
     */
    private static void loadRobots(
            JSONObject robotObject, Map<RobotInterface, Position> robotsAndPositionOffsets, Random random, Arena arena
    ) {
        JSONObject positionObject = (JSONObject) robotObject.get("position");
        Pose pos = new Pose((Double) positionObject.get("x"), (Double) positionObject.get("y"),
                Math.toRadians((Double) positionObject.get("rotation")));

        EntityBuilder builder = new EntityBuilder()
                .engineRight((Double) robotObject.get("engineR"))
                .engineLeft((Double) robotObject.get("engineL"))
                .engineDistnace((Double) robotObject.get("distance"))
                .random(random)
                .pose(pos)
                .arena(arena)
                .powerTransmission((Double) robotObject.get("powerTransmission"))
                .diameters((Double) robotObject.get("diameters"));
        RobotInterface robot;
        switch ((String) robotObject.get("type")) {
            case "1":
                robot = builder.buildRobot1();
                break;
            case "2":
                robot = builder.buildRobot2();
                break;
            case "3":
                robot = builder.buildRobot3();
                break;
            default:
                robot = builder.buildDefault();
        }
        robotsAndPositionOffsets.put(robot, new Position(0, 0));
    }
}
