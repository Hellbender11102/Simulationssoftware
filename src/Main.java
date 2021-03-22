import controller.Controller;
import model.Position;
import model.Robot;
import view.View;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

class Main {

    public static void main(String[] args) {
        Random random = new Random(1000);
        ConcurrentLinkedQueue<Robot> threadOutputQueue = new ConcurrentLinkedQueue<>();
        LinkedList<Robot> pos = new LinkedList<>();
        Map<Robot, Position> robotsAndPositionOffsets = new HashMap<>();

        Position p1 = new Position(400, 400);
        Position p2 = new Position(400, 400);
        Position p3 = new Position(620, 400,90);

        Robot r1 = new Robot(1, 2, 2, p1, threadOutputQueue,random);
        Robot r2 = new Robot(2, 1, 2, p2, threadOutputQueue,random);
        Robot r3 = new Robot(3, 2, 2, p3, threadOutputQueue,random);

        robotsAndPositionOffsets.put(r1, new Position(0, 0));
        robotsAndPositionOffsets.put(r2, new Position(0, 0));
        robotsAndPositionOffsets.put(r3, new Position(0, 0));

        pos.add(r1);
        pos.add(r2);
        pos.add(r3);

        Controller controller = new Controller(threadOutputQueue, robotsAndPositionOffsets,random);

        controller.startRobotThreads(820);
        controller.visiualisationLoop();
    }
}