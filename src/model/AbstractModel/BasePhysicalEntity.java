package model.AbstractModel;

import controller.Logger;
import model.Arena;
import model.Pose;
import model.Position;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

abstract public class BasePhysicalEntity extends Thread implements PhysicalEntity {
    protected int poseRingMemoryHead = 0;
    protected int poseRingMemoryPointer = 0;
    private int ringMemorySize = 10000;
    protected Pose[] poseRingMemory;
    protected Pose pose;
    protected Logger logger;
    protected final Arena arena;
    protected final Random random;
    private double width, height;

    protected BasePhysicalEntity(Arena arena, Random random, double width, double height) {
        this.poseRingMemory = new Pose[ringMemorySize];
        this.arena = arena;
        this.width = width;
        this.height = height;
        this.random = random;
    }

    /**
     * Checks if robots are in the arena bounds
     */
    @Override
    public boolean inArenaBounds() {
        if (getPose().getXCoordinate() < width / 2)
            return false;
        else if (getPose().getXCoordinate() > arena.getWidth() - width / 2)
            return false;
        if (getPose().getYCoordinate() < height / 2)
            return false;
        else return !(getPose().getYCoordinate() > arena.getHeight() - height / 2);
    }

    /**
     * Checks if robots are in the arena bounds
     */
    @Override
    public void setInArenaBounds() {
        if (pose.getXCoordinate() < width / 2)
            pose.setXCoordinate(width / 2);
        else if (pose.getXCoordinate() > arena.getWidth() - width / 2)
            pose.setXCoordinate(arena.getWidth() - width / 2);
        if (pose.getYCoordinate() < height / 2)
            pose.setYCoordinate(height / 2);
        else if (pose.getYCoordinate() > arena.getHeight() - height / 2)
            pose.setYCoordinate(arena.getHeight() - height / 2);
    }

    @Override
    public void collisionDetection() {
        if (!inArenaBounds()) {
            setInArenaBounds();
        }
        for (PhysicalEntity physicalEntity : isCollidingWith()) {
            recursiveCollision(physicalEntity);
        }
    }

    public void recursiveCollision(PhysicalEntity physicalEntity) {
        if (!physicalEntity.inArenaBounds()) {
            setInArenaBounds();
        }
        if (physicalEntity.isMovable()) {
            //r2 gets bumped
            if (physicalEntity.isPositionInEntity(pose.getPositionInDirection(getClosestPositionInBody(physicalEntity.getPose()).euclideanDistance(pose)))) {
                bump(this, physicalEntity, pose.getPositionInDirection(trajectorySpeed()));
            } else if (isPositionInEntity(physicalEntity.getPose().getPositionInDirection(physicalEntity.getClosestPositionInBody(pose).euclideanDistance(physicalEntity.getPose())))) {   //this gets pumped
                bump(physicalEntity, this, physicalEntity.getPose().getPositionInDirection(physicalEntity.trajectorySpeed()));
            } else {
                //both are bumping cause no one drives directly in each other
                if (pose.getXCoordinate() < physicalEntity.getPose().getXCoordinate()) {
                    bump(this, physicalEntity, new Position(pose.getXCoordinate() + trajectorySpeed(), pose.getYCoordinate()));
                    bump(physicalEntity, this, new Position(physicalEntity.getPose().getXCoordinate() - physicalEntity.trajectorySpeed(), physicalEntity.getPose().getYCoordinate()));
                } else {
                    bump(this, physicalEntity, new Position(pose.getXCoordinate() - trajectorySpeed(), pose.getYCoordinate()));
                    bump(physicalEntity, this, new Position(physicalEntity.getPose().getXCoordinate() + physicalEntity.trajectorySpeed(), physicalEntity.getPose().getYCoordinate()));
                }
                if (pose.getYCoordinate() < physicalEntity.getPose().getYCoordinate()) {
                    bump(this, physicalEntity, new Position(pose.getXCoordinate(), pose.getYCoordinate() + trajectorySpeed()));
                    bump(physicalEntity, this, new Position(physicalEntity.getPose().getXCoordinate(), physicalEntity.getPose().getYCoordinate() - physicalEntity.trajectorySpeed()));
                } else {
                    bump(this, physicalEntity, new Position(pose.getXCoordinate(), pose.getYCoordinate() - trajectorySpeed()));
                    bump(physicalEntity, this, new Position(physicalEntity.getPose().getXCoordinate(), physicalEntity.getPose().getYCoordinate() + physicalEntity.trajectorySpeed()));
                }
            }
        }
    }

    /**
     * @param bumping                 Robot that bumps
     * @param getsBumped              Robot that gets bumped
     * @param positionInBumpDirection Position in which the bump directs
     */
    private void bump(PhysicalEntity bumping, PhysicalEntity getsBumped, Position positionInBumpDirection) {
        Position vector = bumping.getPose().creatPositionByDecreasing(positionInBumpDirection);
        getsBumped.getPose().decPosition(vector);

        if (getPose().getXCoordinate() < width / 2)
            bumping.getPose().incPosition(vector.getXCoordinate(), 0);
        else if (getPose().getXCoordinate() > arena.getWidth() - width / 2)
            bumping.getPose().incPosition(vector.getXCoordinate(), 0);
        if (getPose().getYCoordinate() < height / 2)
            bumping.getPose().incPosition(0, vector.getYCoordinate());
        else if (getPose().getYCoordinate() > arena.getHeight() - height / 2)
            bumping.getPose().incPosition(0, vector.getYCoordinate());
    }

    public LinkedList<PhysicalEntity> isCollidingWith() {
        LinkedList<PhysicalEntity> physicalEntities = new LinkedList<>();
        for (PhysicalEntity physicalEntity : arena.getPhysicalEntityList()) {
            if (isPositionInEntity(physicalEntity.getClosestPositionInBody(pose)))
                physicalEntities.add(physicalEntity);
        }
        return physicalEntities;
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
    public boolean isMovable() {
        return true;
    }

    @Override
    public Random getRandom() {
        return random;
    }


}
