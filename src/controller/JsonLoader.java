package controller;

import model.*;
import model.AbstractModel.RobotInterface;
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
                initArena();
            } catch (IOException e) {
                System.err.println("Could not load settings.json and variables.json.");
                e.printStackTrace();
            }
        }
    }

    Arena initArena() {
        if (variables != null) {
            JSONObject arenaObj = (JSONObject) variables.get("arena");
            arena = Arena.getInstance((int) (long) arenaObj.get("width"), (int) (long) arenaObj.get("height"));
        } else {
            System.err.println("Could not read arena from variables.json.");
            arena = Arena.getInstance(500, 500);
        }
        return arena;
    }

    Arena reloadArena() {
        if (variables != null) {
            JSONObject arenaObj = (JSONObject) variables.get("arena");
            arena = Arena.overWriteInstance((int) (long) arenaObj.get("width"), (int) (long) arenaObj.get("height"));
        } else {
            System.err.println("Could not read arena from variables.json.");
        }
        return arena;
    }

    Random loadRandom() {
        if (variables != null)
            return new Random((long) variables.get("seed"));
        else {
            System.err.println("Could not read seed from variables.json.");
            return new Random();
        }
    }

    double loadMaxSpeed() {
        if (variables != null)
            return (double) variables.get("maxSpeed");
        else {
            System.err.println("Could not read maxSpeed from variables.json.");
            return 8.;
        }
    }

    double loadMinSpeed() {
        if (variables != null)
            return (double) variables.get("minSpeed");
        else {
            System.err.println("Could not read minSpeed from variables.json.");
            return 0.;
        }
    }

    int loadFps() {
        if (settings != null)
            return (int) (long) settings.get("fps");
        else {
            System.err.println("Could not read fps from settings.json.");
            return 0;
        }
    }

    int loadSimulatedTime() {
        if (settings != null) {
            JSONObject mode = (JSONObject) settings.get("mode");
            return (int) (long) mode.get("simulate-seconds");
        } else {
            System.err.println("Could not read simulated-seconds from settings.json.");
            return 0;
        }
    }

    boolean loadDisplayView() {
        if (settings != null) {
            JSONObject mode = (JSONObject) settings.get("mode");
            return (boolean) mode.get("display-view");
        } else {
            System.err.println("Could not read display-view from settings.json.");
            return true;
        }
    }

    /**
     *
     * @param random Random
     * @param logger Logger
     * @return Map<RobotInterface, Position>
     */
    Map<RobotInterface, Position> loadRobots(Random random, Logger logger) {
        JSONArray robots = (JSONArray) variables.get("robots");
        if(robots.size() == 0)    System.err.println("Zero robots in variables.json.");
        Map<RobotInterface, Position> robotsAndPositionOffsets = new HashMap<>();
        robots.forEach(entry -> loadRobots(
                (JSONObject) entry,
                robotsAndPositionOffsets,
                random,
                arena,
                logger
                , loadSimulatedTime()));
        return robotsAndPositionOffsets;
    }
    /**
     *
     * @param random Random
     * @param logger Logger
     * @return
     */
    List<Box> loadBoxes(Random random, Logger logger) {
        JSONArray boxes = (JSONArray) variables.get("boxes");
        LinkedList<Box> boxList = new LinkedList();
        for (Object box: boxes) {
            JSONObject jsonBox= (JSONObject) box;
            boxList.add(new Box(arena,new Random(random.nextInt()),(double) jsonBox.get("width"),(double) jsonBox.get("height"),loadPose(jsonBox)));

        }
        return boxList;
    }
    /**
     *
     * @param random Random
     * @param logger Logger
     * @return
     */
    List<Wall> loadWalls(Random random, Logger logger) {
        JSONArray walls = (JSONArray) variables.get("walls");
        LinkedList<Wall> wallList = new LinkedList();
        for (Object wall: walls) {
            JSONObject jsonWall= (JSONObject) wall;
            wallList.add(new Wall(arena,(double) jsonWall.get("width"),(double) jsonWall.get("height"),loadPose(jsonWall)));

        }
        return wallList;
    }
    /**
     *
     * @param random Random
     * @param logger Logger
     * @return
     */
    List<Area> loadAreas(Random random, Logger logger) {
        JSONArray areas = (JSONArray) variables.get("areas");
        LinkedList<Area> wallList = new LinkedList();
        for (Object area: areas) {
            JSONObject jsonArea= (JSONObject) area;
            wallList.add(new Area(arena,new Random(random.nextInt()),(double) jsonArea.get("diameters"),loadPose(jsonArea)));

        }
        return wallList;
    }

    /**
     * @param filePath String
     * @return JSONObject
     */
    private static JSONObject loadJSON(String filePath) throws IOException {
        JSONObject object;
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
     * @param robotObject JSONObject
     * @param robotsAndPositionOffsets Map<RobotInterface, Position>
     * @param random Random
     * @param arena Arena
     * @param logger Logger
     * @param timeToSimulate int
     */
    private void loadRobots(
            JSONObject robotObject, Map<RobotInterface, Position> robotsAndPositionOffsets, Random random, Arena arena,
            Logger logger, int timeToSimulate) {
        Pose pos = loadPose(robotObject);

        RobotBuilder builder = new RobotBuilder()
                .engineRight((Double) robotObject.get("engineR"))
                .engineLeft((Double) robotObject.get("engineL"))
                .engineDistnace((Double) robotObject.get("distance"))
                .random(new Random(random.nextInt()))
                .pose(pos)
                .minSpeed(loadMinSpeed())
                .maxSpeed(loadMaxSpeed())
                .timeToSimulate(timeToSimulate)
                .arena(arena)
                .powerTransmission((Double) robotObject.get("powerTransmission"))
                .diameters((Double) robotObject.get("diameters"))
                .logger(logger)
                .simulateWithView(loadDisplayView());
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
            case "4":
                robot = builder.buildRobot4();
                break;
            default:
                robot = builder.buildDefault();
        }
        robotsAndPositionOffsets.put(robot, new Position(0, 0));
    }

    private Pose loadPose(JSONObject object){
        JSONObject positionObject = (JSONObject) object.get("position");
        return new Pose((Double) positionObject.get("x"), (Double) positionObject.get("y"),
                Math.toRadians((Double) positionObject.get("rotation")));
    }
}
