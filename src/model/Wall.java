package model;

import model.abstractModel.BasePhysicalEntity;

import java.awt.*;
import java.util.Random;

public class Wall extends BasePhysicalEntity {
    final Pose pose;


    public Wall(Arena arena, Random random, double width, double height, boolean simulateWithView, Pose pose, int ticsPerSimulatedSecond) {
        super(arena, random, width, height, simulateWithView, pose, ticsPerSimulatedSecond);
        this.pose = pose;
    }

    @Override
    public double getTrajectoryMagnitude() {
        return 0;
    }

    @Override
    public Color getClassColor() {
        return new Color(55, 55, 55);
    }

    @Override
    public boolean isPositionInEntity(Position position) {
        return isPositionInEntitySquare(position);
    }

    @Override
    public Position getClosestPositionInEntity(Position position) {
        return closestPositionInEntityForSquare(position);
    }

    @Override
    public double getArea() {
        return getAreaSquare();
    }

    @Override
    public double getWeight() {
        return getAreaSquare();
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    /**
     * Calculates and sets the next position
     */
    public void setNextPosition() {
    }

    @Override
    public String toString() {
        return "Wall at " + pose + " width:" + width + " height:" + height;
    }
}
