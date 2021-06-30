package model;

import model.AbstractModel.BasePhysicalEntity;

import java.awt.*;
import java.util.Random;

public class Wall extends BasePhysicalEntity {
    final Pose pose;


    public Wall(Arena arena, Random random, double width, double height, Pose pose) {
        super(arena, random, width, height, pose);
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
    public boolean isMovable() {
        return false;
    }
    @Override
    public double getArea() {
        return getAreaSquare();
    }
}
