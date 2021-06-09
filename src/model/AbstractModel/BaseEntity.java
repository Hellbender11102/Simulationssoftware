package model.AbstractModel;

import controller.Logger;
import model.Arena;
import model.Pose;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

abstract public class BaseEntity extends Thread implements Entity {
    protected int poseRingMemoryHead = 0;
    protected int poseRingMemoryPointer = 0;
    int ringMemorySize = 10000;
    protected Pose[] poseRingMemory;
    protected Pose pose;
    protected Logger logger;
    protected final Arena arena;
    protected boolean isPaused = true;
    protected final Random random;
    protected double width, height;
    protected final Color color;

    protected BaseEntity(Arena arena, Random random, double width, double height) {
        this.poseRingMemory = new Pose[ringMemorySize];
        this.arena = arena;
        this.width = width;
        this.height = height;
        this.random = random;
        this.color = new Color(random.nextInt());
    }



    // Ring memory logic

    /**
     * Reads the poseRingMemory and orders it correctly in a list
     * from list entry zero current position
     * to list entry ringMemorySize position furthest back in time
     * @return List<Pose>
     */
    protected List<Pose> getPosesFromMemory() {
        List<Pose> poseList = new LinkedList<>();
        for (int i = poseRingMemoryHead - 1; 0 <= i; i--) {
            if (poseRingMemory[i] != null) poseList.add(poseRingMemory[i]);
        }
        for (int i = ringMemorySize - 1; poseRingMemoryHead < i; i--) {
            if (poseRingMemory[i] != null) poseList.add(poseRingMemory[i]);
        }
        return poseList;
    }

    /**
     * Sets the pose to the previous pose saved in the memory
     */
    @Override
    public void setPrevPose() {
        List<Pose> positions = getPosesFromMemory();
        if (poseRingMemoryPointer < positions.size())
            pose = positions.get(poseRingMemoryPointer);
        poseRingMemoryPointer += poseRingMemoryPointer < positions.size() - 1 ? 1 : 0;
    }

    /**
     * Sets the pose to the next pose saved in the memory
     */
    @Override
    public void setNextPose() {
        List<Pose> positions = getPosesFromMemory();
        if (0 < poseRingMemoryPointer) {
            if (poseRingMemoryPointer < positions.size())
                pose = positions.get(poseRingMemoryPointer);
            poseRingMemoryPointer -= 1;
        }
    }

    /**
     * Sets the pose of the entity to the latest pose saved in the memory
     */
    @Override
    public void setToLatestPose() {
        if (poseRingMemoryHead - 1 < poseRingMemory.length && 0 <= poseRingMemoryHead - 1)
            pose = poseRingMemory[poseRingMemoryHead - 1];
        poseRingMemoryPointer = 0;
    }

    @Override
    public void updatePositionMemory() {
        poseRingMemory[poseRingMemoryHead] = pose.clone();
        poseRingMemoryHead = (poseRingMemoryHead + 1) % (ringMemorySize - 1);
    }

    //setter & getter

    /**
     * @return Pose
     */
    @Override
    synchronized
    public Pose getPose() {
        return pose;
    }

    @Override
    public Random getRandom() {
        return random;
    }

    public void togglePause() {
        isPaused = !isPaused;
    }

    public boolean getPaused() {
        return isPaused;
    }

        public boolean equals(Entity entity) {
        return pose.equals(entity.getPose()) && color == entity.getColor()
                && entity.hasAnBody() == hasAnBody() && entity.getClass().equals(getClass());
    }

    public Color getColor() {
        return color;
    }
}
