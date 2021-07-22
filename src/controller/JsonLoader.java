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
    private final Logger errorLogger;
    private Arena arena;
    private boolean displayView = false;
    private final String pathSettings = "resources/settings.json";
    private String pathVariables = "resources/variables.json";
    String error;

    /**
     * Constructor
     * Loads default path for settings and variables
     */
    JsonLoader(Logger errorLogger) {
        this.errorLogger = errorLogger;
        {
            try {
                settings = loadJSON(pathSettings, errorLogger);
                variables = loadJSON(pathVariables, errorLogger);
                initArena();
            } catch (IOException e) {
                error = "Could not load settings.json and variables.json.";
                errorLogger.dumpError(error);
                errorLogger.dumpError(e.getMessage());
            }
        }
    }

    public void setPathVariables(String pathVariables) {
        this.pathVariables = pathVariables;
    }

    /**
     * Loads the arena
     * @return Arena
     */
    Arena initArena() {
        if (variables != null) {
            JSONObject arenaObj = (JSONObject) variables.get("arena");
            arena = Arena.getInstance((int) (long) arenaObj.get("width"), (int) (long) arenaObj.get("height"), (boolean) arenaObj.get("torus"));
        } else {
            error = "Could not read arena from variables.json.";

            errorLogger.dumpError(error);
            arena = Arena.getInstance(0, 0, false);
        }
        return arena;
    }

    /**
     * Reloads the arena and overwrites its singleton instance
     * @return Arena
     */
    Arena reloadArena() {
        if (variables != null && variables.containsKey("arena")) {
            JSONObject arenaObj = (JSONObject) variables.get("arena");
            arena = Arena.overWriteInstance((int) (long) arenaObj.get("width"), (int) (long) arenaObj.get("height"), (boolean) arenaObj.get("torus"));
        } else {
            error = "Could not read arena from variables.json.";
            errorLogger.dumpError(error);
        }
        return arena;
    }

    /**
     * Reads the seed from the current variables json object
     * Returns a random with given seed else random without seed
     * @return Random
     */
    Random loadRandom() {
        if (variables != null && variables.containsKey("seed"))
            return new Random((long) variables.get("seed"));
        else {
            System.out.println("No seed is used.");
            return new Random();
        }
    }

    /**
     *  Returns the max speed value of the current variables json object
     * @return double
     */
    double loadMaxSpeed() {
        if (variables != null && variables.containsKey("maxSpeed"))
            return (double) variables.get("maxSpeed");
        else {
            error = "Could not read maxSpeed from variables.json.";
            errorLogger.dumpError(error);
            return 8.;
        }
    }

    /**
     * Returns the min speed value of the current variables json object
     * @return double
     */
    double loadMinSpeed() {
        if (variables != null && variables.containsKey("minSpeed"))
            return (double) variables.get("minSpeed");
        else {
            error = "Could not read minSpeed from variables.json.";

            errorLogger.dumpError(error);
            return 0.;
        }
    }

    /**
     * Loads the fps value of the current settings json object
     * @return int
     */
    int loadFps() {
        if (settings != null && settings.containsKey("fps"))
            return (int) (long) settings.get("fps");
        else {
            error = "Could not read fps from settings.json.";
            errorLogger.dumpError(error);
            return 10;
        }
    }

    /**
     * Returns the simulate-seconds value of the current settings json object
     * @return int
     */
    int loadSimulatedTime() {
        if (settings != null && settings.containsKey("simulate-seconds")) {
            return (int) (long) settings.get("simulate-seconds");
        } else {
            error = "Could not read simulated-seconds from settings.json.";

            errorLogger.dumpError(error);
            return 0;
        }
    }

    /**
     * Returns the display-view value of the current settings json object
     * @return boolean
     */
    boolean loadDisplayView() {
        if (settings != null && settings.containsKey("display-view")) {
            displayView = (boolean) settings.get("display-view");
            return displayView;
        } else {
            error = "Could not read display-view from settings.json.";

            errorLogger.dumpError(error);
            return true;
        }
    }

    /**
     * Returns the current ticsPerSimulatedSecond value of the variables json object
     * @return int
     */
    int loadTicsPerSimulatedSecond() {
        if (variables != null && variables.containsKey("ticsPerSimulatedSecond"))
            return (int) (long) variables.get("ticsPerSimulatedSecond");
        else {
            error = "Could not read variables or entry ticsPerSimulatedSecond from variables.json.";

            errorLogger.dumpError(error);
        }
        return 1;
    }

    /**
     * Loads all boxes from the variables json object
     * Returns a list of all boxes
     * @param random Random
     * @return List<Entity>
     */
    List<Entity> loadBoxes(Random random) {
        LinkedList<Entity> boxList = new LinkedList();
        if (variables == null || !variables.containsKey("boxes")) {
            error = "No entry boxes found in the variables.json";

            errorLogger.dumpError(error);
            return boxList;
        }
        int missing = 0;
        JSONArray boxes = (JSONArray) variables.get("boxes");
        for (Object box : boxes) {
            JSONObject jsonBox = (JSONObject) box;
            if (jsonBox.containsKey("width") && jsonBox.containsKey("position") && jsonBox.containsKey("height") &&
                    loadPose(jsonBox,false) != null)
                boxList.add(
                        new Box(arena,
                                new Random(random.nextInt()),
                                (double) jsonBox.get("width"),
                                (double) jsonBox.get("height"),
                                displayView,
                                loadPose(jsonBox,false),
                                loadTicsPerSimulatedSecond()));
            else {
                error = "Could not load box entry " + (boxList.size() + missing++) + " correctly. Entry width, height or position missing.";

                errorLogger.dumpError(error);
            }
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
            error = "No entry walls found in the variables.json";

            errorLogger.dumpError(error);
            return wallList;
        }
        int missing = 0;
        JSONArray walls = (JSONArray) variables.get("walls");
        for (Object wall : walls) {
            JSONObject jsonWall = (JSONObject) wall;
            if (jsonWall.containsKey("width") && jsonWall.containsKey("position") &&
                    jsonWall.containsKey("height") && loadPose(jsonWall,false) != null)
                wallList.add(new Wall(arena, new Random(random.nextInt()),
                        (double) jsonWall.get("width"),
                        (double) jsonWall.get("height"),
                        displayView,
                        loadPose(jsonWall,false), loadTicsPerSimulatedSecond()));
            else {
                error = "Could not load wall " + (wallList.size() + missing++) + " correctly. Entry width, height or position missing.";
                errorLogger.dumpError(error);
            }
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
            error = "No entry areas found in the variables.json";

            errorLogger.dumpError(error);
            return areaList;
        }
        int missing = 0;
        JSONArray areas = (JSONArray) variables.get("areas");
        for (Object area : areas) {
            JSONObject jsonArea = (JSONObject) area;
            if (jsonArea.containsKey("diameters") && jsonArea.containsKey("noticeableDistanceDiameters") && loadPose(jsonArea,false) != null)
                areaList.add(new Area(arena, new Random(random.nextInt()),
                        (double) jsonArea.get("diameters"),
                        (double) jsonArea.get("noticeableDistanceDiameters"),
                        loadPose(jsonArea,false)));
            else {
                error = "Could not load area " + (areaList.size() + missing++) + " correctly. Entry diameters, noticeableDistance or position missing.";

                errorLogger.dumpError(error);
            }
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
    public static JSONObject loadJSON(String filePath, Logger errorLogger) throws IOException {
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
            String error = "Could not read file from " + filePath + " Please check correct path.";

            errorLogger.dumpError(error);
            errorLogger.dumpError(e.getMessage());
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
            if (robots.size() == 0) {
                error = "Zero robots in variables.json.";
                errorLogger.dumpError(error);
            }
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
        error = "No entry \"robots\" was found";
        errorLogger.dumpError(error);
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
                robotObject.containsKey("diameters") && robotObject.containsKey("type") && loadPose(robotObject,true) != null) {
            RobotBuilder builder = new RobotBuilder()
                    .engineRight((Double) robotObject.get("engineR"))
                    .engineLeft((Double) robotObject.get("engineL"))
                    .engineDistance((Double) robotObject.get("distance"))
                    .random(new Random(random.nextInt()))
                    .pose(loadPose(robotObject,true))
                    .ticsPerSimulatedSecond(loadTicsPerSimulatedSecond())
                    .minSpeed(loadMinSpeed())
                    .maxSpeed(loadMaxSpeed())
                    .timeToSimulate(timeToSimulate)
                    .arena(arena)
                    .powerTransmission((Double) robotObject.get("powerTransmission"))
                    .diameters((Double) robotObject.get("diameters"))
                    .logger(logger)
                    .simulateWithView(displayView);
            //if visionangle and visionrange exist
            if (robotObject.containsKey("visionRange") && robotObject.containsKey("visionAngle")) {
                builder = builder.visionAngle((double) robotObject.get("visionAngle")).visionRange((Double) robotObject.get("visionRange"));
            }
            RobotInterface robot;
            // Implement new robot classes
            switch ((String) robotObject.get("type")) {
                case "sheep":
                    robot = builder.buildSheep();
                    break;
                case "dog":
                    robot = builder.buildDog();
                    break;
                case "vision":
                    robot = builder.buildVisionCone();
                    break;
                /* Space to add own robot types */

                /* ----------------------------- */
                default:
                    robot = builder.buildDefault();
            }
            robotList.add(robot);
        } else {
            error = "Could not load robot " + robotList.size() + " correctly." +
                    "Entry engineR, engineL, distance, powerTransmission, diameters, type or position is missing.";
            errorLogger.dumpError(error);
        }
    }

    /**
     * Loads the Pose of an given JSONObject
     *
     * @param object JSONObject
     * @return Pose
     */
    private Pose loadPose(JSONObject object, boolean loadRotation) {
        if (!object.containsKey("position")) {
            error = "Could not load position for " + object;
            errorLogger.dumpError(error);
            return null;
        }
        JSONObject positionObject = (JSONObject) object.get("position");
        if (!positionObject.containsKey("x") || !positionObject.containsKey("y") || (loadRotation && !positionObject.containsKey("rotation"))) {
            error = "Could not load x, y or rotation for " + object;
            errorLogger.dumpError(error);
            return null;
        }
        if (loadRotation)
            return new Pose((Double) positionObject.get("x"), (Double) positionObject.get("y"),
                    Math.toRadians((Double) positionObject.get("rotation")));
        else return new Pose((Double) positionObject.get("x"), (Double) positionObject.get("y"), 0);
    }

    void setVariables(JSONObject variables) {
        this.variables = variables;
    }

    /**
     * Reloads the JSObjects from the current file paths
     */
    void reload() {
        try {
            settings = loadJSON(pathSettings, errorLogger);
            variables = loadJSON(pathVariables, errorLogger);
        } catch (IOException e) {
            error = "The JSON files couldn't be read.";
            errorLogger.dumpError(error);
        }
    }
}
