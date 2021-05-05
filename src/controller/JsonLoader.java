package controller;

import model.AbstractModel.RobotBuilder;
import model.AbstractModel.RobotInterface;
import model.Arena;
import model.Pose;
import model.Position;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class JsonLoader {
    private JSONObject settings = loadJSON("resources/settings.json");
    private JSONObject variables = loadJSON("resources/variables.json");
    private Arena arena;

    JsonLoader() throws IOException {
    }

    Arena initArena() {
        if (variables != null) {
            JSONObject arenaObj = (JSONObject) variables.get("arena");
            arena = Arena.getInstance((int) (long) arenaObj.get("width"), (int) (long) arenaObj.get("height"));
        } else {
            arena = Arena.getInstance(500, 500);
        }
        return arena;
    }

    Arena reloadArena() {
        if (variables != null) {
            JSONObject arenaObj = (JSONObject) variables.get("arena");
            arena = Arena.overWriteInstance((int) (long) arenaObj.get("width"), (int) (long) arenaObj.get("height"));
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

    int loadSimulatedTime() {
        if (settings != null) {
            JSONObject mode = (JSONObject) settings.get("mode");
            return (int) (long) mode.get("simulate-turns");
        } else return 30;
    }

    boolean displayView() {
        if (settings != null) {
            JSONObject mode = (JSONObject) settings.get("mode");
            return (boolean) mode.get("simulate-turns");
        } else return true;
    }

    Map<RobotInterface, Position> loadRobots(Random random, Logger logger) {
        JSONArray robots = (JSONArray) variables.get("robots");
        Map<RobotInterface, Position> robotsAndPositionOffsets = new HashMap<>();
        robots.forEach(entry -> loadRobots((JSONObject) entry, robotsAndPositionOffsets, random, arena, logger));
        return robotsAndPositionOffsets;
    }

    /**
     * @param filePath
     * @return
     */
    private static JSONObject loadJSON(String filePath) throws IOException {
        JSONObject object = null;
        try {
            FileReader inputFile = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(inputFile);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line);
            object = (JSONObject) JSONValue.parse(stringBuilder.toString());
        } catch (IOException e) {
            throw e;
        }
        return object;
    }

    /**
     * @param robotObject
     * @param robotsAndPositionOffsets
     * @param random
     */
    private static void loadRobots(
            JSONObject robotObject, Map<RobotInterface, Position> robotsAndPositionOffsets, Random random, Arena arena,
            Logger logger) {
        JSONObject positionObject = (JSONObject) robotObject.get("position");
        Pose pos = new Pose((Double) positionObject.get("x"), (Double) positionObject.get("y"),
                Math.toRadians((Double) positionObject.get("rotation")));

        RobotBuilder builder = new RobotBuilder()
                .engineRight((Double) robotObject.get("engineR"))
                .engineLeft((Double) robotObject.get("engineL"))
                .engineDistnace((Double) robotObject.get("distance"))
                .random(random)
                .pose(pos)
                .arena(arena)
                .powerTransmission((Double) robotObject.get("powerTransmission"))
                .diameters((Double) robotObject.get("diameters"))
                .logger(logger);
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
