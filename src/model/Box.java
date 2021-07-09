package model;

import model.AbstractModel.BasePhysicalEntity;

import java.awt.*;
import java.util.Random;

public class Box extends BasePhysicalEntity {
    public Box(Arena arena, Random random, double width, double height, Pose pose,int ticsPerSimulatedSecond) {
        super(arena, random, width, height, pose, ticsPerSimulatedSecond);
    }

    @Override
    public double getTrajectoryMagnitude() {
        return 0;
    }

    @Override
    public Color getClassColor() {
        return new Color(50, 32, 28);
    }

    @Override
    public boolean isPositionInEntity(Position position) {
        return isPositionInEntitySquare(position);
    }

    /**
     * Returns the closest position in the body of the box to the given position
     * @param position Position
     * @return Position
     */
    @Override
    public Position getClosestPositionInEntity(Position position) {
        return closestPositionInEntityForSquare(position);
    }

    @Override
    public double getArea() {
        return getAreaSquare();
    }
    @Override
    public String toString() {
        return "Box at " + pose + " width:" + width + " height:" + height;
    }
}
