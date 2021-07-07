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
    // determines the size of the ring memory
    int ringMemorySize = 10000;
    protected Pose[] poseRingMemory;
    protected Pose pose;
    protected Logger logger;
    protected final Arena arena;
    protected final Random random;
    protected double width, height;
    protected final Color color;
    //starts paused in order to have more control over the simulation
    protected boolean isPaused = true;

    protected BaseEntity(Arena arena, Random random, double width, double height, Pose pose) {
        this.poseRingMemory = new Pose[ringMemorySize];
        this.pose = pose;
        this.arena = arena;
        this.width = width < 0 ? 0 : width;
        this.height = height < 0 ? 0 : height;
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
     * Sets the pose of the entity to the latest pose saved in the ring memory
     */
    @Override
    public void setToLatestPose() {
        if (poseRingMemoryHead - 1 < poseRingMemory.length && 0 <= poseRingMemoryHead - 1)
            pose = poseRingMemory[poseRingMemoryHead - 1];
        poseRingMemoryPointer = 0;
    }

    /**
     * Saves the current position in the ring memory
     */
    @Override
    public void updatePositionMemory() {
        poseRingMemory[poseRingMemoryHead] = pose.clone();
        poseRingMemoryHead = (poseRingMemoryHead + 1) % (ringMemorySize - 1);
    }

    /**
     * Calculates the nearest Position to the given position inside of the entity's square shaped body
     *
     * @param position Position
     * @return Position
     */
    public Position closestPositionInEntityForSquare(Position position) {

        if (isPositionInEntitySquare(position)) return position;
        if (arena.isTorus) {
            position = arena.getClosestPositionInTorus(pose, position);
        }

        Position closest = pose.clone();

        if (position.getX() <= pose.getX() + width / 2 && position.getX() >= pose.getX() - width / 2) {
            closest.setX(position.getX());
        }else if(position.getX() > pose.getX() + width / 2){
            closest.setX(pose.getX()+width /2);
        } else if(position.getX() < pose.getX() + width / 2){
            closest.setX(pose.getX()-width /2);
        }

        if (position.getY() <= pose.getY() + height / 2 && position.getY() >= pose.getY() - height / 2) {
            closest.setY(position.getY());
        }else if(position.getY() > pose.getY() + height / 2){
            closest.setY(pose.getY()+height /2);
        } else if(position.getY() < pose.getY() + height / 2){
            closest.setY(pose.getY()-height /2);
        }
        return closest;
    }

    /**
     * Calculates the nearest Position to the given position inside of the entity's circle shaped body
     *
     * @param position position
     * @param radius   radius
     * @return Position
     */
    public Position closestPositionInEntityForCircle(Position position, double radius) {
        if (arena.isTorus) {
            position = arena.getClosestPositionInTorus(pose, position);
        }
        return pose.getPositionInDirection(radius, pose.getAngleToPosition(position));
    }

    /**
     * Returns true if the position is inside the body of an square
     *
     * @param position Position
     * @return boolean
     */
    public boolean isPositionInEntitySquare(Position position) {
        if (arena.isTorus) {
            position = arena.setPositionInBoundsTorus(position);
        }
        return position.getX() <= pose.getX() + width / 2. &&
                position.getX() >= pose.getX() - width / 2. &&
                position.getY() <= pose.getY() + height / 2. &&
                position.getY() >= pose.getY() - height / 2.;
    }

    /**
     * Returns true if the position is inside the body of an circle
     *
     * @param position Position
     * @return boolean
     */
    public boolean isPositionInEntityCircle(Position position) {
        if (arena.isTorus) {
            position = arena.setPositionInBoundsTorus(position);
        }
        return pose.getEuclideanDistance(position) <= width / 2.;
    }


    //setter & getter

    @Override
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
                && entity.isCollidable() == isCollidable() && entity.getClass().equals(getClass());
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

    /**
     * Calculates the area of square
     *
     * @return double
     */
    public double getAreaSquare() {
        return width * height;
    }

    /**
     * Calculates the area of circular
     *
     * @return double
     */
    public double getAreaCircle() {
        return Math.PI * Math.pow(width / 2, 2);
    }

    @Override
    public String toString() {
        return "Base entity with " + pose + " width:" + width + " height:" + height;
    }
}
