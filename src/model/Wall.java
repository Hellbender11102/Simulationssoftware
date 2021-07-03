package model;

import model.AbstractModel.BasePhysicalEntity;

import java.awt.*;
import java.util.Random;

public class Wall extends BasePhysicalEntity {
    final Pose pose;


    public Wall(Arena arena, Random random, double width, double height, Pose pose, int ticsPerSimulatedSecond) {
        super(arena, random, width, height, pose, ticsPerSimulatedSecond);
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

    /**
     * Calculates and sets the next position
     */
    public void setNextPosition() {
    }

}
