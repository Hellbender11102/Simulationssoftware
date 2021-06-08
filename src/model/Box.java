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
        return new Color(50,32,28);
    }

    @Override
    public boolean isPositionInEntity(Position position) {
        return false;
    }

    @Override
    public Position getClosestPositionInBody(Position position) {
        return null;
    }

    @Override
    public int getTimeToSimulate() {
        return 0;
    }

    @Override
    public boolean equals(PhysicalEntity robot) {
        return false;
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public boolean draw(Graphics g) {
        return false;
    }
}
