package model;

import model.AbstractModel.BasePhysicalEntity;
import model.AbstractModel.PhysicalEntity;

import java.awt.*;
import java.util.Random;

//TODO
public class Wall extends BasePhysicalEntity {
    protected Wall(Arena arena, double width, double height) {
        super(arena, null, width, height);
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
    public boolean isMovable() {
        return false;
    }

    @Override
    public boolean draw(Graphics g) {
        return false;
    }

    @Override
    public double trajectorySpeed() {
        return 0;
    }

    @Override
    public Color getClassColor() {
        return null;
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
}
