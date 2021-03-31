import controller.Controller;
import model.Arena;
import model.Position;
import model.Robot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

class Main {

    public static void main(String[] args) {
        Random random = new Random(1000);
        ConcurrentLinkedQueue<Robot> threadOutputQueue = new ConcurrentLinkedQueue<>();
        Map<Robot, Position> robotsAndPositionOffsets = new HashMap<>();
        Arena arena = new Arena(100,100);

        Position p1 = new Position(400, 400);
        Position p2 = new Position(400, 400);
        Position p3 = new Position(620, 400,90);

        Robot r1 = new Robot(60, 40, 2, p1, threadOutputQueue,random);
        Robot r2 = new Robot(2, 1, 2, p2, threadOutputQueue,random);
        Robot r3 = new Robot(3, 2, 2, p3, threadOutputQueue,random);

        robotsAndPositionOffsets.put(r1, new Position(0, 0));
        robotsAndPositionOffsets.put(r2, new Position(0, 0));
        robotsAndPositionOffsets.put(r3, new Position(0, 0));

        Controller controller = new Controller(threadOutputQueue, robotsAndPositionOffsets,arena,random);

        controller.startRobotThreads(Integer.MAX_VALUE);
        controller.visiualisationLoop(1);
    }
}