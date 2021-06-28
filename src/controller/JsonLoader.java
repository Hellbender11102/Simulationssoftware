package controller;

import model.*;
import model.AbstractModel.Entity;
import model.AbstractModel.RobotInterface;
import org.json.simple.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class JsonLoader {
    private JSONObject settings;
    private JSONObject variables;

    private Arena arena;
    private boolean displayView = false;
    private String pathSettings = "resources/settings.json";
    private String pathVariables = "resources/variables.json";

    /**
     * Constructor
     * Loads default path for settings and variables
     */
    JsonLoader() {
        {
            try {
                settings = loadJSON(pathSettings);
                variables = loadJSON(pathVariables);
                initArena();
            } catch (IOException e) {
                System.err.println("Could not load settings.json and variables.json.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads the arena
     *
     * @return Arena
     */
    Arena initArena() {
        if (variables != null) {
            JSONObject arenaObj = (JSONObject) variables.get("arena");
            arena = Arena.getInstance((int) (long) arenaObj.get("width"), (int) (long) arenaObj.get("height"), (boolean) arenaObj.get("torus"));
        } else {
            System.err.println("Could not read arena from variables.json.");
            arena = Arena.getInstance(0, 0, false);
        }
        return arena;
    }

    /**
     * Reloads the arena and overwrites its singleton instance
     *
     * @return Arena
     */
    Arena reloadArena() {
        if (variables != null && variables.containsKey("arena")) {
            JSONObject arenaObj = (JSONObject) variables.get("arena");
            arena = Arena.overWriteInstance((int) (long) arenaObj.get("width"), (int) (long) arenaObj.get("height"), (boolean) arenaObj.get("torus"));
        } else {
            System.err.println("Could not read arena from variables.json.");
        }
        return arena;
    }

    Random loadRandom() {
        if (variables != null && variables.containsKey("seed"))
            return new Random((long) variables.get("seed"));
        else {
            System.out.println("No seed is used.");
            return new Random();
        }
    }

    double loadMaxSpeed() {
        if (variables != null && variables.containsKey("maxSpeed"))
            return (double) variables.get("maxSpeed");
        else {
            System.err.println("Could not read maxSpeed from variables.json.");
            return 8.;
        }
    }

    double loadMinSpeed() {
        if (variables != null && variables.containsKey("minSpeed"))
            return (double) variables.get("minSpeed");
        else {
            System.err.println("Could not read minSpeed from variables.json.");
            return 0.;
        }
    }

    int loadFps() {
        if (settings != null && settings.containsKey("fps"))
            return (int) (long) settings.get("fps");
        else {
            System.err.println("Could not read fps from settings.json.");
            return 10;
        }
    }

    int loadSimulatedTime() {
        if (settings != null && settings.containsKey("simulate-seconds")) {
            return (int) (long) settings.get("simulate-seconds");
        } else {
            System.err.println("Could not read simulated-seconds from settings.json.");
            return 0;
        }
    }

    boolean loadDisplayView() {
        if (settings != null && settings.containsKey("display-view")) {
            displayView = (boolean) settings.get("display-view");
            return displayView;
        } else {
            System.err.println("Could not read display-view from settings.json.");
            return true;
        }
    }

    int loadTicsPerSimulatedSecond() {
        if (variables != null && variables.containsKey("ticsPerSimulatedSecond"))
            return (int) (long) variables.get("ticsPerSimulatedSecond");
        else {
            System.err.println("Could not read variables or entry ticsPerSimulatedSecond from variables.json.");
        }
        return 1;
    }

    /**
     * Loads all boxes from the variables.json
     * Returns a list of all boxes
     *
     * @param random Random
     * @return List<Entity>
     */
    List<Entity> loadBoxes(Random random) {
        LinkedList<Entity> boxList = new LinkedList();
        if (variables == null || !variables.containsKey("boxes")) {
            System.err.println("No entry boxes found in the variables.json");
            return boxList;
        }
        int missing = 0;
        JSONArray boxes = (JSONArray) variables.get("boxes");
        for (Object box : boxes) {
            JSONObject jsonBox = (JSONObject) box;
            if (jsonBox.containsKey("width") && jsonBox.containsKey("position") && jsonBox.containsKey("height") && loadPose(jsonBox) != null)
                boxList.add(
                        new Box(arena,
                                new Random(random.nextInt()),
                                (double) jsonBox.get("width"),
                                (double) jsonBox.get("height"),
                                loadPose(jsonBox)));
            else
                System.err.println("Could not load box entry " + (boxList.size() + missing++) + " correctly. Entry width, height or position missing.");
        }
        return boxList;
    }


    /**
     * Loads all walls from the variables.json
     * Returns a list of all walls
     *
     * @return List<Entity>
     */
    List<Entity> loadWalls(Random random) {
        LinkedList<Entity> wallList = new LinkedList();
        if (variables == null || !variables.containsKey("walls")) {
            System.err.println("No entry walls found in the variables.json");
            return wallList;
        }
        int missing = 0;
        JSONArray walls = (JSONArray) variables.get("walls");
        for (Object wall : walls) {
            JSONObject jsonWall = (JSONObject) wall;
            if (jsonWall.containsKey("width") && jsonWall.containsKey("position") && jsonWall.containsKey("height") && loadPose(jsonWall) != null)
                wallList.add(new Wall(arena, new Random(random.nextInt()), (double) jsonWall.get("width"), (double) jsonWall.get("height"), loadPose(jsonWall)));
            else
                System.err.println("Could not load wall " + (wallList.size() + missing++) + " correctly. Entry width, height or position missing.");
        }
        return wallList;
    }

    /**
     * Loads all areas from the variables.json
     * Returns a list of all areas
     *
     * @param random Random
     * @return List<Entity>
     */
    List<Entity> loadAreas(Random random) {
        LinkedList<Entity> areaList = new LinkedList();
        if (variables == null || !variables.containsKey("areas")) {
            System.err.println("No entry areas found in the variables.json");
            return areaList;
        }
        int missing = 0;
        JSONArray areas = (JSONArray) variables.get("areas");
        for (Object area : areas) {
            JSONObject jsonArea = (JSONObject) area;
            if (jsonArea.containsKey("diameters") && jsonArea.containsKey("noticeableDistance") && loadPose(jsonArea) != null)
                areaList.add(new Area(arena, new Random(random.nextInt()), (double) jsonArea.get("diameters"), (double) jsonArea.get("noticeableDistance"), loadPose(jsonArea)));
            else
                System.err.println("Could not load area " + (areaList.size() + missing++) + " correctly. Entry diameters, noticeableDistance or position missing.");
        }
        return areaList;
    }

    /**
     * Loads an file given by it's path as JSON file
     * Returns the key structure of that file as JSONObject
     *
     * @param filePath String
     * @return JSONObject
     */
    public static JSONObject loadJSON(String filePath) throws IOException {
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
            System.err.println("Could not read file from " + filePath + " Please check correct path.");
        }
        return object;
    }

    /**
     * Goes through any entry in the variables robots keys and turns it into an JSONObject which goes into the loadRobot function
     * Returns an List filled with any Robot from the current variables.json
     *
     * @param random Random
     * @param logger Logger
     * @return List<Entity>
     */
    List<Entity> loadRobots(Random random, Logger logger) {

        if (variables != null && variables.containsKey("robots")) {
            JSONArray robots = (JSONArray) variables.get("robots");
            if (robots.size() == 0) System.err.println("Zero robots in variables.json.");
            List<Entity> robotList = new LinkedList<>();
            robots.forEach(entry -> loadRobot(
                    (JSONObject) entry,
                    robotList,
                    random,
                    arena,
                    logger
                    , loadSimulatedTime()));
            return robotList;
        }
        System.err.println("No entry robots found in the variables.json");
        return new LinkedList<>();
    }

    /**
     * Loads one robot and adds them to the robotList
     *
     * @param robotObject    JSONObject
     * @param robotList      List<Entity>
     * @param random         Random
     * @param arena          Arena
     * @param logger         Logger
     * @param timeToSimulate int
     */
    private void loadRobot(
            JSONObject robotObject, List<Entity> robotList, Random random, Arena arena,
            Logger logger, int timeToSimulate) {
        if (robotObject.containsKey("engineR") && robotObject.containsKey("engineL") &&
                robotObject.containsKey("distance") && robotObject.containsKey("powerTransmission") &&
                robotObject.containsKey("diameters") && robotObject.containsKey("type") && loadPose(robotObject) != null) {
            RobotBuilder builder = new RobotBuilder()
                    .engineRight((Double) robotObject.get("engineR"))
                    .engineLeft((Double) robotObject.get("engineL"))
                    .engineDistnace((Double) robotObject.get("distance"))
                    .random(new Random(random.nextInt()))
                    .pose(loadPose(robotObject))
                    .ticsPerSimulatedSecond(loadTicsPerSimulatedSecond())
                    .minSpeed(loadMinSpeed())
                    .maxSpeed(loadMaxSpeed())
                    .timeToSimulate(timeToSimulate)
                    .arena(arena)
                    .powerTransmission((Double) robotObject.get("powerTransmission"))
                    .diameters((Double) robotObject.get("diameters"))
                    .logger(logger)
                    .simulateWithView(displayView);
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
            robotList.add(robot);
        } else
            System.err.println("Could not load robot " + robotList.size() + " correctly." +
                    "Entry engineR, engineL, distance, powerTransmission, diameters, type or position is missing.");
    }

    /**
     * Loads the Pose of an given
     *
     * @param object JSONObject
     * @return Pose
     */
    private Pose loadPose(JSONObject object) {
        if (!object.containsKey("position")) {
            System.err.println("Could not load position for " + object);
            return null;
        }
        JSONObject positionObject = (JSONObject) object.get("position");
        if (!positionObject.containsKey("x") || !positionObject.containsKey("y") || !positionObject.containsKey("rotation")) {
            System.err.println("Could not load x, y or rotation for " + object);
            return null;
        }
        return new Pose((Double) positionObject.get("x"), (Double) positionObject.get("y"),
                Math.toRadians((Double) positionObject.get("rotation")));
    }

    void setVariables(JSONObject variables) {
        this.variables = variables;
    }
}
