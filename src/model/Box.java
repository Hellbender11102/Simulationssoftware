package model;

import model.AbstractModel.BasePhysicalEntity;
import model.AbstractModel.PhysicalEntity;

import java.awt.*;
import java.util.Random;

//TODO
public class Box extends BasePhysicalEntity {
    protected Box(Arena arena, Random random, double width, double height) {
        super(arena, random, width, height);
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
        return position.getXCoordinate() <= pose.getXCoordinate() + height / 2 &&
                position.getXCoordinate() >= pose.getXCoordinate() - height / 2 &&
                position.getYCoordinate() <= pose.getYCoordinate() + width / 2 &&
                position.getYCoordinate() >= pose.getYCoordinate() - width / 2;
    }

    @Override
    public Position getClosestPositionInBody(Position position) {
        return null;
    }

    @Override
    public int getTimeToSimulate() {
        return 0;
    }

}
