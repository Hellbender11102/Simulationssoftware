package model;

import model.AbstractModel.BasePhysicalEntity;

import java.awt.*;
import java.util.Random;

//TODO, passing through sometimes
public class Wall extends BasePhysicalEntity {
    final public Position edgeUL, edgeUR, edgeLL, edgeLR;

    public Wall(Arena arena, Random random, double width, double height, Pose pose) {
        super(arena, random, width, height, pose);
        edgeUL = new Position(pose.getX() - width / 2, pose.getY() + height / 2);
        edgeUR = new Position(pose.getX() + width / 2, pose.getY() + height / 2);
        edgeLL = new Position(pose.getX() - width / 2, pose.getY() - height / 2);
        edgeLR = new Position(pose.getX() + width / 2, pose.getY() - height / 2);
    }

    @Override
    public double trajectorySpeed() {
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
       return closestPositionInEntityForSquare(position,edgeUL,edgeUR,edgeLL,edgeLR);
    }

    @Override
    public int getTimeToSimulate() {
        return 0;
    }

    @Override
    public boolean isMovable() {
        return false;
    }

}
