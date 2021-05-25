package controller;

import model.RobotBuilder;
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
    private JSONObject settings;
    private JSONObject variables;

    private Arena arena;

    JsonLoader() {
        {
            try {
                settings = loadJSON("resources/settings.json");
                variables = loadJSON("resources/variables.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Arena initArena() {
        if (variables != null) {
            JSONObject arenaObj = (JSONObject) variables.get("arena");
            arena = Arena.getInstance((int) (long) arenaObj.get("width"), (int) (long) arenaObj.get("height"));
        } else {
            System.err.println("Could not read File.");
            arena = Arena.getInstance(500, 500);
        }
        return arena;
    }

    Arena reloadArena() {
        if (variables != null) {
            JSONObject arenaObj = (JSONObject) variables.get("arena");
            arena = Arena.overWriteInstance((int) (long) arenaObj.get("width"), (int) (long) arenaObj.get("height"));
        } else {
            System.err.println("Could not read File.");
        }
        return arena;
    }

    Random loadRandom() {
        if (variables != null)
            return new Random((long) variables.get("seed"));
        else {
            System.err.println("Could not read File.");
            return new Random();
        }
    }

    int loadFps() {
        if (settings != null)
            return (int) (long) settings.get("fps");
        else {
            System.err.println("Could not read File.");
            return 0;
        }
    }
    int loadSimulatedTime() {
        if (settings != null) {
            JSONObject mode = (JSONObject) settings.get("mode");
            return (int) (long) mode.get("simulate-turns");
        } else {
            System.err.println("Could not read File.");
            return 0;
        }
    }

    boolean displayView() {
        if (settings != null) {
            JSONObject mode = (JSONObject) settings.get("mode");
            return (boolean) mode.get("display-view");
        } else {
            System.err.println("Could not read File.");
            return true;
        }
    }

    Map<RobotInterface, Position> loadRobots(Random random, Logger logger) {
        JSONArray robots = (JSONArray) variables.get("robots");
        Map<RobotInterface, Position> robotsAndPositionOffsets = new HashMap<>();
        robots.forEach(entry -> loadRobots(
                (JSONObject) entry,
                robotsAndPositionOffsets,
                random,
                arena,
                logger
                ,loadSimulatedTime()));
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
    private void loadRobots(
            JSONObject robotObject, Map<RobotInterface, Position> robotsAndPositionOffsets, Random random, Arena arena,
            Logger logger, int timeToSimulate) {
        JSONObject positionObject = (JSONObject) robotObject.get("position");
        Pose pos = new Pose((Double) positionObject.get("x"), (Double) positionObject.get("y"),
                Math.toRadians((Double) positionObject.get("rotation")));

        RobotBuilder builder = new RobotBuilder()
                .engineRight((Double) robotObject.get("engineR"))
                .engineLeft((Double) robotObject.get("engineL"))
                .engineDistnace((Double) robotObject.get("distance"))
                .random(new Random(random.nextInt()))
                .pose(pos)
                .timeToSimulate(timeToSimulate)
                .arena(arena)
                .powerTransmission((Double) robotObject.get("powerTransmission"))
                .diameters((Double) robotObject.get("diameters"))
                .logger(logger)
                .simulateWithView(displayView());
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
