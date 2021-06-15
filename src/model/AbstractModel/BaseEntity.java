package model.AbstractModel;

import controller.Logger;
import model.Arena;
import model.Pose;
import model.Position;

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

    protected BaseEntity(Arena arena, Random random, double width, double height, Pose pose) {
        this.poseRingMemory = new Pose[ringMemorySize];
        this.pose = pose;
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
     *
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

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    protected Position closestPositionInEntityForSquare(Position position, Position edgeUL, Position edgeUR, Position edgeLL, Position edgeLR) {
        if (arena.isTorus) {
            position = getClosestPositionInTorus(position);
        }
        Position closest = Math.min(edgeUL.euclideanDistance(position), edgeUR.euclideanDistance(position)) <
                Math.min(edgeLL.euclideanDistance(position), edgeLR.euclideanDistance(position)) ?
                edgeUL.euclideanDistance(position) < edgeUR.euclideanDistance(position) ? edgeUL : edgeUR
                :
                edgeLL.euclideanDistance(position) < edgeLR.euclideanDistance(position) ? edgeLL : edgeLR;
        if (position.getXCoordinate() <= pose.getXCoordinate() + width / 2 &&
                position.getXCoordinate() >= pose.getXCoordinate() - width / 2) {
            closest.setXCoordinate(position.getXCoordinate());
        } else if (position.getYCoordinate() <= pose.getYCoordinate() + height / 2 &&
                position.getYCoordinate() >= pose.getYCoordinate() - height / 2) {
            closest.setYCoordinate(position.getYCoordinate());
        }
        return closest;
    }

    protected Position closestPositionInEntityForCircle(Position position, double radius) {
        if (arena.isTorus) {
            position = getClosestPositionInTorus(position);
        }
        return pose.getPositionInDirection(radius, pose.calcAngleForPosition(position));
    }

    /**
     * Checks if an Position outside of the Walls is closer due to the Torus Arena Transformation
     * @param position Position target
     * @return position
     */
    public Position getClosestPositionInTorus(Position position) {
        if (position.getXCoordinate() > arena.getWidth() / 2. && pose.getXCoordinate() < arena.getWidth() / 2.)
            position = pose.euclideanDistance(position.creatPositionByDecreasing(arena.getWidth(), 0))
                    < pose.euclideanDistance(position) ? position.creatPositionByDecreasing(arena.getWidth(), 0) :
                    position;
        else if (position.getXCoordinate() < arena.getWidth() / 2. && pose.getXCoordinate() > arena.getWidth() / 2.) {
            position = pose.euclideanDistance(position.creatPositionByDecreasing(-arena.getWidth(), 0))
                    < pose.euclideanDistance(position) ? position.creatPositionByDecreasing(-arena.getWidth(), 0) :
                    position;
        }  
        if (position.getYCoordinate() >  arena.getHeight() / 2. && pose.getYCoordinate() <  arena.getHeight()/ 2.)
            position = pose.euclideanDistance(position.creatPositionByDecreasing(arena.getWidth(), 0))
                    < pose.euclideanDistance(position) ? position.creatPositionByDecreasing(arena.getWidth(), 0) :
                    position;
        else if (position.getYCoordinate() < arena.getHeight() / 2. && pose.getYCoordinate() > arena.getHeight() / 2.) {
            position = pose.euclideanDistance(position.creatPositionByDecreasing(0, arena.getHeight()))
                    < pose.euclideanDistance(position) ? position.creatPositionByDecreasing(0, -arena.getHeight()) :
                    position;
        }
        return position;
    }


}
