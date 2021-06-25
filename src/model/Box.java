package model;

import model.AbstractModel.BasePhysicalEntity;

import java.awt.*;
import java.util.Random;

//TODO
public class Box extends BasePhysicalEntity {
    public Box(Arena arena, Random random, double width, double height, Pose pose) {
        super(arena, random, width, height, pose);
    }

    @Override
    public double trajectorySpeed() {
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

    @Override
    public Position getClosestPositionInEntity(Position position) {
        Position edgeUL = new Position(pose.getX() - width / 2, pose.getY() + height / 2);
        Position edgeUR = new Position(pose.getX() + width / 2, pose.getY() + height / 2);
        Position edgeLL = new Position(pose.getX() - width / 2, pose.getY() - height / 2);
        Position edgeLR = new Position(pose.getX() + width / 2, pose.getY() - height / 2);
        return closestPositionInEntityForSquare(position, edgeUL, edgeUR, edgeLL, edgeLR);
    }

    @Override
    public int getTimeToSimulate() {
        return 0;
    }



    @Override
    public double getArea() {
        return getAreaSquare();
    }
}
